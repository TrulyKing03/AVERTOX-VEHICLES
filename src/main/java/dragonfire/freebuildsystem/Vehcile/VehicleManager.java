package dragonfire.freebuildsystem.Vehcile;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.vehicle.VehicleDestroyEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public class VehicleManager implements Listener {
    private final JavaPlugin plugin;
    private final WalletService walletService;

    private final Map<String, Vehicle> vehiclesById;
    private final Map<UUID, Vehicle> vehiclesByEntityId;

    private final HorseVehicleConfig horseConfig;
    private final List<BoatVehicleConfig> boatConfigs;

    public VehicleManager(JavaPlugin plugin, WalletService walletService) {
        this.plugin = plugin;
        this.walletService = walletService;
        this.vehiclesById = new HashMap<>();
        this.vehiclesByEntityId = new HashMap<>();

        this.horseConfig = loadHorseVehicleConfig();
        this.boatConfigs = loadBoatVehicleConfigs();

        if (plugin.getConfig().getBoolean("remove-vanilla-boat-recipes", true)) {
            removeBoatRecipes();
        }
    }

    public void register() {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    public void shutdown() {
        for (Vehicle vehicle : vehiclesById.values()) {
            despawnVehicle(vehicle);
        }
        vehiclesById.clear();
        vehiclesByEntityId.clear();
    }

    public HorseVehicleConfig getHorseConfig() {
        return horseConfig;
    }

    public List<BoatVehicleConfig> getBoatConfigs() {
        return Collections.unmodifiableList(boatConfigs);
    }

    public WalletService getWalletService() {
        return walletService;
    }

    public Vehicle buildHorseVehicle() {
        List<String> lore = new ArrayList<>();
        lore.add("&7Type: &fHorse");
        lore.add("&7Speed: &f" + horseConfig.speed());
        lore.add("&8Right-click: spawn");
        lore.add("&8Left-click: despawn");

        return new Horse(
                horseConfig.item(),
                horseConfig.displayName(),
                lore,
                horseConfig.speed(),
                plugin,
                horseConfig.armor(),
                horseConfig.color(),
                horseConfig.style(),
                horseConfig.glowing()
        );
    }

    public Vehicle buildBoatVehicle(BoatVehicleConfig boatConfig) {
        List<String> lore = new ArrayList<>();
        lore.add("&7Type: &f" + prettyName(boatConfig.boatType().name()));
        lore.add("&7Speed: &f" + boatConfig.speed());
        lore.add("&8Right-click: spawn");
        lore.add("&8Left-click: despawn");

        return new Boat(
                boatConfig.item(),
                boatConfig.displayName(),
                lore,
                boatConfig.speed(),
                plugin,
                boatConfig.boatType()
        );
    }

    public void registerOwnedVehicle(Vehicle vehicle) {
        vehiclesById.put(vehicle.getVehicleId(), vehicle);
    }

    public Vehicle getVehicleByItem(ItemStack itemStack) {
        String vehicleId = Vehicle.getVehicleIdFromItem(itemStack, plugin);
        if (vehicleId == null) {
            return null;
        }
        return vehiclesById.get(vehicleId);
    }

    public void spawnVehicle(Vehicle vehicle, Location location) {
        Entity entity = vehicle.spawn(location);
        if (entity != null) {
            vehiclesByEntityId.put(entity.getUniqueId(), vehicle);
        }
    }

    public void despawnVehicle(Vehicle vehicle) {
        Entity entity = vehicle.getEntity();
        if (entity != null) {
            vehiclesByEntityId.remove(entity.getUniqueId());
        }
        vehicle.remove();
    }

    @EventHandler
    public void onVehicleDamage(EntityDamageEvent event) {
        if (vehiclesByEntityId.containsKey(event.getEntity().getUniqueId())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onVehicleDestroy(VehicleDestroyEvent event) {
        Vehicle vehicle = vehiclesByEntityId.get(event.getVehicle().getUniqueId());
        if (vehicle == null) {
            return;
        }

        event.setCancelled(true);
    }

    @EventHandler
    public void onDropVehicleToken(PlayerDropItemEvent event) {
        if (getVehicleByItem(event.getItemDrop().getItemStack()) != null) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(colorize("&cVehicle tokens cannot be dropped."));
        }
    }

    @EventHandler
    public void onVehicleTokenUse(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) {
            return;
        }
        if (event.getItem() == null) {
            return;
        }
        if (event.getAction() != Action.RIGHT_CLICK_AIR
                && event.getAction() != Action.RIGHT_CLICK_BLOCK
                && event.getAction() != Action.LEFT_CLICK_AIR
                && event.getAction() != Action.LEFT_CLICK_BLOCK) {
            return;
        }

        Vehicle vehicle = getVehicleByItem(event.getItem());
        if (vehicle == null) {
            return;
        }

        Player player = event.getPlayer();

        if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {
            if (!vehicle.isSpawned()) {
                player.sendMessage(colorize("&7Your vehicle is not currently spawned."));
            } else {
                despawnVehicle(vehicle);
                player.sendMessage(colorize("&aVehicle despawned."));
            }
            event.setCancelled(true);
            return;
        }

        if (vehicle.isSpawned()) {
            player.sendMessage(colorize("&eThis vehicle is already spawned."));
            event.setCancelled(true);
            return;
        }

        spawnVehicle(vehicle, player.getLocation());
        player.sendMessage(colorize("&aVehicle spawned."));
        event.setCancelled(true);
    }

    private HorseVehicleConfig loadHorseVehicleConfig() {
        ConfigurationSection horseSection = plugin.getConfig().getConfigurationSection("vehicles.horse");

        Material item = readMaterial(horseSection, "item", Material.HORSE_SPAWN_EGG);
        String displayName = readString(horseSection, "display-name", "&6Royal Horse");
        int price = readInt(horseSection, "price", 10000);
        double speed = readDouble(horseSection, "speed", 0.225D);
        Material armor = readMaterial(horseSection, "armor", Material.AIR);
        org.bukkit.entity.Horse.Color color = readHorseColor(readString(horseSection, "color", "BLACK"));
        org.bukkit.entity.Horse.Style style = readHorseStyle(readString(horseSection, "style", "WHITE_DOTS"));
        boolean glowing = readBoolean(horseSection, "glowing", false);

        return new HorseVehicleConfig(displayName, item, price, speed, armor, color, style, glowing);
    }

    private List<BoatVehicleConfig> loadBoatVehicleConfigs() {
        List<BoatVehicleConfig> configs = new ArrayList<>();
        ConfigurationSection boatsSection = plugin.getConfig().getConfigurationSection("vehicles.boats");

        if (boatsSection != null) {
            for (String key : boatsSection.getKeys(false)) {
                ConfigurationSection section = boatsSection.getConfigurationSection(key);
                if (section == null || !section.getBoolean("enabled", true)) {
                    continue;
                }

                org.bukkit.entity.Boat.Type type = readBoatType(readString(section, "type", key), org.bukkit.entity.Boat.Type.OAK);
                Material item = readMaterial(section, "item", defaultBoatMaterial(type));
                String displayName = readString(section, "display-name", "&b" + prettyName(type.name()) + " Boat");
                int price = readInt(section, "price", 6000);
                double speed = readDouble(section, "speed", 0.40D);

                configs.add(new BoatVehicleConfig(displayName, item, type, price, speed));
            }
        }

        if (configs.isEmpty()) {
            configs.add(new BoatVehicleConfig("&bOak Boat", Material.OAK_BOAT, org.bukkit.entity.Boat.Type.OAK, 6000, 0.40D));
            configs.add(new BoatVehicleConfig("&bSpruce Boat", Material.SPRUCE_BOAT, org.bukkit.entity.Boat.Type.SPRUCE, 6200, 0.40D));
            configs.add(new BoatVehicleConfig("&bBirch Boat", Material.BIRCH_BOAT, org.bukkit.entity.Boat.Type.BIRCH, 6200, 0.40D));
        }

        return configs;
    }

    private Material defaultBoatMaterial(org.bukkit.entity.Boat.Type type) {
        Material material = Material.matchMaterial(type.name() + "_BOAT");
        return material == null ? Material.OAK_BOAT : material;
    }

    private org.bukkit.entity.Boat.Type readBoatType(String value, org.bukkit.entity.Boat.Type fallback) {
        if (value == null || value.isBlank()) {
            return fallback;
        }
        try {
            return org.bukkit.entity.Boat.Type.valueOf(value.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ignored) {
            return fallback;
        }
    }

    private org.bukkit.entity.Horse.Color readHorseColor(String value) {
        if (value == null || value.isBlank()) {
            return org.bukkit.entity.Horse.Color.BLACK;
        }
        try {
            return org.bukkit.entity.Horse.Color.valueOf(value.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ignored) {
            return org.bukkit.entity.Horse.Color.BLACK;
        }
    }

    private org.bukkit.entity.Horse.Style readHorseStyle(String value) {
        if (value == null || value.isBlank()) {
            return org.bukkit.entity.Horse.Style.NONE;
        }
        try {
            return org.bukkit.entity.Horse.Style.valueOf(value.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ignored) {
            return org.bukkit.entity.Horse.Style.NONE;
        }
    }

    private Material readMaterial(ConfigurationSection section, String key, Material fallback) {
        if (section == null) {
            return fallback;
        }
        String raw = section.getString(key);
        if (raw == null || raw.isBlank()) {
            return fallback;
        }
        Material material = Material.matchMaterial(raw.toUpperCase(Locale.ROOT));
        return material == null ? fallback : material;
    }

    private String readString(ConfigurationSection section, String key, String fallback) {
        if (section == null) {
            return fallback;
        }
        String value = section.getString(key);
        return value == null || value.isBlank() ? fallback : value;
    }

    private int readInt(ConfigurationSection section, String key, int fallback) {
        if (section == null) {
            return fallback;
        }
        return section.getInt(key, fallback);
    }

    private double readDouble(ConfigurationSection section, String key, double fallback) {
        if (section == null) {
            return fallback;
        }
        return section.getDouble(key, fallback);
    }

    private boolean readBoolean(ConfigurationSection section, String key, boolean fallback) {
        if (section == null) {
            return fallback;
        }
        return section.getBoolean(key, fallback);
    }

    private void removeBoatRecipes() {
        Iterator<Recipe> iterator = Bukkit.recipeIterator();
        while (iterator.hasNext()) {
            Recipe recipe = iterator.next();
            Material resultType = recipe.getResult().getType();
            if (resultType.name().endsWith("_BOAT") || resultType.name().endsWith("_CHEST_BOAT")) {
                iterator.remove();
            }
        }
    }

    private String prettyName(String input) {
        String[] parts = input.replace('_', ' ').toLowerCase(Locale.ROOT).split("\\s+");
        StringBuilder builder = new StringBuilder();
        for (String part : parts) {
            if (part.isBlank()) {
                continue;
            }
            if (builder.length() > 0) {
                builder.append(' ');
            }
            builder.append(part.substring(0, 1).toUpperCase(Locale.ROOT));
            builder.append(part.substring(1));
        }
        return builder.toString();
    }

    private String colorize(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }

    public record HorseVehicleConfig(
            String displayName,
            Material item,
            int price,
            double speed,
            Material armor,
            org.bukkit.entity.Horse.Color color,
            org.bukkit.entity.Horse.Style style,
            boolean glowing
    ) {
    }

    public record BoatVehicleConfig(
            String displayName,
            Material item,
            org.bukkit.entity.Boat.Type boatType,
            int price,
            double speed
    ) {
    }
}

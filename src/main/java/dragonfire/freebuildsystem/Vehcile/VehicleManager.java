package dragonfire.freebuildsystem.Vehcile;

import dragonfire.freebuildsystem.FreeBuildSystem;
import org.avertox.AvertoxEssentialsV2;
import org.avertox.Inventory.AvertoxInventory;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.vehicle.VehicleDestroyEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

public class VehicleManager implements Listener {
    private final List<Vehicle> vehicles;
    private final AvertoxEssentialsV2 avE;
    private final FreeBuildSystem freeBuildSystem;
    private final HorseVehicleConfig horseVehicleConfig;
    private final List<BoatVehicleConfig> boatVehicleConfigs;

    public VehicleManager(AvertoxEssentialsV2 avE, FreeBuildSystem freeBuildSystem) {
        this.avE = avE;
        this.freeBuildSystem = freeBuildSystem;
        this.vehicles = new ArrayList<>();

        removeBoatRecipes();
        this.horseVehicleConfig = loadHorseVehicleConfig();
        this.boatVehicleConfigs = loadBoatVehicleConfigs();

        VehicleMenu vehicleMenu = new VehicleMenu(
                "VehicleMenu",
                6,
                "Vehicle Menu",
                avE,
                false,
                true,
                AvertoxInventory.InventoryOrder.Grid,
                false,
                null,
                this
        );

        if (freeBuildSystem.getCommand("vehicle") != null) {
            freeBuildSystem.getCommand("vehicle").setExecutor(vehicleMenu);
        }
    }

    public void registerVehicle(Vehicle vehicle) {
        if (vehicle == null) {
            return;
        }
        vehicles.add(vehicle);
    }

    public HorseVehicleConfig getHorseVehicleConfig() {
        return horseVehicleConfig;
    }

    public List<BoatVehicleConfig> getBoatVehicleConfigs() {
        return Collections.unmodifiableList(boatVehicleConfigs);
    }

    public Horse createHorse(Location location) {
        return new Horse(
                horseVehicleConfig.getItem(),
                location,
                horseVehicleConfig.getSpeed(),
                avE,
                horseVehicleConfig.getArmor(),
                horseVehicleConfig.getColor(),
                horseVehicleConfig.getStyle(),
                horseVehicleConfig.isGlowing()
        );
    }

    public Boat createBoat(Location location, BoatVehicleConfig boatConfig) {
        return new Boat(
                boatConfig.getItem(),
                location,
                boatConfig.getSpeed(),
                avE,
                boatConfig.getBoatType()
        );
    }

    private HorseVehicleConfig loadHorseVehicleConfig() {
        ConfigurationSection horseSection = freeBuildSystem.getConfig().getConfigurationSection("vehicles.horse");

        Material item = readMaterial(horseSection, "item", Material.HORSE_SPAWN_EGG);
        int price = readInt(horseSection, "price", 10000);
        double speed = readDouble(horseSection, "speed", 0.225D);
        Material armor = readMaterial(horseSection, "armor", null);
        org.bukkit.entity.Horse.Color color = readHorseColor(readString(horseSection, "color", "BLACK"));
        org.bukkit.entity.Horse.Style style = readHorseStyle(readString(horseSection, "style", "WHITE_DOTS"));
        boolean glowing = readBoolean(horseSection, "glowing", false);

        return new HorseVehicleConfig(item, price, speed, armor, color, style, glowing);
    }

    private List<BoatVehicleConfig> loadBoatVehicleConfigs() {
        List<BoatVehicleConfig> configs = new ArrayList<>();
        ConfigurationSection boatsSection = freeBuildSystem.getConfig().getConfigurationSection("vehicles.boats");

        if (boatsSection != null) {
            for (String key : boatsSection.getKeys(false)) {
                ConfigurationSection boatSection = boatsSection.getConfigurationSection(key);
                if (boatSection == null || !boatSection.getBoolean("enabled", true)) {
                    continue;
                }

                org.bukkit.entity.Boat.Type boatType = readBoatType(readString(boatSection, "type", key), org.bukkit.entity.Boat.Type.OAK);
                Material item = readMaterial(boatSection, "item", defaultBoatItem(boatType));
                String displayName = readString(boatSection, "display-name", formatDisplayName(key) + " Boat");
                int price = readInt(boatSection, "price", 6000);
                double speed = readDouble(boatSection, "speed", 0.4D);

                configs.add(new BoatVehicleConfig(displayName, item, boatType, speed, price));
            }
        }

        if (configs.isEmpty()) {
            configs.add(new BoatVehicleConfig("Oak Boat", Material.OAK_BOAT, org.bukkit.entity.Boat.Type.OAK, 0.4D, 6000));
            configs.add(new BoatVehicleConfig("Spruce Boat", Material.SPRUCE_BOAT, org.bukkit.entity.Boat.Type.SPRUCE, 0.4D, 6000));
            configs.add(new BoatVehicleConfig("Birch Boat", Material.BIRCH_BOAT, org.bukkit.entity.Boat.Type.BIRCH, 0.4D, 6000));
        }

        return configs;
    }

    private Material defaultBoatItem(org.bukkit.entity.Boat.Type boatType) {
        String itemName = boatType.name() + "_BOAT";
        Material resolved = Material.matchMaterial(itemName);
        if (resolved != null) {
            return resolved;
        }
        return Material.OAK_BOAT;
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
            return null;
        }
        try {
            return org.bukkit.entity.Horse.Color.valueOf(value.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ignored) {
            return null;
        }
    }

    private org.bukkit.entity.Horse.Style readHorseStyle(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return org.bukkit.entity.Horse.Style.valueOf(value.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ignored) {
            return null;
        }
    }

    private Material readMaterial(ConfigurationSection section, String path, Material fallback) {
        if (section == null) {
            return fallback;
        }
        String value = section.getString(path);
        if (value == null || value.isBlank()) {
            return fallback;
        }
        Material material = Material.matchMaterial(value.toUpperCase(Locale.ROOT));
        return material == null ? fallback : material;
    }

    private String readString(ConfigurationSection section, String path, String fallback) {
        if (section == null) {
            return fallback;
        }
        String value = section.getString(path);
        return value == null || value.isBlank() ? fallback : value;
    }

    private int readInt(ConfigurationSection section, String path, int fallback) {
        if (section == null) {
            return fallback;
        }
        return section.getInt(path, fallback);
    }

    private double readDouble(ConfigurationSection section, String path, double fallback) {
        if (section == null) {
            return fallback;
        }
        return section.getDouble(path, fallback);
    }

    private boolean readBoolean(ConfigurationSection section, String path, boolean fallback) {
        if (section == null) {
            return fallback;
        }
        return section.getBoolean(path, fallback);
    }

    private String formatDisplayName(String key) {
        if (key == null || key.isBlank()) {
            return "Boat";
        }
        String[] split = key.replace('-', ' ').replace('_', ' ').split("\\s+");
        StringBuilder builder = new StringBuilder();
        for (String part : split) {
            if (part.isBlank()) {
                continue;
            }
            if (builder.length() > 0) {
                builder.append(' ');
            }
            builder.append(part.substring(0, 1).toUpperCase(Locale.ROOT));
            builder.append(part.substring(1).toLowerCase(Locale.ROOT));
        }
        return builder.toString();
    }

    private void removeBoatRecipes() {
        Iterator<Recipe> it = Bukkit.recipeIterator();
        while (it.hasNext()) {
            Recipe recipe = it.next();
            ItemStack out = recipe.getResult();
            Material type = out.getType();
            if (type.name().endsWith("_BOAT") || type.name().endsWith("_CHEST_BOAT")) {
                it.remove();
            }
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (findVehicleByEntity(event.getEntity()) != null) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onVehicleDestroy(VehicleDestroyEvent event) {
        if (findVehicleByEntity(event.getVehicle()) != null) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        if (findVehicleByItem(event.getItemDrop().getItemStack()) != null) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerVehicleInteract(PlayerInteractEvent event) {
        ItemStack item = event.getItem();
        if (item == null) {
            return;
        }

        Vehicle vehicle = findVehicleByItem(item);
        if (vehicle == null) {
            return;
        }

        if (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {
            vehicle.remove();
            event.setCancelled(true);
            return;
        }

        if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (!vehicle.isSpawned()) {
                vehicle.spawn(event.getPlayer().getLocation());
            }
            event.setCancelled(true);
        }
    }

    private Vehicle findVehicleByEntity(org.bukkit.entity.Entity entity) {
        if (entity == null) {
            return null;
        }
        for (Vehicle vehicle : vehicles) {
            if (vehicle.entity != null && vehicle.entity.equals(entity)) {
                return vehicle;
            }
        }
        return null;
    }

    private Vehicle findVehicleByItem(ItemStack item) {
        String itemKey = getKeyString(item);
        if (itemKey == null) {
            return null;
        }
        for (Vehicle vehicle : vehicles) {
            if (itemKey.equals(getKeyString(vehicle.itemStack))) {
                return vehicle;
            }
        }
        return null;
    }

    private String getKeyString(ItemStack item) {
        if (item == null || !item.hasItemMeta() || item.getItemMeta() == null) {
            return null;
        }
        org.avertox.Utils.Key key = new org.avertox.Utils.Key(item.getItemMeta().getPersistentDataContainer(), avE);
        Object value = key.getKey();
        return value == null ? null : value.toString();
    }

    public static final class HorseVehicleConfig {
        private final Material item;
        private final int price;
        private final double speed;
        private final Material armor;
        private final org.bukkit.entity.Horse.Color color;
        private final org.bukkit.entity.Horse.Style style;
        private final boolean glowing;

        public HorseVehicleConfig(Material item, int price, double speed, Material armor, org.bukkit.entity.Horse.Color color, org.bukkit.entity.Horse.Style style, boolean glowing) {
            this.item = item;
            this.price = price;
            this.speed = speed;
            this.armor = armor;
            this.color = color;
            this.style = style;
            this.glowing = glowing;
        }

        public Material getItem() {
            return item;
        }

        public int getPrice() {
            return price;
        }

        public double getSpeed() {
            return speed;
        }

        public Material getArmor() {
            return armor;
        }

        public org.bukkit.entity.Horse.Color getColor() {
            return color;
        }

        public org.bukkit.entity.Horse.Style getStyle() {
            return style;
        }

        public boolean isGlowing() {
            return glowing;
        }
    }

    public static final class BoatVehicleConfig {
        private final String displayName;
        private final Material item;
        private final org.bukkit.entity.Boat.Type boatType;
        private final double speed;
        private final int price;

        public BoatVehicleConfig(String displayName, Material item, org.bukkit.entity.Boat.Type boatType, double speed, int price) {
            this.displayName = displayName;
            this.item = item;
            this.boatType = boatType;
            this.speed = speed;
            this.price = price;
        }

        public String getDisplayName() {
            return displayName;
        }

        public Material getItem() {
            return item;
        }

        public org.bukkit.entity.Boat.Type getBoatType() {
            return boatType;
        }

        public double getSpeed() {
            return speed;
        }

        public int getPrice() {
            return price;
        }
    }
}

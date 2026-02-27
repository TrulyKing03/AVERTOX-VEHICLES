package dragonfire.freebuildsystem.Vehcile;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class VehicleMenu implements CommandExecutor, Listener {
    private final JavaPlugin plugin;
    private final VehicleManager vehicleManager;

    private final String title;
    private final int rows;
    private final int horseSlot;
    private final int boatStartSlot;
    private final Material fillerMaterial;

    public VehicleMenu(JavaPlugin plugin, VehicleManager vehicleManager) {
        this.plugin = plugin;
        this.vehicleManager = vehicleManager;

        this.title = plugin.getConfig().getString("shop.title", "&6Avertox Vehicles");
        this.rows = Math.max(1, Math.min(6, plugin.getConfig().getInt("shop.rows", 3)));
        this.horseSlot = plugin.getConfig().getInt("shop.horse-slot", 11);
        this.boatStartSlot = plugin.getConfig().getInt("shop.boat-start-slot", 13);
        this.fillerMaterial = readMaterial(plugin.getConfig().getString("shop.filler-material", "GRAY_STAINED_GLASS_PANE"), Material.GRAY_STAINED_GLASS_PANE);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }

        if (!player.hasPermission("avertoxvehicles.use")) {
            player.sendMessage(colorize("&cYou do not have permission to use this command."));
            return true;
        }

        if (args.length > 0 && args[0].equalsIgnoreCase("balance")) {
            showBalance(player);
            return true;
        }

        openShop(player);
        return true;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getInventory().getHolder() instanceof VehicleMenuHolder holder)) {
            return;
        }

        event.setCancelled(true);

        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }

        int clickedSlot = event.getRawSlot();
        if (clickedSlot < 0 || clickedSlot >= event.getInventory().getSize()) {
            return;
        }

        ShopEntry entry = holder.entries.get(clickedSlot);
        if (entry == null) {
            return;
        }

        processPurchase(player, entry);
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (event.getInventory().getHolder() instanceof VehicleMenuHolder holder) {
            holder.entries.clear();
        }
    }

    private void openShop(Player player) {
        int size = rows * 9;
        VehicleMenuHolder holder = new VehicleMenuHolder();
        Inventory inventory = Bukkit.createInventory(holder, size, colorize(title));
        holder.inventory = inventory;

        fillBackground(inventory);

        int normalizedHorseSlot = normalizeSlot(horseSlot, size);
        VehicleManager.HorseVehicleConfig horseConfig = vehicleManager.getHorseConfig();
        holder.entries.put(normalizedHorseSlot, new ShopEntry(
                "horse",
                horseConfig.price(),
                vehicleManager::buildHorseVehicle,
                buildVehicleIcon(
                        horseConfig.item(),
                        horseConfig.displayName(),
                        horseConfig.price(),
                        horseConfig.speed(),
                        "Horse"
                )
        ));
        inventory.setItem(normalizedHorseSlot, holder.entries.get(normalizedHorseSlot).icon);

        int slot = normalizeSlot(boatStartSlot, size);
        for (VehicleManager.BoatVehicleConfig boatConfig : vehicleManager.getBoatConfigs()) {
            if (slot >= size) {
                break;
            }
            ShopEntry entry = new ShopEntry(
                    "boat-" + boatConfig.boatType().name().toLowerCase(Locale.ROOT),
                    boatConfig.price(),
                    () -> vehicleManager.buildBoatVehicle(boatConfig),
                    buildVehicleIcon(
                            boatConfig.item(),
                            boatConfig.displayName(),
                            boatConfig.price(),
                            boatConfig.speed(),
                            prettyName(boatConfig.boatType().name()) + " Boat"
                    )
            );
            holder.entries.put(slot, entry);
            inventory.setItem(slot, entry.icon);
            slot++;
        }

        int infoSlot = size - 5;
        inventory.setItem(infoSlot, buildInfoItem(player));

        player.openInventory(inventory);
    }

    private void processPurchase(Player player, ShopEntry entry) {
        if (player.getInventory().firstEmpty() == -1) {
            player.sendMessage(colorize("&cYou need at least one free inventory slot."));
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 0.8f, 1.0f);
            return;
        }

        if (!vehicleManager.getWalletService().withdraw(player, entry.price)) {
            String priceText = vehicleManager.getWalletService().format(entry.price);
            player.sendMessage(colorize("&cNot enough funds. Required: &f" + priceText));
            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 0.8f, 1.0f);
            return;
        }

        Vehicle vehicle = entry.factory.create();
        vehicleManager.registerOwnedVehicle(vehicle);
        ItemStack token = vehicle.createToken();
        player.getInventory().addItem(token);

        String tokenName = "Vehicle";
        if (token.hasItemMeta() && token.getItemMeta() != null && token.getItemMeta().getDisplayName() != null) {
            tokenName = stripColor(token.getItemMeta().getDisplayName());
        }
        player.sendMessage(colorize("&aPurchased &f" + tokenName + "&a."));
        showBalance(player);
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 0.8f, 1.2f);
    }

    private ItemStack buildVehicleIcon(Material material, String displayName, int price, double speed, String kind) {
        Material iconMaterial = material == null ? Material.MINECART : material;
        ItemStack item = new ItemStack(iconMaterial);
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return item;
        }

        meta.setDisplayName(colorize(displayName));

        List<String> lore = new ArrayList<>();
        lore.add(colorize("&7Kind: &f" + kind));
        lore.add(colorize("&7Speed: &f" + speed));
        lore.add(colorize("&7Price: &f" + vehicleManager.getWalletService().format(price)));
        lore.add(colorize("&8Click to purchase"));
        meta.setLore(lore);

        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack buildInfoItem(Player player) {
        ItemStack item = new ItemStack(Material.NETHER_STAR);
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return item;
        }

        meta.setDisplayName(colorize("&eWallet"));
        List<String> lore = new ArrayList<>();
        lore.add(colorize("&7Balance: &f" + vehicleManager.getWalletService().format(vehicleManager.getWalletService().getBalance(player))));
        lore.add(colorize("&8Use /vehicle balance"));
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    private void fillBackground(Inventory inventory) {
        ItemStack filler = new ItemStack(fillerMaterial);
        ItemMeta fillerMeta = filler.getItemMeta();
        if (fillerMeta != null) {
            fillerMeta.setDisplayName(colorize("&8 "));
            filler.setItemMeta(fillerMeta);
        }

        for (int i = 0; i < inventory.getSize(); i++) {
            inventory.setItem(i, filler);
        }
    }

    private int normalizeSlot(int slot, int inventorySize) {
        if (slot < 0) {
            return 0;
        }
        if (slot >= inventorySize) {
            return inventorySize - 1;
        }
        return slot;
    }

    private void showBalance(Player player) {
        String balance = vehicleManager.getWalletService().format(vehicleManager.getWalletService().getBalance(player));
        player.sendMessage(colorize("&7Current balance: &f" + balance));
    }

    private Material readMaterial(String value, Material fallback) {
        if (value == null || value.isBlank()) {
            return fallback;
        }
        Material material = Material.matchMaterial(value.toUpperCase(Locale.ROOT));
        return material == null ? fallback : material;
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

    private String stripColor(String input) {
        return ChatColor.stripColor(input == null ? "Vehicle" : input);
    }

    private String colorize(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }

    private record ShopEntry(String id, int price, VehicleFactory factory, ItemStack icon) {
    }

    @FunctionalInterface
    private interface VehicleFactory {
        Vehicle create();
    }

    private static final class VehicleMenuHolder implements InventoryHolder {
        private Inventory inventory;
        private final Map<Integer, ShopEntry> entries = new HashMap<>();

        @Override
        public Inventory getInventory() {
            return inventory;
        }
    }
}

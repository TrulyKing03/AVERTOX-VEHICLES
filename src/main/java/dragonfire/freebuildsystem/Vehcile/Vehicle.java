package dragonfire.freebuildsystem.Vehcile;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.AbstractHorse;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public abstract class Vehicle {
    private static final String VEHICLE_ID_KEY = "vehicle-id";

    protected final JavaPlugin plugin;
    protected final double speed;
    protected final ItemStack itemStack;
    protected Entity entity;
    private final String vehicleId;
    private final NamespacedKey vehicleIdKey;

    protected Vehicle(Material tokenMaterial, String displayName, List<String> lore, double speed, JavaPlugin plugin) {
        this.plugin = plugin;
        this.speed = speed;
        this.vehicleId = UUID.randomUUID().toString();
        this.vehicleIdKey = new NamespacedKey(plugin, VEHICLE_ID_KEY);
        this.itemStack = new ItemStack(tokenMaterial == null ? Material.MINECART : tokenMaterial);
        decorateToken(displayName, lore);
    }

    private void decorateToken(String displayName, List<String> lore) {
        ItemMeta itemMeta = this.itemStack.getItemMeta();
        if (itemMeta == null) {
            return;
        }

        if (displayName != null && !displayName.isBlank()) {
            itemMeta.setDisplayName(colorize(displayName));
        }

        if (lore != null && !lore.isEmpty()) {
            List<String> coloredLore = new ArrayList<>();
            for (String line : lore) {
                coloredLore.add(colorize(line));
            }
            itemMeta.setLore(coloredLore);
        }

        itemMeta.getPersistentDataContainer().set(
                vehicleIdKey,
                PersistentDataType.STRING,
                this.vehicleId
        );

        this.itemStack.setItemMeta(itemMeta);
    }

    public final Entity spawn(Location location) {
        if (location == null || location.getWorld() == null) {
            return null;
        }
        if (isSpawned()) {
            return this.entity;
        }

        Location spawnLocation = location.clone().add(location.getDirection().normalize().multiply(1.5D));
        spawnLocation.setPitch(0.0F);

        this.entity = spawnEntity(spawnLocation);
        configureEntity();
        return this.entity;
    }

    protected abstract Entity spawnEntity(Location location);

    protected abstract void configureEntity();

    protected final void applyBaseControlSettings() {
        if (this.entity == null) {
            return;
        }

        this.entity.setInvulnerable(true);
        this.entity.setPersistent(true);

        if (this.entity instanceof AbstractHorse horse) {
            AttributeInstance movement = horse.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED);
            if (movement != null) {
                movement.setBaseValue(this.speed);
            }
        }

        if (this.entity instanceof Boat boat) {
            boat.setMaxSpeed(this.speed);
            boat.setOccupiedDeceleration(0.2D);
            boat.setUnoccupiedDeceleration(0.8D);
        }
    }

    public final boolean isSpawned() {
        return this.entity != null && this.entity.isValid() && !this.entity.isDead();
    }

    public final void remove() {
        if (this.entity == null) {
            return;
        }
        this.entity.remove();
        this.entity = null;
    }

    public final String getVehicleId() {
        return vehicleId;
    }

    public final ItemStack createToken() {
        return this.itemStack.clone();
    }

    public final Entity getEntity() {
        return entity;
    }

    public static String getVehicleIdFromItem(ItemStack itemStack, JavaPlugin plugin) {
        if (itemStack == null || !itemStack.hasItemMeta()) {
            return null;
        }
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null) {
            return null;
        }
        NamespacedKey key = new NamespacedKey(plugin, VEHICLE_ID_KEY);
        return itemMeta.getPersistentDataContainer().get(key, PersistentDataType.STRING);
    }

    private String colorize(String input) {
        return ChatColor.translateAlternateColorCodes('&', input);
    }
}

package dragonfire.freebuildsystem.Vehcile;

import org.avertox.Utils.Key;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.AbstractHorse;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Entity;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import java.util.UUID;

public abstract class Vehicle implements Listener {
    protected Entity entity;
    protected final double speed;
    protected final ItemStack itemStack;
    private final Plugin plugin;
    private final String vehicleId;

    public Vehicle(Material item, Location location, double speed, Plugin plugin){
        this.plugin = plugin;
        this.speed = speed;
        this.itemStack = new ItemStack(item);
        this.vehicleId = UUID.randomUUID().toString();
        addNamespacedKey();
    }

    public final Entity spawn(Location location) {
        if (location == null || location.getWorld() == null) {
            return null;
        }
        this.entity = spawnEntity(location);
        setAbilities();
        return this.entity;
    }

    protected abstract Entity spawnEntity(Location location);
    protected abstract void setAbilities();

    private void addNamespacedKey(){
        ItemMeta itemMeta = this.itemStack.getItemMeta();
        if (itemMeta == null) {
            return;
        }
        new Key(itemMeta.getPersistentDataContainer(), this.plugin).setKey("vehicle:" + this.vehicleId);
        this.itemStack.setItemMeta(itemMeta);
    }

    protected void applyBaseControlSettings() {
        if (this.entity == null) {
            return;
        }
        this.entity.setInvulnerable(true);
        this.entity.setPersistent(true);
        if (this.entity instanceof AbstractHorse horse) {
            AttributeInstance movement = horse.getAttribute(Attribute.MOVEMENT_SPEED);
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

    public boolean isSpawned() {
        return this.entity != null && !this.entity.isDead();
    }

    public void remove(){
        if (this.entity == null) {
            return;
        }
        this.entity.remove();
        this.entity = null;
    }
}

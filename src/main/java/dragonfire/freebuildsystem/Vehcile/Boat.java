package dragonfire.freebuildsystem.Vehcile;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.Plugin;

public class Boat extends Vehicle{
    private final org.bukkit.entity.Boat.Type boatType;

    public Boat(Material item, Location location, double speed, Plugin plugin) {
        this(item, location, speed, plugin, org.bukkit.entity.Boat.Type.OAK);
    }

    public Boat(Material item, Location location, double speed, Plugin plugin, org.bukkit.entity.Boat.Type boatType) {
        super(item, location, speed, plugin);
        this.boatType = boatType == null ? org.bukkit.entity.Boat.Type.OAK : boatType;
        spawn(location);
    }

    @Override
    protected Entity spawnEntity(Location location) {
        return location.getWorld().spawn(location, org.bukkit.entity.Boat.class);
    }

    @Override
    protected void setAbilities() {
        if (!(this.entity instanceof org.bukkit.entity.Boat boat)) {
            return;
        }
        applyBaseControlSettings();
        boat.setBoatType(this.boatType);
        boat.setWorkOnLand(false);
    }
}

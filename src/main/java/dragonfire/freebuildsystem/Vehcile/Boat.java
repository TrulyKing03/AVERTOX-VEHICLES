package dragonfire.freebuildsystem.Vehcile;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class Boat extends Vehicle {
    private final org.bukkit.entity.Boat.Type boatType;

    public Boat(
            Material tokenMaterial,
            String displayName,
            List<String> lore,
            double speed,
            JavaPlugin plugin,
            org.bukkit.entity.Boat.Type boatType
    ) {
        super(tokenMaterial, displayName, lore, speed, plugin);
        this.boatType = boatType == null ? org.bukkit.entity.Boat.Type.OAK : boatType;
    }

    @Override
    protected Entity spawnEntity(Location location) {
        return location.getWorld().spawn(location, org.bukkit.entity.Boat.class);
    }

    @Override
    protected void configureEntity() {
        if (!(this.entity instanceof org.bukkit.entity.Boat boat)) {
            return;
        }

        applyBaseControlSettings();
        boat.setBoatType(this.boatType);
        boat.setWorkOnLand(false);
    }
}

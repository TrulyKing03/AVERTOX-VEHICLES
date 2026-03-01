package dragonfire.freebuildsystem.Vehcile;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
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
        return location.getWorld().spawnEntity(location, resolveBoatEntityType());
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

    private EntityType resolveBoatEntityType() {
        String variantType = this.boatType == org.bukkit.entity.Boat.Type.BAMBOO
                ? "BAMBOO_RAFT"
                : this.boatType.name() + "_BOAT";

        try {
            return EntityType.valueOf(variantType);
        } catch (IllegalArgumentException ignored) {
            return EntityType.BOAT;
        }
    }
}

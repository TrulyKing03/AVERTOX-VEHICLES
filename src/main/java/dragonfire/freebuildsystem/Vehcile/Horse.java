package dragonfire.freebuildsystem.Vehcile;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class Horse extends Vehicle {
    private final Material armor;
    private final org.bukkit.entity.Horse.Color color;
    private final org.bukkit.entity.Horse.Style style;
    private final boolean glowing;

    public Horse(
            Material tokenMaterial,
            String displayName,
            List<String> lore,
            double speed,
            JavaPlugin plugin,
            Material armor,
            org.bukkit.entity.Horse.Color color,
            org.bukkit.entity.Horse.Style style,
            boolean glowing
    ) {
        super(tokenMaterial, displayName, lore, speed, plugin);
        this.armor = armor;
        this.color = color;
        this.style = style;
        this.glowing = glowing;
    }

    @Override
    protected Entity spawnEntity(Location location) {
        return location.getWorld().spawn(location, org.bukkit.entity.Horse.class);
    }

    @Override
    protected void configureEntity() {
        if (!(this.entity instanceof org.bukkit.entity.Horse horse)) {
            return;
        }

        applyBaseControlSettings();

        horse.setTamed(true);
        horse.getInventory().setSaddle(new ItemStack(Material.SADDLE));
        horse.setGlowing(this.glowing);

        if (this.armor != null && this.armor != Material.AIR) {
            horse.getInventory().setArmor(new ItemStack(this.armor));
        }

        if (this.color != null) {
            horse.setColor(this.color);
        }

        if (this.style != null) {
            horse.setStyle(this.style);
        }
    }
}

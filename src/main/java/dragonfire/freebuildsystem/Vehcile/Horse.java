package dragonfire.freebuildsystem.Vehcile;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

public class Horse extends Vehicle{
    private final Material armory;
    private final org.bukkit.entity.Horse.Color color;
    private final org.bukkit.entity.Horse.Style style;
    private final boolean glowing;

    public Horse(Material item, Location location, double speed, Plugin plugin, Material armory, org.bukkit.entity.Horse.Color color, org.bukkit.entity.Horse.Style style,boolean glowing) {
        super(item, location, speed,plugin);
        this.armory = armory;
        this.color = color;
        this.style = style;
        this.glowing = glowing;
        spawn(location);
    }

    @Override
    protected Entity spawnEntity(Location location) {
        return location.getWorld().spawn(location, org.bukkit.entity.Horse.class);
    }

    @Override
    protected void setAbilities() {
        if(this.entity == null){
            return;
        }
        if(!(this.entity instanceof org.bukkit.entity.Horse horse)){
            return;
        }
        applyBaseControlSettings();
        horse.setTamed(true);
        horse.getInventory().setSaddle(new ItemStack(Material.SADDLE));
        horse.setGlowing(this.glowing);
        if(this.armory != null && this.armory != Material.AIR){
            horse.getInventory().setArmor(new ItemStack(this.armory));
        }
        if(this.style != null){
            horse.setStyle(this.style);
        }
        if(this.color != null){
            horse.setColor(this.color);
        }
    }
}

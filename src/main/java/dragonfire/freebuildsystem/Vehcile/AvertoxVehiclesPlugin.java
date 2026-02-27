package dragonfire.freebuildsystem.Vehcile;

import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

public final class AvertoxVehiclesPlugin extends JavaPlugin {
    private WalletService walletService;
    private VehicleManager vehicleManager;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        this.walletService = new WalletService(this);
        this.vehicleManager = new VehicleManager(this, walletService);
        this.vehicleManager.register();

        VehicleMenu vehicleMenu = new VehicleMenu(this, vehicleManager);
        Bukkit.getPluginManager().registerEvents(vehicleMenu, this);

        PluginCommand vehicleCommand = getCommand("vehicle");
        if (vehicleCommand == null) {
            getLogger().severe("Command '/vehicle' is missing in plugin.yml.");
        } else {
            vehicleCommand.setExecutor(vehicleMenu);
        }

        getLogger().info("AvertoxVehicles enabled.");
    }

    @Override
    public void onDisable() {
        if (vehicleManager != null) {
            vehicleManager.shutdown();
        }
        if (walletService != null) {
            walletService.save();
        }
        getLogger().info("AvertoxVehicles disabled.");
    }
}

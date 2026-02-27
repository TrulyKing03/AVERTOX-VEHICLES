package dragonfire.freebuildsystem.Vehcile;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class WalletService {
    private final JavaPlugin plugin;
    private final File walletFile;
    private final FileConfiguration walletConfig;

    private final boolean enabled;
    private final double startingBalance;
    private final String currencySymbol;

    public WalletService(JavaPlugin plugin) {
        this.plugin = plugin;
        this.enabled = plugin.getConfig().getBoolean("economy.enabled", true);
        this.startingBalance = plugin.getConfig().getDouble("economy.starting-balance", 25000.0D);
        this.currencySymbol = plugin.getConfig().getString("economy.currency-symbol", "$");

        this.walletFile = new File(plugin.getDataFolder(), "wallets.yml");
        if (!walletFile.exists()) {
            try {
                if (walletFile.getParentFile() != null) {
                    walletFile.getParentFile().mkdirs();
                }
                walletFile.createNewFile();
            } catch (IOException exception) {
                plugin.getLogger().severe("Could not create wallets.yml: " + exception.getMessage());
            }
        }

        this.walletConfig = YamlConfiguration.loadConfiguration(walletFile);
    }

    public boolean withdraw(Player player, double amount) {
        if (!enabled) {
            return true;
        }

        if (amount <= 0.0D) {
            return true;
        }

        UUID playerId = player.getUniqueId();
        double balance = getBalance(playerId);
        if (balance < amount) {
            return false;
        }

        setBalance(playerId, balance - amount);
        return true;
    }

    public double getBalance(Player player) {
        return getBalance(player.getUniqueId());
    }

    public boolean isEnabled() {
        return enabled;
    }

    public String format(double amount) {
        return currencySymbol + String.format("%,.0f", amount);
    }

    public void save() {
        try {
            walletConfig.save(walletFile);
        } catch (IOException exception) {
            plugin.getLogger().severe("Could not save wallets.yml: " + exception.getMessage());
        }
    }

    private double getBalance(UUID playerId) {
        String path = "balances." + playerId;
        if (!walletConfig.contains(path)) {
            walletConfig.set(path, startingBalance);
            save();
        }
        return walletConfig.getDouble(path, startingBalance);
    }

    private void setBalance(UUID playerId, double balance) {
        walletConfig.set("balances." + playerId, balance);
        save();
    }
}

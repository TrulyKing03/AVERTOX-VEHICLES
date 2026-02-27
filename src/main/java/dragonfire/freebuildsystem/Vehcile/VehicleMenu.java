package dragonfire.freebuildsystem.Vehcile;

import org.avertox.AvertoxEssentialsV2;
import org.avertox.Commands.CurrencyHandler;
import org.avertox.Inventory.AvertoxInventory;
import org.avertox.Inventory.InvItem;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.util.Locale;

public class VehicleMenu extends AvertoxInventory implements CommandExecutor {
    private final VehicleManager vehicleManager;

    public VehicleMenu(String id, int rows, String title, AvertoxEssentialsV2 avE, boolean paginationEnabled, boolean border, InventoryOrder inventoryOrder, boolean openCommand, AvertoxInventory closeItem, VehicleManager vehicleManager) {
        super(id, rows, title, avE, paginationEnabled, border, inventoryOrder, openCommand, closeItem);
        this.vehicleManager = vehicleManager;
        addHorseEntry();
        addBoatEntries();
    }

    private void addHorseEntry() {
        VehicleManager.HorseVehicleConfig horseConfig = vehicleManager.getHorseVehicleConfig();
        addInvItem(new InvItem(
                horseConfig.getItem(),
                "Horse",
                true,
                "Cost: " + horseConfig.getPrice() + " money",
                "Speed: " + horseConfig.getSpeed()
        ) {
            @Override
            public void clickableFunction(Player player) {
                purchaseVehicle(
                        player,
                        horseConfig.getPrice(),
                        location -> vehicleManager.createHorse(location),
                        "You bought a horse for " + horseConfig.getPrice() + " money"
                );
            }
        });
    }

    private void addBoatEntries() {
        for (VehicleManager.BoatVehicleConfig boatConfig : vehicleManager.getBoatVehicleConfigs()) {
            addInvItem(new InvItem(
                    boatConfig.getItem(),
                    boatConfig.getDisplayName(),
                    true,
                    "Cost: " + boatConfig.getPrice() + " money",
                    "Type: " + boatConfig.getBoatType().name().toLowerCase(Locale.ROOT),
                    "Speed: " + boatConfig.getSpeed()
            ) {
                @Override
                public void clickableFunction(Player player) {
                    purchaseVehicle(
                            player,
                            boatConfig.getPrice(),
                            location -> vehicleManager.createBoat(location, boatConfig),
                            "You bought a " + boatConfig.getDisplayName() + " for " + boatConfig.getPrice() + " money"
                    );
                }
            });
        }
    }

    private void purchaseVehicle(Player player, int price, VehicleFactory vehicleFactory, String successMessage) {
        Inventory inventory = player.getInventory();
        if (avE.getUitilFunctions().getEmtySlots(inventory) <= 0) {
            player.sendMessage(avE.avertoxMessage("You need one free inventory slot."));
            return;
        }

        if (!avE.getCurrencyHandler().transferCurrency(
                CurrencyHandler.Mod.MINUS,
                CurrencyHandler.CurrencyTyps.MONEY,
                avE.getPlayerHandler().getPlayerByUUID(player.getUniqueId()),
                price
        )) {
            player.sendMessage(avE.avertoxMessage("You do not have enough money."));
            return;
        }

        Vehicle vehicle = vehicleFactory.create(null);
        vehicleManager.registerVehicle(vehicle);
        inventory.addItem(vehicle.itemStack);
        player.sendMessage(avE.avertoxMessage(successMessage));
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if (!command.getName().equalsIgnoreCase("vehicle")) {
            return false;
        }

        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }

        return openShop(player);
    }

    private boolean openShop(Player player) {
        String[] openMethodNames = new String[]{"open", "openInventory", "show", "display"};

        for (String methodName : openMethodNames) {
            try {
                Method method = this.getClass().getSuperclass().getMethod(methodName, Player.class);
                method.invoke(this, player);
                return true;
            } catch (ReflectiveOperationException ignored) {
                // Try next possible API variant from AvertoxInventory.
            }
        }

        try {
            Method inventoryMethod = this.getClass().getSuperclass().getMethod("getInventory");
            Object inventory = inventoryMethod.invoke(this);
            if (inventory instanceof Inventory inv) {
                player.openInventory(inv);
                return true;
            }
        } catch (ReflectiveOperationException ignored) {
            // Fallback message below.
        }

        player.sendMessage(avE.avertoxMessage("Vehicle shop could not be opened."));
        return true;
    }

    @FunctionalInterface
    private interface VehicleFactory {
        Vehicle create(Location location);
    }
}

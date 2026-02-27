# AVERTOX-VEHICLES

A polished Spigot vehicle plugin built around vanilla entities, with configurable horse + boat shop entries and clean in-game controls.

## What Works

- Horse and boat vehicles are fully supported in one shop.
- Boats use native Minecraft boat entities only (`org.bukkit.entity.Boat`).
- Boat variants are configurable per entry (oak, spruce, birch, and more).
- Shared base movement/control behavior is implemented in `Vehicle` so horses and boats stay consistent.
- Vehicle tokens are bound with PersistentDataContainer IDs for reliable spawn/despawn handling.

## Player Flow

1. Run `/vehicle` to open the vehicle shop.
2. Click a horse or boat variant to buy a vehicle token.
3. Right-click token: spawn your vehicle.
4. Left-click token: despawn your vehicle.

Optional command:

- `/vehicle balance` shows current wallet balance.

## Config Highlights

File: `src/main/resources/config.yml`

- `vehicles.horse.*` controls horse item, price, speed, style, color, armor.
- `vehicles.boats.<id>.*` controls each boat listing (type, item, price, speed, name).
- `shop.*` controls menu title, layout slots, filler material.
- `economy.*` controls internal wallet behavior.

## Build

```bash
mvn clean package
```

Jar output:

- `target/avertox-vehicles-1.0.0.jar`

## Main Classes

- `Vehicle.java` shared lifecycle + token key + base controls
- `Horse.java` horse-specific setup
- `Boat.java` boat-specific setup with `Boat.Type`
- `VehicleManager.java` config loading, registry, interaction listeners
- `VehicleMenu.java` polished GUI shop + purchase flow
- `WalletService.java` built-in money backend
- `AvertoxVehiclesPlugin.java` plugin bootstrap

## Reference

README style reference requested and followed from:

- https://github.com/TrulyKing03/AVERTOX-INVENTORY?tab=readme-ov-file

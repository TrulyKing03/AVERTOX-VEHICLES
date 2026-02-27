# AVERTOX-VEHICLES

A configurable vehicle system for Minecraft servers focused on **horses and vanilla boat entities**.

## Highlights

- Supports horses and boats as shop-buyable vehicles.
- Uses **native Minecraft boat entities** (`org.bukkit.entity.Boat`) with no external resource packs.
- Supports configurable boat variants per entry (for example `oak`, `spruce`, `birch`) through `config.yml`.
- Shares movement speed control in the `Vehicle` base class so horse and boat handling stays consistent.
- Vehicle tokens are keyed per vehicle instance via persistent data.

## Commands

- `/vehicle` - Opens the vehicle shop.

## Vehicle Token Controls

- **Right-click** with a vehicle token: spawn vehicle (if not currently spawned).
- **Left-click** with a vehicle token: despawn current vehicle entity.

## Configuration

Default config file: [`src/main/resources/config.yml`](src/main/resources/config.yml)

Example section:

```yml
vehicles:
  horse:
    item: HORSE_SPAWN_EGG
    price: 10000
    speed: 0.225

  boats:
    oak:
      enabled: true
      display-name: Oak Boat
      item: OAK_BOAT
      type: OAK
      price: 6000
      speed: 0.40
```

## Notes

- Boat recipes are removed in `VehicleManager` so boats are acquired via the vehicle system.
- The `/vehicle` shop is wired through `VehicleMenu` and `VehicleManager`.

## File Overview

- `Vehicle.java` - shared vehicle lifecycle, keying, and base control settings
- `Horse.java` - horse-specific setup
- `Boat.java` - vanilla boat entity setup + boat type support
- `VehicleManager.java` - registration, config loading, interaction handling
- `VehicleMenu.java` - shop entries and purchasing flow

## Reference

README structure/style referenced from:

- https://github.com/TrulyKing03/AVERTOX-INVENTORY?tab=readme-ov-file

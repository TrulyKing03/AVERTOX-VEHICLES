# AVERTOX-VEHICLES

Advanced Vehicle Shop and Token-Control Framework for Spigot/Paper 1.20.4
Developed by TrulyKing03

![Version](https://img.shields.io/badge/version-1.0.0-blue) ![Spigot API](https://img.shields.io/badge/Spigot-1.20.4-orange) ![Java 17](https://img.shields.io/badge/Java-17-green) ![Storage](https://img.shields.io/badge/storage-YAML-yellowgreen) ![Status](https://img.shields.io/badge/status-stable-brightgreen) ![License](https://img.shields.io/badge/license-All%20Rights%20Reserved-lightgrey)

---
## Overview

**AvertoxVehicles** is a complete horse + boat vehicle infrastructure designed for RPG and progression servers.

It includes:
- Config-driven vehicle shop with polished inventory GUI
- Horse and vanilla boat vehicle support in one unified system
- Per-player vehicle token ownership with persistent unique token IDs
- Configurable boat variants (`oak`, `spruce`, `birch`, and more)
- Shared movement/control baseline via the `Vehicle` base class
- Spawn/despawn lifecycle bound directly to owned vehicle tokens
- Runtime wallet integration with configurable starting balance and currency symbol
- Vehicle safety guards (tracked entity damage protection + token misuse protection)

---
## Feature Matrix
| System | Included |
|---|---|
| Core Managers | `VehicleManager`, `VehicleMenu`, `WalletService` |
| Vehicle Types | `Horse`, `Boat` (vanilla entities only) |
| Boat Variants | Config-driven `Boat.Type` mapping per listing |
| Shop UX | Inventory GUI with icon lore, pricing, and live balance display |
| Token System | Unique token IDs via `PersistentDataContainer` |
| Control Logic | Shared speed/control baseline in `Vehicle` base class |
| Safety Guards | Damage protection, destroy protection, token drop blocking |
| Commands | `/vehicle`, `/vehicle balance` |
| Persistence | YAML wallet storage (`wallets.yml`) |

---
## Requirements

- Java 17
- Maven 3.8+
- Spigot/Paper 1.20.4-compatible server

---

## Build

```bash
mvn clean package
```

Output jar:

- `target/avertox-vehicles-1.0.0.jar`

---
## Installation

1. Build and place jar in `plugins/`.
2. Start server once to generate config + wallet files.
3. Edit `plugins/AvertoxVehicles/config.yml`.
4. Adjust horse/boat listing values under `vehicles.*`.
5. Restart server.

---
## Configuration

Primary files:

- `src/main/resources/config.yml`
- `src/main/resources/plugin.yml`
- `plugins/AvertoxVehicles/wallets.yml`

Key options:

- Economy runtime behavior (`economy.enabled`, `economy.starting-balance`, `economy.currency-symbol`)
- Shop layout and visuals (`shop.title`, `shop.rows`, slot positions, filler material)
- Horse listing config (`vehicles.horse.*`)
- Boat listing config (`vehicles.boats.<id>.*`)
- Vanilla boat recipe suppression (`remove-vanilla-boat-recipes`)

Value formats supported:

- Bukkit material names (e.g. `OAK_BOAT`, `HORSE_SPAWN_EGG`)
- Bukkit boat types (e.g. `OAK`, `SPRUCE`, `BIRCH`)

Validation is applied at load; invalid values fall back to safe defaults.

---
## Commands

### Player

- `/vehicle`
- `/vehicle balance`

### Admin

- No dedicated admin command suite yet (all behavior is config-driven).

---
## Vehicle Controls

Supported token actions:

- Right-click vehicle token: spawn owned vehicle
- Left-click vehicle token: despawn owned vehicle

Vehicle lifecycle:

1. Vehicle purchased from shop
2. Token added to inventory
3. Token right-click spawns tracked vehicle
4. Token left-click despawns tracked vehicle
5. Tracked vehicle remains protected while active

---

## Data Design
### YAML storage files

- `plugins/AvertoxVehicles/wallets.yml`

Stored values:

- `balances.<player-uuid>`

Wallet reads/writes are handled automatically by `WalletService`.

---
## API and Extensibility

Current extension model:

- Add new vehicle listing entries via `config.yml`
- Extend vehicle behavior by adding subclasses of `Vehicle`
- Extend menu rendering via `VehicleMenu`
- Extend ownership/spawn safety logic via `VehicleManager`

Public external API hooks are not exposed yet; integration is currently source-level.

---
## GUI Suite

Menus included:

- Vehicle Main Shop (`/vehicle`)
- Horse purchase card
- Boat purchase cards (dynamic from config)
- Wallet summary card (current balance)

GUI behavior:

- Click-to-purchase flow with inventory-space checks
- Purchase validation with live wallet deduction
- Success/failure feedback using chat + sound cues

---
## Understanding the Plugin (Player FAQ)

### What is a vehicle token?
A vehicle token is an item that represents one owned vehicle instance.

### How do I get one?
Open `/vehicle` and buy a horse or boat listing from the shop.

### How do I spawn my vehicle?
Hold the token and right-click.

### How do I despawn it?
Hold the token and left-click.

### Can I drop vehicle tokens?
No. Vehicle tokens are protected from being dropped.

---
## How Systems Interact (Big Picture)

1. Config loads shop/economy/vehicle definitions at startup.
2. `VehicleMenu` renders shop entries from loaded vehicle config.
3. Purchase flow checks wallet and inventory capacity.
4. `VehicleManager` registers purchased vehicle ownership by token ID.
5. Interaction events map token -> owned vehicle instance.
6. Spawn/despawn updates tracked runtime entity state.
7. Safety listeners protect tracked vehicles from damage/destruction misuse.
8. `WalletService` persists balances in YAML.

---
## Vehicle Deep Dive

### 1) Token Lifecycle
Created -> Owned -> Spawned -> Despawned -> Reusable.

### 2) Horse Setup
Horse config applies speed, tame state, saddle, optional armor, color, and style.

### 3) Boat Variant Mapping
Each boat listing maps directly to `org.bukkit.entity.Boat.Type` from config.

### 4) Shared Control Layer
`Vehicle` centralizes base speed/control settings used by both horse and boat subclasses.

### 5) Runtime Safety
Tracked entities are protected and token misuse paths are canceled safely.

---
## Notes

- Built with Spigot API `1.20.4-R0.1-SNAPSHOT`
- Java 17 target
- Vanilla boat entities only (no resource packs or custom models)

---
## Developer & Rights

Developed by **TrulyKing03**
All rights reserved.
Email: **TrulyKingDevs@gmail.com**

AvertoxVehicles - Designed for scalable transport and vehicle progression systems.

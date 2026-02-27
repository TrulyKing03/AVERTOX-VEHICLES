# AVERTOX-VEHICLES

Advanced Vehicle Shop and Token Control Framework for Spigot/Paper 1.20.4
Developed by TrulyKing03

![Version](https://img.shields.io/badge/version-1.0.0-blue) ![Spigot API](https://img.shields.io/badge/Spigot-1.20.4-orange) ![Java 17](https://img.shields.io/badge/Java-17-green) ![Storage](https://img.shields.io/badge/storage-YAML-yellowgreen) ![Status](https://img.shields.io/badge/status-stable-brightgreen) ![License](https://img.shields.io/badge/license-All%20Rights%20Reserved-lightgrey)

* * *
## Overview

AvertoxVehicles is a complete horse + boat vehicle infrastructure designed for RPG and progression servers.

It includes:
  * Config-driven vehicle shop with polished inventory GUI
  * Horse and vanilla boat support in one unified system
  * Per-player vehicle ownership via unique token IDs
  * Configurable boat variants (`OAK`, `SPRUCE`, `BIRCH`, and more)
  * Shared movement/control baseline in the `Vehicle` base class
  * Spawn/despawn lifecycle bound directly to owned vehicle tokens
  * Built-in wallet runtime with configurable starting balance
  * Vehicle safety guards (damage protection + token misuse protection)
* * *
## Feature Matrix
System Included
Core Managers `VehicleManager`, `VehicleMenu`, `WalletService`
Vehicle Types Horse, Boat (vanilla entity classes)
Boat Variants Config-driven `Boat.Type` mapping per listing
Token System Unique IDs via `PersistentDataContainer`
Persistence Player balances in YAML (`wallets.yml`)
Storage Backends YAML
Integrations Internal wallet economy runtime (no external dependency required)
GUI Vehicle Shop GUI (`/vehicle`)
Feedback UX Purchase sounds/messages, balance readout, token interaction messaging
Safety Guards Damage cancel for tracked entities, destroy cancel, token drop prevention
Player Commands `/vehicle`, `/vehicle balance`
Extensibility Add new boat variants and tune vehicle stats directly in config
* * *
## Requirements

  * Java 17
  * Maven 3.8+
  * Spigot/Paper 1.20.4-compatible server

* * *

## Build

    mvn clean package

Output jar:

  * `target/avertox-vehicles-1.0.0.jar`

* * *
## Installation

  1. Build and place jar in `plugins/`.
  2. Start server once to generate config and wallet files.
  3. Edit `plugins/AvertoxVehicles/config.yml`.
  4. Adjust vehicle listings and shop layout in config.
  5. Restart server.

* * *
## Configuration

Primary files:

  * `src/main/resources/config.yml`
  * `plugins/AvertoxVehicles/config.yml`
  * `plugins/AvertoxVehicles/wallets.yml`

Key options:

  * Economy behavior (`economy.enabled`, `economy.starting-balance`, `economy.currency-symbol`)
  * Shop layout (`shop.title`, `shop.rows`, `shop.horse-slot`, `shop.boat-start-slot`, `shop.filler-material`)
  * Horse definition (`vehicles.horse.*`) with:
    * Display name, icon item, price, speed
    * Armor, color, style, glow flag
  * Boat catalog (`vehicles.boats.*`) with:
    * Enabled flag
    * Display name, icon item
    * Boat type (`Boat.Type`)
    * Price and speed
  * Vanilla recipe toggle (`remove-vanilla-boat-recipes`)
Definition formats supported:

  * YAML (`.yml`, `.yaml`)

Validation is applied at load; invalid vehicle values fall back to safe defaults.

* * *
## Commands

### Player

  * `/vehicle`
  * `/vehicle balance`

### Admin (`op` / console)

  * No dedicated admin command set yet (management is config-driven).

* * *
## Vehicle Controls

Supported token actions:

  * Right-click token: spawn owned vehicle
  * Left-click token: despawn owned vehicle

Vehicle lifecycle:
  1. Player buys a vehicle from `/vehicle` shop
  2. Token is added to inventory with unique vehicle ID
  3. Right-click spawns that owned vehicle instance
  4. Left-click despawns the active instance
  5. Tracked vehicle remains protected while active

* * *
## Data Design

### YAML storage files

  * `wallets.yml`

Stored data:

  * `balances.<uuid>: <amount>`

Persistence behavior:

  * Balance updates save immediately after purchase
  * Data reloads on plugin startup

* * *
## API and Extensibility

Config-driven extension path:

  * Add unlimited boat listings under `vehicles.boats:`
  * Tune per-vehicle speed and price values
  * Customize shop visuals through `shop.*`
  * Adjust player economy behavior through `economy.*`

Code-level extension points:
  * `Vehicle` for shared control behavior
  * `VehicleManager` for ownership/lifecycle and safety logic
  * `VehicleMenu` for purchase UX adjustments

* * *
## GUI Suite

Menus included:

  * Vehicle Shop Menu (`/vehicle`)

Menu behavior:

  * Shop items display type, speed, and price
  * Click purchase directly from GUI
  * Wallet card shows current player balance
  * Purchase flow validates money and free inventory slots

Feedback behavior:

  * Success level-up sound on purchase
  * Failure villager sound on insufficient funds/full inventory

* * *
## Shop GUI Controls

Main shop (`/vehicle`):

  * Click horse or boat icon: buy one vehicle token
  * Lore preview: shows type, speed, and price

Token controls:

  * Right-click token: spawn
  * Left-click token: despawn

* * *

## Understanding the Plugin (Player FAQ)

### What is a vehicle token?

A vehicle token is an item that represents one owned vehicle instance.

### How do I get one?

Use `/vehicle` and purchase from the shop GUI.

### How do I spawn my vehicle?

Hold your token and right-click.

### How do I despawn it?

Hold your token and left-click.

### Can I drop vehicle tokens?

No. Vehicle tokens are protected from being dropped.

### Are boats custom models?

No. Boats are native Minecraft boat entities with configurable vanilla boat types.

* * *
## How Systems Interact (Big Picture)
  1. Vehicle definitions load from config at startup.
  2. Players open `/vehicle` and browse dynamic horse/boat entries.
  3. Purchase flow validates wallet and inventory capacity.
  4. Owned vehicle token is created and registered by unique ID.
  5. Token interaction maps item -> owned vehicle object.
  6. Spawn/despawn updates tracked entity state.
  7. Safety listeners protect tracked vehicles from damage and misuse.
  8. Wallet values persist in YAML after changes.

* * *
## Vehicle System Deep Dive

### 1) Token Lifecycle

Purchased -> Tokenized -> Spawned -> Despawned -> Reusable.

### 2) Horse Setup

Horse config applies tame state, saddle, optional armor, style/color, and movement speed.

### 3) Boat Variant Mapping

Config entries map directly to `org.bukkit.entity.Boat.Type` values.

### 4) Shared Control Layer

`Vehicle` centralizes base movement behavior used by both horse and boat subclasses.

### 5) Safety and Persistence

Tracked vehicles are protected at runtime and player wallet balances persist across restarts.

* * *

## Notes

  * Built with Spigot API `1.20.4-R0.1-SNAPSHOT`
  * Java 17 target
  * Uses vanilla boat entities only (no resource packs)

* * *
## Developer & Rights

Developed by TrulyKing03
All rights reserved.
Email: TrulyKingDevs@gmail.com

AvertoxVehicles - Designed for scalable horse and boat transport systems.

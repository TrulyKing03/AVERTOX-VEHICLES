# AVERTOX-VEHICLES

Horse + Boat Vehicle Shop Framework for Spigot/Paper 1.20.4  
Developed by TrulyKing03

![Version](https://img.shields.io/badge/version-1.0.0-blue) ![Spigot API](https://img.shields.io/badge/Spigot-1.20.4-orange) ![Java 17](https://img.shields.io/badge/Java-17-green) ![Boat Types](https://img.shields.io/badge/Boat%20Variants-Config%20Driven-00bcd4) ![Status](https://img.shields.io/badge/status-stable-brightgreen) ![License](https://img.shields.io/badge/license-All%20Rights%20Reserved-lightgrey)

* * *
## Overview

AvertoxVehicles is a configurable vehicle system for RPG, economy, and progression-focused servers.

It includes:
- Horse and vanilla boat support in one unified shop
- Native Minecraft entities only (`Horse`, `Boat`) with no resource packs
- Config-driven boat variants (`oak`, `spruce`, `birch`, and more)
- Shared movement/control baseline in the `Vehicle` base class
- Persistent vehicle tokens using `PersistentDataContainer`
- Protected spawn/despawn lifecycle using token interaction
- Built-in wallet-based purchase flow with per-player balances

* * *
## Feature Matrix

System Included  
Core Managers `VehicleManager`, `VehicleMenu`, `WalletService`  
Vehicle Types `Horse`, `Boat` (vanilla entities)  
Boat Variant System Per-entry `Boat.Type` config mapping  
Shop UX Click-to-buy inventory GUI with balance display  
Token System Unique per-vehicle token IDs via PDC  
Control Logic Shared speed/control settings via `Vehicle` base class  
Vehicle Safety Cancels damage + protected destroy handling for tracked vehicles  
Player Commands `/vehicle`, `/vehicle balance`  
Persistence Player wallet storage in `wallets.yml`

* * *
## Requirements

- Java 17
- Maven 3.8+
- Spigot/Paper 1.20.4-compatible server

* * *

## Build

    mvn clean package

Output jar:

- `target/avertox-vehicles-1.0.0.jar`

* * *
## Installation

1. Build and place jar in `plugins/`.
2. Start server once to generate config and wallet files.
3. Edit `plugins/AvertoxVehicles/config.yml`.
4. Restart server.

* * *
## Configuration

Primary files:

- `src/main/resources/config.yml`
- `plugins/AvertoxVehicles/wallets.yml` (runtime generated)

Key options:

- Economy toggle and defaults (`economy.*`)
- Shop title, rows, and slot layout (`shop.*`)
- Horse listing values (`vehicles.horse.*`)
- Boat listing values (`vehicles.boats.<id>.*`)
- Optional vanilla boat recipe removal (`remove-vanilla-boat-recipes`)

* * *
## Commands

### Player

- `/vehicle`
- `/vehicle balance`

### Permission

- `avertoxvehicles.use`

* * *
## Vehicle Control Flow

1. Player opens `/vehicle` and purchases a token.
2. Right-click token to spawn that specific owned vehicle.
3. Left-click token to despawn it.
4. If already spawned, repeated spawn is blocked.
5. Tracked vehicle entities are protected from damage.

* * *

## Data Design
### Runtime data file

- `plugins/AvertoxVehicles/wallets.yml`

Stored values:

- `balances.<uuid>`

* * *

## Vehicle Deep Dive

### 1) Token Lifecycle

Purchase -> Unique Token Created -> Added to Inventory -> Bound to Vehicle ID

### 2) Spawn/Despawn Control

Each token controls exactly one registered vehicle object.

### 3) Shared Movement Layer

Base speed and control settings are applied in `Vehicle` and reused by horse and boat subclasses.

### 4) Boat Variant Mapping

Config entries map directly to `org.bukkit.entity.Boat.Type` values.

### 5) Runtime Safety

Tracked vehicles are protected from direct damage and protected from token misuse cases.

* * *

## Notes

- Built with Spigot API `1.20.4-R0.1-SNAPSHOT`
- Java 17 target
- Vanilla boat entities only (no custom models/resource packs)

* * *
## Developer & Rights

Developed by TrulyKing03  
All rights reserved.  
Email: TrulyKingDevs@gmail.com

AvertoxVehicles - Designed for configurable horse and boat transport systems.

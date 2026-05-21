# Gleam

A lightweight, client-side Fabric mod that renders a light overlay to show exactly where mobs can spawn.

![Light Overlay Example](https://github.com/coalaura/lights/blob/master/.github/overlay.png?raw=true)

## Features

- Renders a red cross on blocks that are dark enough for monsters to spawn (block light level 0).
- Smart scanning only highlights valid spawn locations (solid blocks with air above them).
- Configurable **Visibility Modes**: Toggle (default), Hold or Always Show.
- Performance-friendly, throttled area scanning to maintain high framerates.
- Fully client-side; works perfectly on multiplayer servers.

## Usage

By default, press `F7` to toggle the light overlay on and off. You can change this behavior to "Hold" or "Always" in the configuration menu.

The keybind can be changed in the vanilla Minecraft controls menu under the "Misc" category.

### Configuration

Gleam supports [ModMenu](https://modrinth.com/mod/modmenu?version=1.21.10&loader=fabric) for in-game configuration. You can also manually edit the `config/gleam.json` file.

Available settings:
- **Visibility Mode**: Choose how the overlay is shown (**Hold** key, **Toggle** with key or **Always**).

## Installation

1. Install [Fabric Loader](https://fabricmc.net/) for Minecraft 1.21.10.
2. Download the [Fabric API](https://modrinth.com/mod/fabric-api?version=1.21.10) mod.
3. Download the latest compiled `.jar` from the [Releases](https://github.com/coalaura/lights/releases) page.
4. Place both `.jar` files into your `.minecraft/mods` folder.

## Requirements

- Minecraft 1.21.10
- [Fabric Loader](https://fabricmc.net/) 0.19.2+
- [Fabric API](https://modrinth.com/mod/fabric-api?version=1.21.10)
- [ModMenu](https://modrinth.com/mod/modmenu?version=1.21.10&loader=fabric) (Optional, for in-game configuration)

## License

This project is licensed under the GNU General Public License v3.0 (GPL-3.0).
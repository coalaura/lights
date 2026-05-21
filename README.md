# Gleam

A lightweight, client-side Fabric mod that renders a light overlay to show exactly where mobs can spawn.

![Mob Spawn Overlay](https://github.com/coalaura/lights/blob/master/.github/overlay.png?raw=true)
*Translucent red overlays indicate where monsters can spawn (block light 0).*

![Optimal Torch Placement](https://github.com/coalaura/lights/blob/master/.github/torches.png?raw=true)
*Translucent yellow markers show the most efficient locations for torch placement.*

## Features

- Renders a translucent red overlay on blocks that are dark enough for monsters to spawn (block light level 0).
- **Optimal Torch Placement**: Calculates efficient locations for torches, rendered as golden markers, based on a fixed world grid to maximize coverage with minimal torches.
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
- **Optimal Torches**: Toggle the display of golden markers showing the most efficient locations for torch placement. These are fixed to a stable world grid and will not shift around as you move.
- **Horizontal Radius**: The horizontal distance scanned around the player for dark blocks.
- **Vertical Radius**: The vertical distance (up and down) scanned around the player for dark blocks.

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
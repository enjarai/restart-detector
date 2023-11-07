# Restart Detector

This **fully server-side** mod adds Restart Detectors, 
a new block that will output a redstone signal before the server shuts down.

## Visuals

The Restart detector uses the model of a daylight sensor, 
but distinguishes itself by having a fake command block hovering above it.
This allows it to work fully server-side, while still being visually distinct.

![A Restart Detector ingame](img/detector.png)

## Functionality

All Restart Detectors that are loaded will output a redstone signal when the server is about to shut down, 
starting at strength 1, and slowly increasing to 15 until the actual shutdown.
When the server comes back online, the signal will slowly decay back to 0
to makes sure the world has time to load before contraptions are started back up.

**Restart Detectors CAN be moved by pistons.** 
This lets you make use of them on flying machines and other moving contraptions!

![A Restart Detector on a flying machine](img/flying_machine.png)

## Commands

The `/stop` command is modified by this mod to wait a configurable amount of time before actually shutting down the server,
it is within this window that all Restart Detectors will output a signal.
It also adds a `/stop cancel` and `/stop now` command to cancel and bypass the countdown respectively.

## Configuration

This mod creates a config file at `config/restart_detector.json` with the following options:

- `hijackStopCommand` (default: `true`): Whether to hijack the vanilla `/stop` command to add a countdown.
- `stopCountdownTicks` (default: `600` (30s)): The amount of ticks to wait before actually shutting down the server.

The server must be restarted for changes to the config to take effect.
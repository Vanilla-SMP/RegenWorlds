# RegenWorlds

> **Want to see it live?** Join our Minecraft server and Discord community — **[discord.gg/UbbNPUJkCV](https://discord.gg/UbbNPUJkCV)**

Welcome to **RegenWorlds** — your gateway to fresh, random void worlds in Minecraft!
Step into a special portal, explore a clean and empty dimension, then jump back to where you came from.

## What is this plugin? 🌀

`RegenWorlds` adds special **Crying Obsidian portals** that can:

- Send you to a corresponding **void world** (Overworld / Nether / The End version)
- Keep your exact return point so you can come back safely
- Occasionally regenerate the void worlds with new seeds, so the expanse changes over time
- Protect the built-in portal structure in void worlds from griefing

Think of it as a mini "adventure pitstop" for PvP, mini events, and fast resets.

## How to use portals 👣

### 1) Build a valid crying obsidian frame

Create a 4×5 frame of `Crying Obsidian`:

- Frame size: **4 blocks wide, 5 blocks high**
- Inside space: **2×3** (must be mostly air)
- The outer layer is crying obsidian, inner space must be free (or existing portal blocks)

You can orient it **horizontally (X/Z)** like a normal nether portal shape.

### 2) Light it with Flint and Steel 🔥

Right-click any block of the crying obsidian frame with **Flint and Steel**.

If the frame is valid, the plugin will fill the interior with a portal.

### 3) Enter the void portal 🚪

Walk into any of the portal blocks and:

- You will be teleported into a linked void world
- Your current position is saved automatically
- The destination is always near `0.5, 65, 0.5` (center of the void world spawn platform)

That's it. No commands, no extra setup.

### 4) Return to your home world 🌍

Inside any void world, the portal blocks now act as a return gate.

Just step into them again and you will come back to your saved point automatically.

If for some reason your return point was not saved, you'll be sent to the main world spawn.

## Void world boundaries 🗺️

Each void world has a **world border of 100×100 blocks** (centered at 0, 0).
This keeps the space compact and focused — players can't wander endlessly into the void.

The spawn platform with the return portal is always at the center `(0, 65, 0)`, so you're never far from the exit.

## Useful tips & lifehacks 💡

- Build your portal in a place with enough space (inside part must be free).
- Keep your eyes on your return direction — your exact facing (yaw/pitch) is preserved.
- If the portal doesn't appear, check:
  - Frame is exactly 4×5 crying obsidian outer shape
  - Interior has no random blocks blocking the 2×3 core
  - You clicked the frame with flint and steel, not another item
- In void worlds, avoid trying to destroy the spawn portal structure — it is protected by design.
- The void world is only **100×100 blocks** — don't stray too far from center.

## What to expect from void worlds 🔁

Void worlds can be regenerated automatically from time to time (by server config).
When this happens, everyone inside gets moved to the main world spawn and the world is rebuilt with a new random seed.

So treat the void as a **dynamic space**: perfect for quick resets, mini events, or "new map" moments.

## For server players in short 🎮

- You can only enter this by building and lighting a crying obsidian portal.
- You can always come back through the portal.
- Your return location is saved automatically.
- Void areas are not for permanent builds — they can be remade.
- Each void world is limited to a **100×100 block border**.

Have fun, experiment with fast travel layouts, and enjoy the chaos of clean void resets! 🌟

---

## Try it live 🎮

This plugin runs on our public Minecraft server. Come test it yourself, report bugs, or just have fun!

**[Join our Discord → discord.gg/UbbNPUJkCV](https://discord.gg/UbbNPUJkCV)**

---

## For developers 🛠️

Interested in how this works, want to contribute, or have ideas for improvements?

The source is in this repo. Feel free to open issues or PRs.
For questions, architecture discussions, or feedback — find us on Discord:

**[discord.gg/UbbNPUJkCV](https://discord.gg/UbbNPUJkCV)**

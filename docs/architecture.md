# Architecture

## Decision

Do not merge HelloOO7/BounceTales and Wafer-EX/BounceTalesReversed into one git tree. They are independent decompilations with different hosts, build systems, and license postures.

This repo is a **new runtime**:

- `game-logic` owns rules, simulation, and later file-format readers.
- `runtime-pc` owns window, keyboard, audio, and saves.
- `runtime-android` owns the future APK host.

Upstream clones stay outside this repository and are used as behavior oracles.

## Why three modules

Bounce Tales used Nokia `DirectGraphics` and `DeviceControl`. Those APIs belong in a host, not in shared rules. Desktop uses a **Hangar-style AWT host** (one `JFrame`, keymap, later LCDUI shims). Jademula was not chosen: the HelloOO7 tree expects NetBeans + S40 SDK, and the local `Jademula` checkout is empty. Android can use a J2ME loader embed or a rewritten renderer. Shared code should not import `javax.microedition.*` unless an issue explicitly accepts that dependency.

## Host strategy

| Phase | Desktop | Phone |
| --- | --- | --- |
| Now | Hangar-style AWT window + keymap (`:runtime-pc:run`) | Original JAR + J2ME Loader |
| Next | Load local `assets/`, then MIDI/save | Still JAR in J2ME Loader |
| Later | Same `game-logic` in a desktop jar | Android Gradle APK |
| Not now | — | iOS / IPA (needs a native rewrite) |

## Data flow (later)

```text
user JAR (local)
    -> extract / map resources (not in git)
    -> game-logic reads formats
    -> runtime draws and takes input
```

## What HelloOO7 is for

Readable class structure, resource composer, Nokia S40 packaging, Jademula Windows notes, debug overlay ideas.

## What Wafer-EX is for

Gradle desktop layout, original keyboard table, save/MIDI ideas. This repo’s keymap is a new implementation: arrows **and WASD**, Backspace for Back (not `2`), Q for Star.

## What this repo must not become

A re-upload of either upstream tree, or a public dump of Nokia/Rovio assets.

# Architecture

## Decision

Do not merge HelloOO7/BounceTales and Wafer-EX/BounceTalesReversed into one git tree. They are independent decompilations with different hosts, build systems, and license postures.

This repo is a **MIDlet host**, not a remake:

- `j2me-api` owns original package names the game already links against (`javax.microedition.*`, `com.nokia.mid.ui`).
- `runtime-pc` loads the bundled game JAR with a classloader, sets Nokia platform properties, and starts `MIDlet-1`.
- `game-logic` may still parse JAR catalogs / RLEF for debug tools. It must not import `javax.microedition.*`.
- `runtime-android` owns the future APK host.

Upstream clones stay outside this repository and are used as behavior oracles. Do not copy Wafer-EX or HelloOO7 sources.

## Why these modules

Bounce Tales used Nokia `DirectGraphics` and `DeviceControl`. Those APIs belong in `j2me-api`, not in shared rules. Desktop uses a **Hangar-style AWT host** (one `JFrame`, keymap, LCDUI blit). Jademula was not chosen: the HelloOO7 tree expects NetBeans + S40 SDK. Android can later embed the same shims. `game-logic` stays free of MIDP types.

## Host strategy

| Phase | Desktop | Phone |
| --- | --- | --- |
| Now | Host JAR in the repo root; load the authorized game JAR from `assets/` | Original JAR + J2ME Loader |
| Next | Fill missing MIDP/Nokia methods until menus and chapters run | Still JAR in J2ME Loader |
| Later | Same host packaged as a desktop jar | Android Gradle APK that loads a user JAR |
| Not now | — | iOS / IPA |

## Data flow

```text
authorized game JAR (`assets/bounce-tales.jar`)
    -> MidletManifest reads MIDlet-1
    -> URLClassLoader (parent = host, so javax.* come from j2me-api)
    -> microedition.platform = NokiaN73 (must match manifest Nokia-Platform: Nokia*)
    -> RMIDlet.startApp()
    -> GameCanvas buffer blit at 240×320, window scaled 2×
```

The ChapterPlay types in `game-logic` are leftover parsers for catalog/debug logs. They are not a play window.

## Platform check

Original `StringManager` reads `/META-INF/MANIFEST.MF` `Nokia-Platform:` and compares it to `microedition.platform`. A mismatch calls `System.exit(0)`. Do not set `microedition.platform=PC`. `getAppProperty` must return **manifest** keys, not `System.getProperty`.

## What HelloOO7 is for

Readable class structure, resource composer, Nokia S40 packaging, Jademula Windows notes.

## What Wafer-EX is for

Gradle desktop layout, original keyboard table, save/MIDI ideas. This repo’s keymap is a new implementation: arrows **and WASD**, Backspace for Back (not `2`), Q for Star. Do not copy their LCDUI shims.

## Asset boundary

The approved original game JAR is tracked under the authorization documented in `LEGAL.md`. This repository must not become a re-upload of either upstream source tree or a public dump of additional, extracted, or unapproved Nokia/Rovio assets.

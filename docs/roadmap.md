# Roadmap

Skeleton complete: module graph, CI, issue/PR workflow, legal boundary.

Work after this commit must go through GitHub Issues.

## Milestone A — desktop host

- [x] Choose Hangar-style AWT (not Jademula)
- [x] Window and keymap (arrows + WASD, Backspace, Q) with zh-CN UI
- [x] Asset loader from `assets/` — issue #3
- [x] Workbench: PNG blit, MIDI, save slot, lang table, packed index `a`, ball preview — issue #13
- [x] Load user JAR and start original MIDlet — issue #17
- [x] Playable title, menus, and chapters from original bytecode — issues #19 #21
- [x] Double-click desktop JAR; retire `--debug-overlay` — issue #23
- [x] Commit the host JAR so a GitHub clone can double-click — issue #26
- [x] Bundle the authorized original game JAR — issue #28

## Milestone B — playable original

- [x] Chase missing MIDP / Nokia / MMAPI methods until the title and menus appear
- [x] Play a chapter from original bytecode (not the RLEF overlay)
- [x] Keep `--debug-overlay` gated; overlay UI removed in issue #23
- Debug overlay / collision draw stays unused unless requested

## Milestone C — Android APK

- Convert `runtime-android` to Android Gradle Plugin
- Load user-supplied JAR from device storage
- Keep producing a JAR for J2ME Loader as long as it remains useful

## Out of scope until C is done

- IPA / App Store
- Shipping separately extracted original sprites or music
- Clean-room full remake under a new game title (rejected for this product)

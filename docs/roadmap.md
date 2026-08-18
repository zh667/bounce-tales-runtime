# Roadmap

Skeleton complete: module graph, CI, issue/PR workflow, legal boundary.

Work after this commit must go through GitHub Issues.

## Milestone A — desktop host

- [x] Choose Hangar-style AWT (not Jademula)
- [x] Window and keymap (arrows + WASD, Backspace, Q) with zh-CN UI
- [x] Local asset loader from `assets/` (gitignored) — issue #3
- [x] Workbench: PNG blit, MIDI, save slot, lang table, packed index `a`, ball preview — issue #13

## Milestone B — playable desktop

- [x] Load Misty Morning (`bf`) and draw collision overlay — issue #15
- [ ] Eggs, trampolines, enemies, water, cannons as gameplay (markers are drawn only)
- [ ] Menus / chapter select
- Debug overlay / collision draw (inspired by HelloOO7, written here)
- Document how to point BounceComposer at a local JAR

## Milestone C — Android APK

- Convert `runtime-android` to Android Gradle Plugin
- Load user-supplied assets from device storage
- Keep producing a JAR for J2ME Loader as long as it remains useful

## Out of scope until C is done

- IPA / App Store
- Shipping original sprites or music
- Clean-room full remake under a new game title (separate product if we go that far)

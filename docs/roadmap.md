# Roadmap

Skeleton complete: module graph, CI, issue/PR workflow, legal boundary.

Work after this commit must go through GitHub Issues.

## Milestone A — desktop host

- [x] Choose Hangar-style AWT (not Jademula)
- [x] Window and keymap (arrows + WASD, Backspace, Q) with zh-CN UI
- [x] Local asset loader from `assets/` (gitignored) — issue #3
- [x] Workbench: PNG blit, MIDI, save slot, lang table, packed index `a`, ball preview — issue #13
- [x] Load user JAR and start original MIDlet — issue #17

## Milestone B — playable original

- [ ] Chase missing MIDP / Nokia / MMAPI methods until the title and menus appear
- [ ] Play a chapter from original bytecode (not the RLEF overlay)
- [ ] Keep `--debug-overlay` gated; delete or archive the remake overlay later
- Debug overlay / collision draw stays unused unless requested

## Milestone C — Android APK

- Convert `runtime-android` to Android Gradle Plugin
- Load user-supplied JAR from device storage
- Keep producing a JAR for J2ME Loader as long as it remains useful

## Out of scope until C is done

- IPA / App Store
- Shipping original sprites or music
- Uploading the original game JAR to GitHub
- Clean-room full remake under a new game title (rejected for this product)

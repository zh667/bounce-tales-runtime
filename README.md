# bounce-tales-runtime

Unofficial desktop + Android **runtime scaffold** for a Bounce Tales-compatible client.

This repository is **not** Bounce Tales, not a Nokia/Rovio product, and does not ship original game assets. You must supply a legally obtained original JAR locally. See [LEGAL.md](LEGAL.md).

Current status: **skeleton only**. Modules compile and tests pass. No gameplay yet.

## Goals

1. Keep game rules in `game-logic` (JVM, no UI).
2. Run that logic on desktop via `runtime-pc`.
3. Later wrap the same logic as an Android APK via `runtime-android`.
4. Do **not** target iOS until the desktop and Android paths are real.

J2ME as a phone OS is gone. Android play today still works by loading a JAR in [J2ME Loader](https://github.com/nikita36078/J2ME-Loader). This repo exists so later builds can be a native desktop app and an APK without merging two upstream decompilations.

## Layout

```text
game-logic/        shared rules and module identity
runtime-pc/        desktop entry (Hangar / Jademula-style host later)
runtime-android/   APK host later; JVM stub for now
assets/            local original resources only; gitignored
docs/              architecture, roadmap, contributor rules
```

## Requirements

- JDK 17 for Gradle and CI (toolchain downloads it if needed)
- Windows, macOS, or Linux

Java 26 can compile the skeleton with `--release 17`, but CI and `./gradlew` are specified against **JDK 17**.

## Commands

```bash
./gradlew test
./gradlew :runtime-pc:run
```

Windows:

```powershell
.\gradlew.bat test
.\gradlew.bat :runtime-pc:run
```

Fallback if Gradle cannot start on a too-new JDK:

```powershell
.\scripts\verify.ps1
```

## Collaboration

Bugs and features start as GitHub Issues. Implementation happens on a branch, then a pull request into `main`. Do not push straight to `main`. Details: [CONTRIBUTING.md](CONTRIBUTING.md).

## Study references (clone yourself)

These are **not** git submodules and must not be copied into this tree:

- https://github.com/HelloOO7/BounceTales
- https://github.com/Wafer-EX/BounceTalesReversed

Use them to learn behavior, file formats, and Nokia-specific APIs. Do not vendor their sources here unless a later issue records a clean legal basis (HelloOO7 currently has no license file).

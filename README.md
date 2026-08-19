# bounce-tales-runtime

Unofficial **MIDlet host** for Bounce Tales. Desktop now; Android APK later.

This repository is not an official Nokia/Rovio product. It includes an authorized copy of the original game JAR at `assets/bounce-tales.jar`; that artifact is distributed separately from the Apache-2.0 host code. See [LEGAL.md](LEGAL.md).

Current status: clone this repo and double-click the committed `bounce-tales-runtime.jar` (Java 17+). The host loads the bundled game from `assets/` and does not embed it in the host JAR.

## Goals

1. Host the original MIDlet. Do not reimplement chapters, physics, or enemies.
2. Provide original J2ME / Nokia LCDUI, MIDI, and RMS shims in `j2me-api`.
3. Later wrap the same host as an Android APK via `runtime-android`.
4. Do **not** target iOS until the desktop and Android paths are real.

Until the Android APK exists, [J2ME Loader](https://github.com/nikita36078/J2ME-Loader) plus the same local JAR is the working phone path.

## Layout

```text
j2me-api/          original MIDP / Nokia / MMAPI / RMS shims
game-logic/        JAR catalog and optional debug overlay parsers
runtime-pc/        desktop host (classloader + 240×320 window, scaled 2×)
runtime-android/   APK host later; JVM stub for now
bounce-tales-runtime.jar  host fat JAR (no original game)
assets/            authorized original game JAR
docs/              architecture, roadmap, contributor rules
```

## Requirements

- JDK 17 for Gradle and CI (toolchain downloads it if needed)
- Windows, macOS, or Linux

Java 26 can compile the skeleton with `--release 17`, but CI and `./gradlew` are specified against **JDK 17**.

## Play after clone

The repository includes both the Apache-2.0 **host** JAR (`bounce-tales-runtime.jar`) and the separately authorized game JAR (`assets/bounce-tales.jar`).

1. Install Java 17 or newer (double-click needs `javaw` associated with `.jar`).
2. Double-click `bounce-tales-runtime.jar` in the repo root.

```text
bounce-tales-runtime/
  bounce-tales-runtime.jar   ← host (this repo)
  assets/
    bounce-tales.jar         ← authorized bundled game
```

If double-click does nothing:

```powershell
java -jar bounce-tales-runtime.jar
```

## Commands

```bash
./gradlew check
./gradlew :runtime-pc:run
./gradlew :runtime-pc:syncHostJar
```

Windows:

```powershell
.\gradlew.bat check
.\gradlew.bat :runtime-pc:run
.\gradlew.bat :runtime-pc:run --args="--headless"
.\gradlew.bat :runtime-pc:syncHostJar
```

`:runtime-pc:run` is the source-tree path (same `assets/` folder). After you change host code, run `syncHostJar` and commit the updated root `bounce-tales-runtime.jar` so clones stay current. CI checks that file is our host (`Main-Class: DesktopRuntime`), not a byte-for-byte rebuild.

`--assets <目录>`、`-Dbounce.assets.dir=`、`BOUNCE_ASSETS_DIR` 仍然有效。存档默认写在用户目录 `.bounce-tales-runtime/saves/`（可用 `-Dbounce.save.dir=` 或 `BOUNCE_SAVE_DIR` 改）。

`--debug-overlay` 已移除；没有原版 JAR 时会弹出说明窗口，而不是旧的碰撞预览。

默认界面为简体中文（系统语言为 `en` 时用英文）。原版游戏键位：

| 按键 | 发给游戏 |
| --- | --- |
| ↑ / W | 上 |
| ↓ / S | 下 |
| ← / A | 左 |
| → / D | 右 |
| Enter | 确认 / FIRE |
| Backspace / Esc | 返回 |
| Q | 星号 |

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

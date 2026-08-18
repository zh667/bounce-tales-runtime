# bounce-tales-runtime

Unofficial **MIDlet host** for a legally obtained Bounce Tales JAR. Desktop now; Android APK later.

This repository is **not** Bounce Tales, not a Nokia/Rovio product, and does not ship original game assets. You must supply a legally obtained original JAR locally. See [LEGAL.md](LEGAL.md).

Current status: with a local original JAR, the desktop host loads that JAR and starts the original `RMIDlet`. Menus and chapters come from the original bytecode. The older Misty Morning debug overlay is still in the tree (`--debug-overlay`) but is not the product.

## Goals

1. Host the original MIDlet. Do not reimplement chapters, physics, or enemies.
2. Provide original J2ME / Nokia LCDUI, MIDI, and RMS shims in `j2me-api`.
3. Later wrap the same host as an Android APK via `runtime-android`.
4. Do **not** target iOS until the desktop and Android paths are real.

Until this host can play a full chapter, [J2ME Loader](https://github.com/nikita36078/J2ME-Loader) plus the same local JAR is the working phone path.

## Layout

```text
j2me-api/          original MIDP / Nokia / MMAPI / RMS shims
game-logic/        JAR catalog and optional debug overlay parsers
runtime-pc/        desktop host (classloader + 240×320 window, scaled 2×)
runtime-android/   APK host later; JVM stub for now
assets/            local original JAR only; gitignored
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
.\gradlew.bat :runtime-pc:run --args="--headless"
.\gradlew.bat :runtime-pc:run --args="--debug-overlay"
```

把合法取得的 Bounce Tales `.jar` 放到仓库里的 `assets/`（该目录已被 gitignore），再运行 `:runtime-pc:run`。宿主会加载 JAR 并启动原版 MIDlet。也可以 `--assets <目录>` 或 `-Dbounce.assets.dir=`。存档默认写在用户目录 `.bounce-tales-runtime/saves/`（可用 `-Dbounce.save.dir=` 或 `BOUNCE_SAVE_DIR` 改）。

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

`--debug-overlay` 仍打开以前的关卡碰撞预览，不是默认窗口。

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

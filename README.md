# bounce-tales-runtime

Unofficial desktop + Android **runtime scaffold** for a Bounce Tales-compatible client.

This repository is **not** Bounce Tales, not a Nokia/Rovio product, and does not ship original game assets. You must supply a legally obtained original JAR locally. See [LEGAL.md](LEGAL.md).

Current status: with a local original JAR, the desktop host loads **Misty Morning** (`bf`), draws terrain as a debug overlay, and lets a ball walk/jump on that collision. Enemies, eggs, trampolines, and menus are not simulated yet.

## Goals

1. Keep game rules in `game-logic` (JVM, no UI).
2. Run that logic on desktop via `runtime-pc`.
3. Later wrap the same logic as an Android APK via `runtime-android`.
4. Do **not** target iOS until the desktop and Android paths are real.

J2ME as a phone OS is gone. Android play today still works by loading a JAR in [J2ME Loader](https://github.com/nikita36078/J2ME-Loader). This repo exists so later builds can be a native desktop app and an APK without merging two upstream decompilations.

## Layout

```text
game-logic/        shared rules, JAR catalog, RLEF/lang parsers, chapter play
runtime-pc/        Hangar-style AWT host (window, keymap, blit, MIDI, save, overlay)
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
.\gradlew.bat :runtime-pc:run --args="--headless"
```

把合法取得的 Bounce Tales `.jar` 放到仓库里的 `assets/`（该目录已被 gitignore），再运行 `:runtime-pc:run`。若 JAR 含关卡条目 `bf`，窗口会进入「薄雾早晨」碰撞预览。也可以 `--assets <目录>` 或 `-Dbounce.assets.dir=`。存档默认写在用户目录 `.bounce-tales-runtime/saves/`（可用 `-Dbounce.save.dir=` 或 `BOUNCE_SAVE_DIR` 改）。

默认界面为简体中文（系统语言为 `en` 时用英文）。进关后的键位：

| 按键 | 动作 |
| --- | --- |
| ↑ / W / Enter | 跳跃 |
| ↓ / S | 播放 / 暂停 MIDI |
| ← / A | 向左 |
| → / D | 向右 |
| Backspace | 重生并写入存档槽 |
| Q | 下一张 JAR 内 PNG（工作台模式） |

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

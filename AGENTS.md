# AGENTS.md

> Repository map for coding agents. Read only the docs needed for the current task.
> `docs/`, `LEGAL.md`, and `CONTRIBUTING.md` are the source of truth.

## 1. Project snapshot

- **Name**: bounce-tales-runtime
- **Goal**: unofficial JVM runtime scaffold so Bounce Tales-compatible logic can run on desktop now and Android later, without shipping original assets
- **Tech stack**: Java 17, Gradle 8.14, JUnit 5
- **Package manager**: Gradle Wrapper
- **Runtime version**: JDK 17 (toolchain)
- **Supported environments**: Windows / macOS / Linux; Android APK is a later milestone
- **Environment initialization**: `./gradlew test`
- **Required services**: none
- **Default branch**: `main`
- **Main code**: `game-logic/`, `runtime-pc/`, `runtime-android/`
- **Tests**: `*/src/test/java`
- **CI**: `.github/workflows/ci.yml` (job name `Verify`)

## 2. Real commands

- Install / resolve: `./gradlew test` (downloads the wrapper distribution and JDK 17 toolchain)
- Run desktop host: `./gradlew :runtime-pc:run` (add `--args="--headless"` in CI or scripts)
- Format check: none yet
- Lint: none yet
- Typecheck: Java compile via Gradle
- Unit test: `./gradlew test`
- Integration / E2E: none
- Build: `./gradlew build`
- CI equivalent: `./gradlew test` or `.\scripts\verify.ps1`
- Preview / deploy: none; do not publish APK/IPA
- UI verification: `./gradlew :runtime-pc:run` then press arrows/WASD; tests cover KeyMap and zh-CN strings only
- Metrics / Trace: none

Do not report a command as passing unless it was actually run.

## 3. Document map

| Task | Source |
| --- | --- |
| Architecture | `docs/architecture.md` |
| Roadmap | `docs/roadmap.md` |
| Engineering rules | `docs/ai-rules/engineering.md` |
| Tests | `docs/ai-rules/testing.md` |
| Legal / assets | `LEGAL.md` |
| Git / PR | `CONTRIBUTING.md` |
| Security | `SECURITY.md` |

## 4. Hard boundaries

- Never commit original JAR/JAD, sprites, MIDI, `lang.*`, or packed levels.
- Never vendor `HelloOO7/BounceTales` sources (no license file).
- Do not implement iOS in this phase.
- Do not merge to `main` locally; open a pull request.
- Keep Nokia-specific APIs behind host interfaces in runtimes, not in `game-logic` if they can be avoided.

## 5. Workflow

Issue first → `feature/<id>-slug` or `bugfix/<id>-slug` → PR → `Verify` green → merge.

# Engineering rules

- Prefer the existing modules. Do not add another app without an issue. `j2me-api` holds original MIDP/Nokia shims (issue #17); it is a library, not a second product.
- `game-logic` stays UI-free and Android-SDK-free.
- Runtimes may depend on `game-logic`. `game-logic` must not depend on runtimes.
- Public Java types live under `io.github.zh667.bouncetales.*`, except `j2me-api` which must use original `javax.microedition.*` and `com.nokia.mid.*` names.
- Commands that CI runs must work on Windows and Ubuntu.
- If a Gradle task is a no-op, say so in README. Do not fake a green build.
- The approved `assets/bounce-tales.jar` is tracked. Other original assets stay ignored unless maintainer-approved and documented in `LEGAL.md`.
- Small diffs: feature, refactor, and formatting in separate commits when practical.

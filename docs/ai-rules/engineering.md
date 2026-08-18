# Engineering rules

- Prefer the existing three modules. Do not add a fourth app without an issue.
- `game-logic` stays UI-free and Android-SDK-free.
- Runtimes may depend on `game-logic`. `game-logic` must not depend on runtimes.
- Public Java types live under `io.github.zh667.bouncetales.*`.
- Commands that CI runs must work on Windows and Ubuntu.
- If a Gradle task is a no-op, say so in README. Do not fake a green build.
- Original assets stay in gitignored `assets/`.
- Small diffs: feature, refactor, and formatting in separate commits when practical.

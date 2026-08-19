# Testing

Skeleton tests lock the module graph and host labels. They are not gameplay tests.

When adding behavior:

1. List cases first: happy path, illegal input, missing assets, host-not-ready.
2. Put unit tests next to the module under `src/test/java`.
3. Keep unit tests independent of the bundled game JAR. Build tiny placeholder JARs in temp dirs. Headless tests must not start the Swing MIDlet window.
4. Prefer synthetic PNG/MIDI/lang/index blobs for focused unit tests; use the authorized game JAR only for explicit integration checks.

Current required command: `./gradlew test`.

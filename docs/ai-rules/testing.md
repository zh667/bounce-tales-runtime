# Testing

Skeleton tests lock the module graph and host labels. They are not gameplay tests.

When adding behavior:

1. List cases first: happy path, illegal input, missing assets, host-not-ready.
2. Put unit tests next to the module under `src/test/java`.
3. Do not require original JAR contents in CI. Build tiny placeholder JARs in temp dirs.
4. Do not assert on copyrighted pixel buffers or MIDI bytes in this repository. Synthetic PNG/MIDI/lang/index blobs are required.

Current required command: `./gradlew test`.

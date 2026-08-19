# assets

This directory contains the authorized Bounce Tales game artifact used by the runtime.

`bounce-tales.jar` is intentionally tracked. Other files in this directory are ignored unless explicitly approved and documented in `LEGAL.md`.

## Load a JAR

1. Keep the bundled `bounce-tales.jar` in this folder.
2. Double-click `..\bounce-tales-runtime.jar` (Java 17+), or run:

```powershell
.\gradlew.bat :runtime-pc:run
```

Or point at another folder:

```powershell
.\gradlew.bat :runtime-pc:run --args="--assets D:\games\bounce"
```

The window should start the original MIDlet. The game JAR is tracked under the redistribution authorization described in `LEGAL.md`; the host JAR at the repo root is separately covered by Apache-2.0.

Untracked local inputs may include:

- composed sprites, MIDI, `lang.*`, and packed levels extracted by your own tools

Do not replace `bounce-tales.jar` or commit extracted resources without maintainer approval and an update to `LEGAL.md`.

HelloOO7's BounceComposer can decompose a JAR locally. Do not copy its output into a pull request unless it is separately approved.

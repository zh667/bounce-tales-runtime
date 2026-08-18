# assets

Keep original Bounce Tales resources here on your machine only.

This directory is gitignored except for this README and `.gitkeep`.

## Load a JAR

1. Copy a legally obtained Bounce Tales `.jar` into this folder.
2. Keep only one Bounce Tales package here (other `.jar` files make the loader ask you to choose).
3. Run:

```powershell
.\gradlew.bat :runtime-pc:run
```

Or point at another folder:

```powershell
.\gradlew.bat :runtime-pc:run --args="--assets D:\games\bounce"
```

The window should start the original MIDlet (menus from the JAR, not the debug overlay). Git will not stage the `.jar`. Do not upload this file to GitHub.

Expected local inputs (do not commit):

- original `.jar`
- composed sprites, MIDI, `lang.*`, and packed levels extracted by your own tools

HelloOO7's BounceComposer can decompose a JAR you already have. Point it at a local copy; do not copy its output into a pull request.

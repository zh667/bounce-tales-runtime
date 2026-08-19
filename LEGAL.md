# Legal boundary

This file is a project policy, not legal advice.

## What this repository is

Original scaffolding, documentation, J2ME API shims, independently written runtime code, and one authorized original game artifact. Apache License 2.0 applies only to the original project material.

## What this repository is not

- Not an official Bounce / Bounce Tales release
- Not a license to Nokia, Rovio, or Microsoft intellectual property
- Not a general-purpose mirror for other game builds, extracted PNG, MIDI, language packs, or level blobs

## The original JAR

The maintainer has confirmed authorization to redistribute the original Bounce Tales JAR tracked at `assets/bounce-tales.jar`:

- MIDlet version: `2.0.14`
- SHA-256: `c8265a2f10b3c69c7c4c835f86c40f98f44a776a2911b04b88d47b1594d8f18d`

The committed `bounce-tales-runtime.jar` at the repository root is this project's host. Apache-2.0 covers the host, but does not cover the bundled game JAR. Bounce Tales and its contents remain subject to their applicable third-party rights and the authorization held by the maintainer.

Do not replace the approved artifact or publish additional original game builds without maintainer approval and a corresponding update to this file.

## Assets

`assets/bounce-tales.jar` is intentionally tracked and loaded by the host. Other files under `assets/` remain ignored by default.

Do not open a pull request that adds, unless separately approved and documented:

- other original game `*.jar` / `*.jad` packages
- extracted original `aa.png` … `av.png`, `*.mid`, `lang.*`, or packed level files (`be`–`bv`, resource map `a`)
- decompiled dumps of HelloOO7/BounceTales

## Upstream study repos

| Repo | Role | License posture |
| --- | --- | --- |
| HelloOO7/BounceTales | Readable decompilation, resource composer, Jademula Windows layer | No license file; do not copy into this repo |
| Wafer-EX/BounceTalesReversed | Desktop Gradle build, save/MIDI/keyboard ideas, Hangar host | Apache-2.0 for *that author's* code only |

If we later reuse Wafer-EX code, record it in `NOTICE` and keep it separate from the authorized game artifact.

## Distribution

The authorized game JAR may be distributed in this repository alongside the host. Embedding it into a different artifact or distribution channel, such as a future APK, requires confirmation that the authorization covers that form of distribution. The root `bounce-tales-runtime.jar` remains a host-only artifact.

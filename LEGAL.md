# Legal boundary

This file is a project policy, not legal advice.

## What this repository is

Original scaffolding, documentation, and later independently written runtime code. Apache License 2.0 applies only to that original material.

## What this repository is not

- Not an official Bounce / Bounce Tales release
- Not a license to Nokia, Rovio, or Microsoft intellectual property
- Not a place to publish the original JAR, PNG, MIDI, language packs, or level blobs

## Assets

Put a legally obtained original JAR on your machine. Extract resources into `assets/` if a local tool needs them. `assets/` is gitignored except for the README.

Never open a pull request that adds:

- `*.jar` / `*.jad` game packages
- original `aa.png` … `av.png`, `*.mid`, `lang.*`, or packed level files (`be`–`bv`, resource map `a`)
- decompiled dumps of HelloOO7/BounceTales

## Upstream study repos

| Repo | Role | License posture |
| --- | --- | --- |
| HelloOO7/BounceTales | Readable decompilation, resource composer, Jademula Windows layer | No license file; do not copy into this repo |
| Wafer-EX/BounceTalesReversed | Desktop Gradle build, save/MIDI/keyboard ideas, Hangar host | Apache-2.0 for *that author's* code only |

If we later reuse Wafer-EX code, record it in `NOTICE` and keep original assets out.

## Distribution

Shipping an APK/IPA that contains original Bounce Tales art, music, or story is not in scope. Public binaries from this project must use placeholder or original replacement assets unless a rights holder grants written permission.

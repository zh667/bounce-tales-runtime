# Contributing

This project is set up for **multi-person work**. Code does not go to `main` except through a pull request.

## 1. Start with an issue

Open an issue before writing code, unless the change is a typo in docs you already own.

- Bug: use the Bug report template. Include OS, JDK, module, and how to reproduce.
- Feature: use the Feature request template. Describe the user-visible result and the module (`game-logic` / `runtime-pc` / `runtime-android`).

Wait until the issue is triaged if the change is large, legal-sensitive, or touches assets.

## 2. Branch from latest `main`

```text
bugfix/<issue-number>-short-slug
feature/<issue-number>-short-slug
docs/<issue-number>-short-slug
```

Examples:

```text
feature/12-desktop-window
bugfix/18-timestep-dt
```

Do not commit on `main`.

## 3. Implement on the branch

- Keep game rules in `game-logic`. Runtimes only host, render, and persist.
- Do not add original JAR contents. See [LEGAL.md](LEGAL.md).
- Tests belong with the module they protect.
- Update docs when commands, layout, or workflow change.

## 4. Open a pull request

- Fill in `.github/PULL_REQUEST_TEMPLATE.md`
- Link `Fixes #N` or `Refs #N`
- Keep the PR focused; one issue when possible
- CI job `Verify` must pass

## 5. Review and merge

- Resolve review comments on the PR
- Maintainers squash or merge after `Verify` is green
- Delete the branch after merge

## What not to do

- Force-push `main`
- Bypass the pull request
- Vendor HelloOO7 sources
- Commit secrets, API keys, or game dumps

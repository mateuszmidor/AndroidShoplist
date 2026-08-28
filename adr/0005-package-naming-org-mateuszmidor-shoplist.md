# ADR-0005: Package naming org.mateuszmidor.shoplist

- Status: accepted
- Date: 2026-08-28

## Context and Problem Statement

The template ships the placeholder package `com.example.shoplist` for
namespace, applicationId, and source directories. Changing the applicationId /
namespace later is expensive (renaming source dirs, imports, manifest, and any
future backup/queries), so the decision is best fixed at scaffolding time while
only a handful of files exist.

## Considered Options

- Rename now to a real package `org.mateuszmidor.shoplist`.
- Keep the placeholder `com.example.shoplist`.

## Decision Outcome

Chosen option: "Rename now to `org.mateuszmidor.shoplist`", because it is trivial
now and costly later, gives a stable applicationId that will never be
re-published, and removes a template artifact from a personal app.

### Consequences

- Good, because the package/applicationId is stable and the source tree matches
  the real identity.
- Bad, because the rename touches build config, imports, and manifest in the same
  change; must be verified with a full build.

# ADR-0004: Dependency and version policy

- Status: accepted
- Date: 2026-08-28

## Context and Problem Statement

Every dependency added to the project must follow a consistent policy so the
project stays maintainable and current. As of this change the project uses AGP
9.3.2, Kotlin 2.2.10, Compose BOM 2026.02.01, compileSdk/targetSdk 37, minSdk 30.
ARCHITECTURE.md requires "mature-but-verified" technology and no experimental or
unreleased libraries.

## Considered Options

- Keep current stable, never-add-experimental policy: adopt current stable
  Google-recommended components, no pre-release/alpha licenses.
- Pin an older, settled version set for maximum tutorial compatibility.

## Decision Outcome

Chosen option: "Keep current stable versions; no experimental libraries",
because the app is a learning project aimed at current Android/Kotlin practice
and must not depend on unstable technology.

### Consequences

- Good, because the project tracks current APIs and official guidance.
- Bad, because staying at the latest stable means occasional breaking-change
  migrations; the solo maintainer must keep up with release notes.

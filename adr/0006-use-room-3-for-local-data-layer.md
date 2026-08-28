# ADR-0006: Use Room 3.0 for the local data layer

- Status: accepted
- Date: 2026-08-28

## Context and Problem Statement

ARCHITECTURE.md fixes local persistence via Room. The data layer must choose
between the 2.x line (in maintenance mode since Room 3 shipped stable) and the
new Room 3.0 series (`androidx.room3` artifacts, Kotlin-only, KSP-only,
coroutines-first). The project is a brand-new Kotlin app (Kotlin 2.2.10,
minSdk 30) with no legacy Room code, and ADR-0004 commits the project to
current stable, Google-recommended components with no experimental libraries.

## Considered Options

- Room 3.0.2 (`androidx.room3:room3-runtime`, `androidx.room3:room3-compiler`
  via the KSP plugin, plus the `androidx.room3` Gradle plugin for schema
  export) - current stable (2026-08-26), the path Google recommends for new
  Kotlin projects.
- Room 2.8.4 (`androidx.room:room-runtime`, `androidx.room:room-compiler`) -
  last 2.x stable, in maintenance mode with patch releases only.

## Decision Outcome

Chosen option: "Room 3.0.2 with KSP", because ADR-0004 mandates current
stable components; Room 3.0 is stable, is the direction of the library
("2.x is in maintenance mode"), is KSP-native (the current Kotlin toolchain
primitive), and is coroutines-first, which matches ARCHITECTURE's reactive
Flow principle. Using 2.8 would adopt a settling line that is no longer the
recommended path.

### Consequences

- Good, because the data layer starts on the current, forward-looking Room
  line and needs no later 2.x -> 3.x migration; suspending DAO functions and
  reactive Flow reads are built in rather than bolted on.
- Bad, because learning material and community examples overwhelmingly cover
  Room 2.x (`androidx.room.*` imports), so help must be read through the
  newer API surface; and because Room 3.0 requires KSP pinned to the Kotlin
  compiler version, so Kotlin and KSP version bumps must travel together
  (managed centrally by ADR-0003's version catalog).
- Follow-up: keep DAOs within the core suspend/Flow API surface to limit
  exposure to newer driver internals; revisit this ADR only if a migration
  to a future major off the Room 3 line becomes necessary.
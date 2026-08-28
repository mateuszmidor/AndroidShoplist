# ADR-0003: Gradle Kotlin DSL and version catalog

- Status: accepted
- Date: 2026-08-28

## Context and Problem Statement

The build configuration language and how dependency versions are managed must be
fixed early, since every future dependency (Room, KSP, Navigation) flows through
it. The options are script language (Groovy vs Kotlin DSL) and version management
(central version catalog vs inline versions).

## Considered Options

- Gradle Kotlin DSL with a central `gradle/libs.versions.toml` version catalog.
- Gradle Groovy DSL with inline versions.

## Decision Outcome

Chosen option: "Gradle Kotlin DSL with a central version catalog", because it is
the current Google/Gradle recommended approach, gives type-safe build scripts,
and centralizes all versions in one catalog for consistent updates.

### Consequences

- Good, because dependency versions are declared once and reused everywhere.
- Bad, because Kotlin DSL has a steeper learning curve than Groovy; accepted as
  part of the learning goal.

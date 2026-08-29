# ADR-0007: Use Navigation Compose with type-safe routes

- Status: accepted
- Date: 2026-08-29

## Context and Problem Statement

The app must move from a placeholder to a two-screen navigation model
(lists <-> items). ARCHITECTURE.md mandates "Jetpack Navigation Compose
(type-safe routes + back stack)". Route arguments include a shopping list
UUID passed from the lists screen to the items screen. `kotlinx.serialization`
has no built-in serializer for `java.util.UUID` (it ships one only for the
newer `kotlin.uuid.Uuid`), while the whole domain layer - entities and DAO -
uses `java.util.UUID`.

## Decision Outcome

Adopt Jetpack Navigation Compose (current stable 2.10.0) with type-safe
`@Serializable` route types in the navigation layer. Keep `java.util.UUID` as
the single domain identifier type throughout the app; route arguments of type
`UUID` use a small custom `KSerializer<UUID>` (canonical string form) declared
once and reused by every route that carries an identifier.

### Consequences

- Good, because route arguments and destinations are compile-time checked, add
  a stable contract that later screens (items, etc.) build on, and UUID
  argument handling is centralized in one serializer instead of repeated
  `navArgument` string ceremony.
- Bad, because type-safe routes require the Kotlin serialization Gradle plugin
  and `kotlinx-serialization-json` as build/runtime dependencies, and the
  custom UUID serializer must be kept in step with the domain type.
- Follow-up: the items screen (change 03) consumes the `items/{listId}` route
  defined here; no further navigation technology change is anticipated while
  the two-screen model holds.
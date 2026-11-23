# ADR Review Manifest

- Status: completed
- Review date: 2026-11-23

## Review Summary

ADR review completed for this change. The reshaped "New item" dialog is a local interaction decision: it reuses the existing state in `ItemsScreen`, the existing lookup helpers in `ui/items`, and the existing add/toggle callbacks. It introduces no new boundary, dependency, module, schema, or persistent contract, so no decision from this change needs to outlive it.

## In-Force ADRs Reviewed

- ADR-0001: Manual DI via AppContainer
- ADR-0002: Single `:app` module
- ADR-0003: Gradle Kotlin DSL version catalog
- ADR-0004: Dependency version policy
- ADR-0005: Package naming `org.mateuszmidor.shoplist`
- ADR-0006: Use Room 3 for local data layer
- ADR-0007: Use Navigation Compose with type-safe routes
- ADR-0008: Source ViewModels via CreationExtras initializer
- ADR-0009: Items relate to lists with cascade delete
- ADR-0010: Lists expose derived aggregates as a single query
- ADR-0011: Pure logic in `domain` package
- ADR-0012: Navigation routes may carry a list of identifiers

No ADR declares a `Supersedes` field, so all reviewed ADRs are currently in force. This change is coherent with all of them: no new dependency or DI wiring (0001, 0004), no module or package change (0002, 0005), no storage or navigation change (0006, 0007, 0008, 0009, 0010, 0012), and the lookup/filter rules stay as single-screen presentation logic in `ui/items` under ADR-0011.

## New Durable ADRs Created

- None - no major durable architectural decisions were introduced.

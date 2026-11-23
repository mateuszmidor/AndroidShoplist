# ADR Review Manifest

- Status: completed
- Review date: 2026-11-23

## Review Summary

ADR review completed for this change. Moving the Listonic import entry point from the FAB menu to a TopAppBar `MoreVert` overflow action is a local screen-composition change. It reuses existing dialogs, ViewModel callbacks, and navigation; it introduces no new dependency, module boundary, persistence contract, or cross-cutting pattern.

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

No ADR declares a `Supersedes` field, so all reviewed ADRs are currently in force. This change is coherent with all of them; no in-force ADR needs revisiting.

## New Durable ADRs Created

- None - no major durable architectural decisions were introduced.

# ADR Review Manifest

- Status: completed
- Review date: 2026-08-29

## Review Summary

ADR review completed for this change. All design decisions were evaluated against
the in-force ADR set; no decision establishes a new durable architectural
commitment, and no stored decision is intentionally diverged from. The change
implements existing established patterns: ordering at the DAO (as introduced in
Change 03), ViewModel write-path via the repository (ADR-0008), and Room as the
data layer (ADR-0006).

## In-Force ADRs Reviewed

- ADR-0001: Manual dependency injection via AppContainer
- ADR-0002: Single :app module
- ADR-0003: Gradle Kotlin DSL and version catalog
- ADR-0004: Dependency and version policy
- ADR-0005: Package naming org.mateuszmidor.shoplist
- ADR-0006: Use Room 3.0 for the local data layer
- ADR-0007: Use Navigation Compose with type-safe routes
- ADR-0008: Source ViewModels via CreationExtras initializer from AppContainer
- ADR-0009: Items relate to lists with cascade delete

## New Durable ADRs Created

- None - no major durable architectural decisions were introduced. The concrete
  choices (DAO-level two-section ordering, atomic `SET bought = NOT bought`
  toggle query, checkbox/strikethrough row UI, fake-repository ordering mirror)
  are tactical implementations of patterns already established in the in-force
  ADR set and do not merit standalone records.
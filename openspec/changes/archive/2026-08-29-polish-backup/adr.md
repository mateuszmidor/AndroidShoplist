# ADR Review Manifest

- Status: completed
- Review date: 2026-08-29

## Review Summary

ADR review completed for this change.

## In-Force ADRs Reviewed

- ADR-0001: Manual DI via AppContainer
- ADR-0002: Single :app module
- ADR-0003: Gradle Kotlin DSL and version catalog
- ADR-0004: Dependency and version policy
- ADR-0005: Package naming org.mateuszmidor.shoplist
- ADR-0006: Use Room 3 for the local data layer
- ADR-0007: Use Navigation Compose with type-safe routes
- ADR-0008: Source ViewModels via CreationExtras initializer from AppContainer
- ADR-0009: Items relate to lists with cascade delete

## New Durable ADRs Created

- `adr/0010-lists-expose-derived-aggregates-as-single-query.md` — records the
  per-list summary pattern: derived aggregates are computed as a single
  data-layer query exposing `ListSummary` rows from one flow, with ViewModels
  consuming (not combining) them. Supersedes nothing; builds on ADR-0008 and
  the follow-up anticipated by ADR-0009.
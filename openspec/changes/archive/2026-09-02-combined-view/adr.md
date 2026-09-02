# ADR Review Manifest

- Status: completed
- Review date: 2026-09-01

## Review Summary

ADR review completed for this change.

## In-Force ADRs Reviewed

- ADR-0001 (manual DI via `AppContainer`)
- ADR-0002 (single `:app` module)
- ADR-0003 (Gradle Kotlin DSL version catalog)
- ADR-0004 (dependency version policy)
- ADR-0005 (package root `org.mateuszmidor.shoplist`)
- ADR-0006 (Room 3 via KSP for local persistence)
- ADR-0007 (Navigation Compose type-safe routes, single-UID argument)
- ADR-0008 (ViewModels via CreationExtras; reads via `Flow`, writes via repository)
- ADR-0009 (items relate to lists with cascade delete)
- ADR-0010 (derived aggregates exposed as a single data-layer query)
- ADR-0011 (pure business logic in a `domain` package)

No other in-force ADRs exist and none reference `Supersedes`.

## New Durable ADRs Created

- `adr/0012-navigation-routes-may-carry-a-list-of-identifiers.md` — extends
  ADR-0007's single-UID route argument convention to routes carrying a list of
  identifiers, recorded as the encoding baseline for the combined view route
  and future multi-id routes.
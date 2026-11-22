# ADR Review Manifest

- Status: completed
- Review date: 2026-11-22

## Review Summary

ADR review completed for this change. The change is a tactical UI behavior —
auto-focusing the new-item dialog's text field via an opt-in flag on a shared
composable. It introduces no long-term architectural commitment, boundary,
contract, or technology choice.

## In-Force ADRs Reviewed

- ADR-0001 (manual DI via `AppContainer`)
- ADR-0002 (single `:app` module)
- ADR-0003 (Gradle Kotlin DSL version catalog)
- ADR-0004 (dependency and version policy)
- ADR-0005 (package root `org.mateuszmidor.shoplist`)
- ADR-0006 (Room 3 via KSP for local persistence)
- ADR-0007 (Navigation Compose with type-safe routes)
- ADR-0008 (ViewModels via CreationExtras; reads via `Flow`, writes via repository)
- ADR-0009 (items relate to lists with cascade delete)
- ADR-0010 (derived aggregates as a single data-layer query)
- ADR-0011 (pure business logic in a `domain` package)
- ADR-0012 (navigation routes may carry a list of identifiers)

No ADR references `Supersedes`; all listed ADRs are in force. None is
superseded, revisited, or contradicted by this change.

## New Durable ADRs Created

- None - no major durable architectural decisions were introduced. Auto-focus
  is a local UI detail in `NameDialog`, not an architectural pattern or
  cross-change contract.

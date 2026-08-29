# ADR Review Manifest

- Status: completed
- Review date: 2026-08-29

## Review Summary

ADR review completed for this change. The design introduces one durable
architectural decision — pure business logic living in a new `domain` package —
recorded as a new repository-level ADR (ADR-0011). All other design choices
(batch atomicity via a single Room `@Insert`, sequential creation timestamps,
FAB menu pattern, live-parse confirm gating) are tactical implementation
details consistent with the already-in-force ADR set and do not introduce new
long-term commitments. No in-force ADR is superseded or revisited.

## In-Force ADRs Reviewed

- ADR-0001 manual-di-via-appcontainer -> manual DI via `AppContainer` (in force)
- ADR-0002 single-app-module -> single `:app` module, layers kept clean via
  packages (in force)
- ADR-0003 gradle-kotlin-dsl-version-catalog -> Kotlin DSL + central version
  catalog (in force)
- ADR-0004 dependency-version-policy -> current stable versions only, no
  experimental libraries (in force)
- ADR-0005 package-naming-org-mateuszmidor-shoplist ->
  `org.mateuszmidor.shoplist` (in force)
- ADR-0006 use-room-3-for-local-data-layer -> Room 3.0 via KSP (in force)
- ADR-0007 use-navigation-compose-with-type-safe-routes -> Navigation Compose
  with `@Serializable` routes and a reusable UUID serializer (in force)
- ADR-0008 source-viewmodels-via-creationextras-initializer -> ViewModels via
  `viewModel(CreationExtras)` initializer reading repositories from
  `AppContainer`; reads via Flow / writes via repository (in force)
- ADR-0009 items-relate-to-lists-with-cascade-delete -> `shopping_items` owned
  by `list_id`, cascade on list delete (in force)
- ADR-0010 lists-expose-derived-aggregates-as-single-query -> derived
  aggregates computed in the data layer as one reactive query (in force)

## New Durable ADRs Created

- `adr/0011-pure-logic-in-domain-package.md` - pure business logic lives in a
  new `org.mateuszmidor.shoplist.domain` package. Establishes the seam for
  platform-independent, side-effect-free logic (starting with
  `ListonicImportParser`), separating it from the IO-bound data layer and the
  ViewModels that coordinate it.
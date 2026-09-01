# ADR Review Manifest

- Status: completed
- Review date: 2026-09-01

## Review Summary

ADR review completed for this change. The design's decisions — a one-shot
snapshot query in the Room data layer, an export formatter joining item names
into bullet-prefixed lines, the ViewModel holding the clipboard, and a context-
menu entry point — are all tactical extensions of already-in-force ADRs: the
formatter's placement in the `domain` package follows ADR-0011, which explicitly
anticipates formatting logic as a future unit there; passing the clipboard
(`Context`-sourced) into the ViewModel is a direct application of the manual DI
wiring pattern (ADR-0001) and CreationExtras ViewModel sourcing (ADR-0008); and
the snapshot query extends the existing Room data layer (ADR-0006). No new
long-term architectural commitment is introduced, and no in-force ADR is
superseded or revisited.

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
- ADR-0011 pure-logic-in-domain-package -> pure business logic (import/export
  parsing, formatting, validation) lives in a `domain` package (in force)

## New Durable ADRs Created

- None - no major durable architectural decisions were introduced. The design
  is coherent with the in-force ADR set, and no new repository-level ADR files
  were created.

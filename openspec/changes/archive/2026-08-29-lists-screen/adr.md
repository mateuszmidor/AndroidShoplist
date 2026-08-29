# ADR Review Manifest

- Status: completed
- Review date: 2026-08-29

## Review Summary

ADR review completed for this change. The design establishes the navigation
technology and the per-screen ViewModel pattern that the items screen and all
future changes will mirror; both are recorded as new repository-level ADRs.

## In-Force ADRs Reviewed

- ADR-0001 manual-dependancy-container -> manual DI via `AppContainer` (in force)
- ADR-0002 single-app-module -> single `:app` module, layers kept clean via
  packages (in force)
- ADR-0003 gradle-kotlin-dsl-version-catalog -> Kotlin DSL + central version
  catalog (in force)
- ADR-0004 dependency-version-policy -> current stable versions only, no
  experimental libraries (in force)
- ADR-0005 package-naming -> `org.mateuszmidor.shoplist` (in force)
- ADR-0006 use-room-3 -> Room 3.0 via KSP (in force)

No ADR is superseded by this change; the in-force set above constrains the
design and is not contradicted by it.

## New Durable ADRs Created

- `adr/0007-use-navigation-compose-with-type-safe-routes.md` - adopts Jetpack
  Navigation Compose (2.10.0) with `@Serializable` route types and a reusable
  `KSerializer<UUID>` for list-id route arguments.
- `adr/0008-source-viewmodels-via-creationextras-initializer.md` - per-screen
  ViewModels are obtained via the `viewModel(CreationExtras)` initializer
  reading repositories from `AppContainer`; state is the repository Flow via
  `stateIn` with `WhileSubscribed`, and writes delegate to the repository.

## Implementation Notes (Deviations Discovered During Implementation)

ADR-0007 states route arguments use a reusable `KSerializer<UUID>`.
Implementation revealed Navigation Compose 2.10.0 cannot resolve a KSerializer
declared directly on a route class *field* (`@Serializable(with = ...)` fails
at runtime with "Cannot find KSerializer"), and custom argument types still
need a `NavType` supplied via the destination `typeMap`. The intent is
preserved with a small mechanical refinement:

- `Items.listId` is typed `ListId`, a `@Serializable` wrapper around `UUID`
  with its serializer declared on the class declaration
  (`ListIdSerializer`, canonical string form — the same encoding the ADR
  requires), plus `ListIdNavType` passed as
  `typeMap = mapOf(typeOf<ListId>() to ListIdNavType)` on the `Items` route.
- Result verified on device: `Items` decodes the canonical UUID string back
  to a `ListId`, and the placeholder screen renders the original id.
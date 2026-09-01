# export-items Design

## Context

The app (manual DI via `AppContainer`, ADR-0001; single `:app` module, ADR-0002)
has a Lists screen that renders all shopping lists with a long-press context
menu (Rename / Delete). Change 06 added Listonic-format **import** (parse pasted
text into item names) with a pure parse routine living in a `domain` package per
ADR-0011, alongside a new snapshot-capable item data layer. There is currently
no way to get list contents out of the app.

This change adds the inverse direction: exporting every item of a list to the
clipboard as bullet-prefixed text whose shape the existing import parser accepts
verbatim, completing import/export symmetry.

The `ListsViewModel` today depends only on `ShoppingListRepository`; export
requires reading items for a list, which currently exists only as a reactive
`Flow` (not a one-shot fetch), and requires a clipboard component, which needs an
Android `Context`.

In-force constraints to stay coherent with:
- ADR-0001 (AppContainer owns the object graph)
- ADR-0006 (Room 3 data layer)
- ADR-0008 (ViewModels via CreationExtras; reads via Flow, writes via repository;
  ViewModels stay stateless w.r.t. UI state)
- ADR-0011 (pure business logic in the `domain` package)

## Goals / Non-Goals

**Goals:**
- Add an "Export items" action to the Lists screen long-press context menu.
- Fetch all items of the selected list in one shot (bought and unbought).
- Format them as one bullet-prefixed (`• `) line per item, round-trip compatible
  with the existing import parser.
- Copy the text to the device clipboard and confirm with a Toast.

**Non-Goals:**
- No preview dialog before copying (direct copy).
- No export of multiple lists at once, no file/share-sheet integration, no
  list-name export.
- No change to the buy/order semantics or the existing import feature.

## Decisions

### 1. One-shot snapshot query in the data layer

The DAO currently exposes only `observeByList(...)` returning `Flow`. Add a
non-reactive suspend query `SELECT ... WHERE list_id = :listId` to
`ShoppingItemDao`, exposed through `ShoppingItemRepository#getAllByList(...)`
and implemented in `RoomShoppingItemRepository`.

- Alternative: reuse the reactive `Flow` and `.first()`. Rejected: a directional
  one-shot read is `Flow.first()` overkill here and mixing reactive reads into a
  fire-and-forget export complicates testability. A plain suspend query is the
  idiomatic Room pattern for a single explicit fetch.

### 2. Export formatter lives in the `domain` package (ADR-0011)

Add a pure, stateless `ListonicExportFormatter` in
`org.mateuszmidor.shoplist.domain`, mirroring `ListonicImportParser`. Its single
`format(items: List<ShoppingItemEntity>): String` joins each item name as
`"• $name"` on its own line. Because import strips at most one leading bullet
from each line, an item name containing a literal `•` (or `-`/`*`) could break a
round-trip; this is accepted (see Trade-offs) and the formatter deliberately does
not escape names.

- Alternative: inline the `"• $name"` join inside the ViewModel. Rejected: this
  is pure business logic that must be JVM-unit-tested without Android
  dependencies (ADR-0011), and a named formatter mirrors the import parser for
  symmetry and gives the formatter/parser pair a canonical home.

### 3. Clipboard owner

The ViewModel performs the copy via a `ClipboardManager` obtained from a
`Context`. `ListsViewModel`'s constructor widens to
`ListsViewModel(listRepository, itemRepository, clipboardManager)` and exposes
`exportListItems(listId: UUID)`, which fetches items, formats them, writes to the
clipboard, and triggers a Toast.

- Alternative: the Screen composes/owns the clipboard and Toast, with the
  ViewModel returning formatted text through a one-shot event channel. Rejected:
  at this app's scale that adds a SharedFlow/event-wrapper mechanism for little
  benefit; ViewModels already run in the Android process and the clipboard is a
  trivial system service. Keeping the copy in the ViewModel matches the existing
  pattern where ViewModels are the sole action owner and keeps the Screen thin.

### 4. Clipboard + Toast supplied at the wiring point (App)

The screen cannot rely on `LocalContext` at the ViewModel constructor (it lives
in composition). Instead `App.kt` obtains the clipboard from the ambient
`Context` and passes it into `ListsViewModel`. The Toast requires a `Context`
too; the ViewModel receives a `Context`/`ClipboardManager` together so the Toast
can be shown from the same place.

### 5. Context-menu entry point

Add a third `DropdownMenuItem` "Export items" to the existing long-press menu in
`ListsScreen`, routed through a new `onExportItems(listId)` callback wired to
`viewModel::exportListItems` in `App.kt`. This mirrors how Rename/Delete are
wired today and keeps the menu the single discoverable home for row actions.

## Risks / Trade-offs

- [Item names containing a bullet/`-`/`*` character won't round-trip through
  import (import strips at most one such leading char)] -> Acceptance: the
  feature targets Listonic-style names where this is rare; escaping would
  introduce format divergence from Listonic and complicate the spec. Documented
  as a known limitation.
- [Widening `ListsViewModel` constructor couples it to an Android
  `Context`/`ClipboardManager`, making JVM ViewModel unit tests harder] ->
  Mitigation: keep the pure formatting in the domain formatter (JVM-testable),
  and let the ViewModel's own tests inject a fake/real clipboard layer; the
  Android dependency is a thin final step.
- [Reactive `Flow` order (bought last) vs snapshot query ordering] -> Mitigation:
  the snapshot query reuses the same `ORDER BY bought ASC, created_at ASC` so
  exported output order matches the on-screen Items order.

## Migration Plan

Additive feature: no schema migration, no data migration. Ship behind the
existing app, no rollback concern beyond removing the menu item and the new
repository method if reverted.

## Open Questions

- None affecting in-force ADRs: this change is consistent with the current ADR
  set (formatter in domain per ADR-0011, ViewModel wiring per ADR-0008/0001, Room
  snapshot per ADR-0006). No supersession needed.
- Minor: whether a future "share sheet" should replace the clipboard as the
  destination — out of scope, but the exported-text formatting would be reusable.

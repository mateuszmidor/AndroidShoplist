## Why

Importing from Listonic is a rare action, but it currently shares the items-screen FAB menu with "Add item", the most common action. Every add requires an extra tap through the menu. Moving import to a top-bar overflow action makes adding immediate again while keeping import discoverable.

## What Changes

- The items-screen FAB (+) becomes a single-purpose action: tapping it opens the "New item" dialog directly, with no FAB menu.
- Import moves to a `MoreVert` icon button in the items-screen top app bar. Tapping it opens a dropdown menu containing "Import from Listonic", which opens the existing paste-import dialog.
- The import dialog, parsing, and ViewModel import behavior remain unchanged; only the entry point moves.

## Capabilities

### New Capabilities

- None

### Modified Capabilities

- `items-screen`: The FAB opens the create-item dialog directly instead of showing a menu with "Add item" and "Import from Listonic"; the top app bar gains a `MoreVert` overflow action for rare actions.
- `items-import`: The import entry point moves from the FAB menu to the top-bar `MoreVert` menu; the import dialog, parse rules, and ViewModel behavior are unchanged.

## Impact

- Code: `app/src/main/java/org/mateuszmidor/shoplist/ui/items/ItemsScreen.kt` (TopAppBar actions, FAB onClick, remove FAB menu state, add overflow menu state).
- Specs: `openspec/specs/items-screen/spec.md`, `openspec/specs/items-import/spec.md` (delta sync on archive/sync).
- No changes to `ImportDialog`, `ListonicImportParser`, `ItemsViewModel`, repository/DAO/Room, navigation, or dependencies.
- Verification: build, existing unit/instrumented tests, and manual check on Samsung Galaxy A52 for FAB direct-open and top-bar import menu.
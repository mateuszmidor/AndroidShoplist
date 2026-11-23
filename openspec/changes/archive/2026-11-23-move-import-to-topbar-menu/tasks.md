## 1. Move import entry to TopAppBar overflow

- [x] 1.1 In `ItemsScreen.kt`, add `overflowMenuVisible` state and a `TopAppBar` `actions` slot with a `MoreVert` `IconButton` that opens a `DropdownMenu` containing "Import from Listonic"; selecting it closes the menu and sets `importDialogVisible = true`
- [x] 1.2 Change the FAB `onClick` to open the AddItemDialog directly (`newItemQuery = ""`, `lastAddedName = null`, `createDialogVisible = true`) and remove the FAB `DropdownMenu` and `fabMenuVisible` state
- [x] 1.3 Add required Compose imports (`Icons`, `Icons.Filled.MoreVert`, `Icon`, `IconButton`) and keep `ImportDialog`/`AddItemDialog` behavior unchanged

## 2. Update tests

- [x] 2.1 Update `AddItemDialogMultiAddUiTest.openDialog()` to tap `+` and expect the AddItemDialog directly (remove the "Add item" menu tap)
- [x] 2.2 Update `ItemLookupDiacriticsUiTest` to tap `+` and expect the AddItemDialog directly (remove the "Add item" menu tap)
- [x] 2.3 Search for other tests referencing the FAB "Add item" / "Import from Listonic" menu flow and update them to the new entry points

## 3. Verification

- [x] 3.1 Build the app (`./gradlew assembleDebug`) and confirm it compiles
- [x] 3.2 Run `./gradlew testDebugUnitTest` and `./gradlew connectedDebugAndroidTest`; confirm updated tests pass
- [x] 3.3 Install and launch the debug build on the Samsung Galaxy A52; verify FAB opens AddItemDialog directly, `⋮` menu shows "Import from Listonic", import dialog works as before, and no FAB menu remains
- [x] 3.4 Run `openspec validate move-import-to-topbar-menu --type change --strict`

## 4. Bookkeeping

- [x] 4.1 Add Change 13 to `TASKS.md` and mark it complete after verification
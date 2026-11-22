# newitem-auto-focus

## Why

Adding an item is a frequent, short interaction, but the create-item dialog opens
with its name field unfocused: the user must tap the field before typing. That
extra tap interrupts the flow every time an item is added. Auto-focusing the
field (and showing the keyboard) removes the friction.

## What Changes

- The "New item" dialog on the items screen opens with its name field focused
  and the soft keyboard ready, so the user can type immediately.
- The shared `NameDialog` gains an opt-in `autoFocus` capability. Only the
  create-item dialog enables it; the new-list, rename-item, rename-list, and
  import dialogs keep their current focus behavior.
- No data, ViewModel, navigation, or dependency changes. Focus is transient UI
  state local to the dialog.

## Capabilities

### New Capabilities

- None.

### Modified Capabilities

- `items-screen`: The add-item FAB dialog now opens with the name field focused
  and the soft keyboard requested; the existing add, cancel, and blank-name
  behavior is unchanged.

## Impact

- **Modified code**: `ui/common/NameDialog.kt` (opt-in auto-focus support via
  `FocusRequester` + `LaunchedEffect`), `ui/items/ItemsScreen.kt` (enable
  auto-focus for the "New item" dialog).
- **Unchanged**: ViewModels, repositories, Room schema, navigation, and
  dependencies. No schema version change or migration.
- **Tests**: Existing unit and integration tests are unaffected. Focus and
  keyboard behavior are verified manually on the target device (Samsung Galaxy
  A52); UI automation remains deferred per ARCHITECTURE.md.
- **Not in scope**: Auto-focus for the new-list, rename, or import dialogs;
  IME action handling (e.g. "Done" submitting the item); dialog redesign.

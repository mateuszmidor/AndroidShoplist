## Why

The "New item" dialog mixes up two different endings. After un-marking a bought item the work is already done — un-marking is applied immediately — yet the only way out is a button labelled "Cancel", which reads as if it would undo the un-mark. At the same time "Add" sits beside it in the same footer, so the dialog presents adding and cancelling as a single either/or, and every additional item needs the dialog reopened. The dialog should behave like a short adding session: add as many items as needed, un-mark what is already bought, then leave with "Done".

## What Changes

- Rename the dialog's dismiss action from "Cancel" to "Done"; dismissing still closes the dialog without creating anything.
- Move the "Add" action out of the footer and next to the name field, so the footer contains only "Done".
- Adding no longer closes the dialog. It persists the trimmed name, clears the name field, keeps the field focused with the keyboard open, and shows a confirmation naming the item just added.
- Map the keyboard's Done action to the same add behaviour, so a run of items can be entered without touching the buttons.
- The confirmation and the existing duplicate warning share one hint slot: a pending typed name that exactly matches an item shows "Already on this list"; otherwise the most recent add is confirmed; otherwise nothing is shown.
- Update the `items-screen` specification to describe the multi-add session and the "Done" action, and cover it with an instrumented Compose test.

## Capabilities

### New Capabilities

- None

### Modified Capabilities

- `items-screen`: The create-item dialog becomes a multi-add session — "Done" replaces "Cancel", "Add" moves beside the name field, a successful add clears the field and confirms itself instead of closing the dialog, and the keyboard Done key adds too.

## Impact

- Code: `app/src/main/java/org/mateuszmidor/shoplist/ui/items/ItemsScreen.kt` (`AddItemDialog` layout and actions, `ItemsScreen` dialog state for the last added name).
- Tests: new instrumented Compose test under `app/src/androidTest/java/org/mateuszmidor/shoplist/ui/items/`; the existing `ItemLookupDiacriticsUiTest` is unaffected because it never taps "Add" or "Cancel".
- No changes to `ItemsViewModel`, the repository, DAO, Room schema, navigation, `NameDialog`, or dependencies.

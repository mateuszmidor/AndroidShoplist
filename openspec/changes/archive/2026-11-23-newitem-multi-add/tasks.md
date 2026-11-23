## 1. Reshape the create-item dialog

- [x] 1.1 In `AddItemDialog` (`app/src/main/java/org/mateuszmidor/shoplist/ui/items/ItemsScreen.kt`), replace the standalone name field with a `Row` holding `OutlinedTextField` (`Modifier.weight(1f)`, existing `focusRequester`) and a filled `Button("Add")` (`enabled = addEnabled`, `Modifier.height(56.dp)`, 8 dp spacing, centred vertically)
- [x] 1.2 Add a `lastAddedName: String?` parameter and render the single hint line under the field row: the error "Already on this list" when the query is non-blank and `addEnabled` is false, otherwise `Added "<lastAddedName>"` when it is non-null, otherwise nothing
- [x] 1.3 Replace the footer `Cancel`/`Add` row with a single end-aligned `TextButton("Done")` that calls `onDismiss`
- [x] 1.4 Add `keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done)` and `keyboardActions = KeyboardActions(onDone = ...)` that run the add handler only when `addEnabled`, then restore focus via `focusRequester.requestFocus()` and `LocalSoftwareKeyboardController.current?.show()`

## 2. Wire the multi-add session in `ItemsScreen`

- [x] 2.1 Add `var lastAddedName by rememberSaveable { mutableStateOf<String?>(null) }` beside `newItemQuery`
- [x] 2.2 Replace the current `onConfirm` body with an add handler that calls `onAddItem(newItemQuery)`, sets `lastAddedName` to the trimmed query, and clears `newItemQuery` without hiding the dialog
- [x] 2.3 Clear `lastAddedName` in `onQueryChange` and when the FAB's "Add item" opens the dialog, so typing or a new session never shows a stale confirmation
- [x] 2.4 Pass `lastAddedName` into `AddItemDialog`; keep passing `query`, `matches`, and `addEnabled` unchanged

## 3. Instrumented test

- [x] 3.1 Add `app/src/androidTest/java/org/mateuszmidor/shoplist/ui/items/AddItemDialogMultiAddUiTest.kt` following `ItemLookupDiacriticsUiTest`'s container/seed pattern, with an empty current list
- [x] 3.2 Test that adding "Milk" keeps the dialog open, empties the field, and shows `Added "Milk"`
- [x] 3.3 Test that typing "Bread" straight after the add produces a "Bread" item (proving the field was cleared, not "MilkBread") and that the confirmation is replaced by `Added "Bread"`
- [x] 3.4 Test that tapping "Done" with "Ham" typed closes the dialog and creates no "Ham", while both "Milk" and "Bread" are shown on the items screen
- [x] 3.5 Test the keyboard path with `performImeAction()`: a typed "Milk" is added, the dialog stays open with an empty field, and the confirmation names "Milk"
- [x] 3.6 Test hint precedence: after an add, typing the same name shows "Already on this list" and the confirmation is gone

## 4. Verification

- [x] 4.1 Build the app (`./gradlew assembleDebug`) and confirm it compiles
- [x] 4.2 Install and launch the debug build on the Samsung Galaxy A52; verify multi-add, field clearing, focus/keyboard retention after both Add and the keyboard Done key, hint precedence, un-mark-then-Done, the 56 dp Add button beside a long name, and rotation with the dialog open
- [x] 4.3 Run `./gradlew testDebugUnitTest` and `./gradlew connectedDebugAndroidTest`, confirming the new test and the existing `ItemLookupDiacriticsUiTest` pass
- [x] 4.4 Run `openspec validate newitem-multi-add --type change --strict` before archive

## 5. Bookkeeping

- [x] 5.1 Add Change 12 to `TASKS.md` and mark it complete after verification

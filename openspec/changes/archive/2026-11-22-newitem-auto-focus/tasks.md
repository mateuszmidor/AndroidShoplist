## 1. Shared dialog support

- [x] 1.1 Add `autoFocus: Boolean = false` to `NameDialog` in `app/src/main/java/org/mateuszmidor/shoplist/ui/common/NameDialog.kt`
- [x] 1.2 Remember a `FocusRequester` in `NameDialog`, attach it to the `OutlinedTextField` via `Modifier.focusRequester`, and request focus from a `LaunchedEffect` when `autoFocus` is true
- [x] 1.3 Confirm the default `autoFocus = false` leaves the new-list and rename dialogs' focus behavior unchanged

## 2. Enable auto-focus for the new-item dialog

- [x] 2.1 Pass `autoFocus = true` to the `NameDialog` opened by the "Add item" action in `ItemsScreen` (`app/src/main/java/org/mateuszmidor/shoplist/ui/items/ItemsScreen.kt`)
- [x] 2.2 Build the app (`./gradlew assembleDebug`) and confirm it compiles

## 3. Verification on the target device

- [x] 3.1 Install and launch the debug build on the Samsung Galaxy A52
- [x] 3.2 Verify FAB -> "Add item" opens the dialog with the name field focused and the soft keyboard visible
- [x] 3.3 Verify the user can type immediately; "Add" creates the item, "Cancel" creates nothing, and a blank/whitespace-only name remains blocked
- [x] 3.4 Verify the rename-item, rename-list, new-list, and import dialogs do not auto-focus (no regression)
- [x] 3.5 Verify the keyboard hides when the dialog closes
- [x] 3.6 Verify the "Add" button remains reachable while the keyboard is open; if not, apply the fallback from `design.md` (frame wait and/or `LocalSoftwareKeyboardController.show()`)
- [x] 3.7 Run the existing JVM unit tests (`./gradlew testDebugUnitTest`) and instrumented tests (`./gradlew connectedDebugAndroidTest`) to confirm no regressions
- [x] 3.8 Run `openspec validate newitem-auto-focus --type change --strict` before archive

## 4. Bookkeeping

- [x] 4.1 Mark Change 09 in `TASKS.md` complete after verification

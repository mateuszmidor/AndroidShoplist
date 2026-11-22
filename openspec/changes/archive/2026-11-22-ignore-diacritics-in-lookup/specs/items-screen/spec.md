## MODIFIED Requirements

### Requirement: Users SHALL be able to add an item via the FAB

The items screen SHALL provide a FAB (+) that opens a full-screen create-item
dialog with a name field and a list of matching items from the current list.
The create-item dialog SHALL open with the name field focused and the soft
keyboard requested, so the user can type immediately without tapping the field.

The dialog SHALL display every bought item of the current list when the name
field is empty. As the user types, it SHALL display only bought items whose
name contains the trimmed input, ignoring case and Polish diacritics. For
matching purposes, `a`/`ą`, `c`/`ć`, `e`/`ę`, `l`/`ł`, `n`/`ń`, `o`/`ó`,
`s`/`ś`, and `z`/`ź`/`ż` SHALL be equivalent in both directions, including
uppercase forms. This equivalence SHALL apply to matching only: stored names
and displayed labels SHALL keep their original spelling. Items from other lists
SHALL NOT be displayed. Tapping a displayed item SHALL un-mark it immediately
and SHALL keep the dialog open.

The "Add" action SHALL be disabled when the trimmed name, compared ignoring
case and the same Polish diacritics, exactly matches any item in the current
list, whether bought or not, and the dialog SHALL show an indication that the
name is already on the list. A partial match SHALL be displayed but SHALL NOT
disable "Add". Confirming with a non-empty trimmed name SHALL create an item in
the current list. The dialog SHALL close without creating when cancelled, and
SHALL NOT submit a blank or whitespace-only name. The rename, new-list, and
import dialogs SHALL retain their existing behavior.

Feature: items-screen
Rule: A FAB opens a create dialog that persists a new item in the current list and reveals bought items that may already match, ignoring case and Polish diacritics

#### Scenario: Add an item from the FAB

- **GIVEN** the items screen for a list is visible
- **WHEN** the user taps the FAB, enters "Milk", and confirms
- **THEN** an item named "Milk" is persisted in the current list and shown on the
  items screen
- **AND** the create dialog is closed

#### Scenario: Name field is focused when the create dialog opens

- **GIVEN** the items screen for a list is visible
- **WHEN** the user taps the FAB and chooses "Add item"
- **THEN** the create-item dialog opens with the name field focused
- **AND** the soft keyboard is shown, so the user can type immediately

#### Scenario: All bought items are shown when the name is empty

- **GIVEN** the current list contains both bought and unbought items
- **WHEN** the user opens the create-item dialog without typing a name
- **THEN** every bought item of the current list is displayed in the dialog
- **AND** no unbought item and no item from another list is displayed

#### Scenario: Results filter while typing

- **GIVEN** the create-item dialog is open
- **WHEN** the user types "milk"
- **THEN** the dialog displays only bought items of the current list whose names
  contain "milk", ignoring case and Polish diacritics
- **AND** the displayed results update as the input changes

#### Scenario: Plain input matches a name with Polish diacritics

- **GIVEN** the current list contains a bought item named "nić dentystyczna"
- **WHEN** the user types "nic"
- **THEN** "nić dentystyczna" is displayed as a matching bought item
- **AND** the item label still shows "nić dentystyczna"

#### Scenario: Input with Polish diacritics matches a plain name

- **GIVEN** the current list contains a bought item named "losos"
- **WHEN** the user types "łosoś"
- **THEN** "losos" is displayed as a matching bought item

#### Scenario: Un-mark a matched item from the dialog

- **GIVEN** the create-item dialog displays a bought item "Milk"
- **WHEN** the user taps that displayed item
- **THEN** the item is marked as not bought on the current list
- **AND** the create-item dialog stays open
- **AND** the item no longer appears in the dialog's results

#### Scenario: Exact match disables Add

- **GIVEN** the current list already contains an item named "Milk"
- **WHEN** the user enters "  milk  " in the create-item dialog
- **THEN** the "Add" action is disabled
- **AND** the dialog indicates that the name is already on the list
- **AND** no new item is created

#### Scenario: Diacritic-insensitive exact match disables Add

- **GIVEN** the current list already contains an item named "nić"
- **WHEN** the user enters "nic" in the create-item dialog
- **THEN** the "Add" action is disabled
- **AND** the dialog indicates that the name is already on the list
- **AND** no new item named "nic" is created

#### Scenario: Partial match does not disable Add

- **GIVEN** the current list contains a bought item named "Eggplant"
- **WHEN** the user enters "egg" in the create-item dialog
- **THEN** "Eggplant" is displayed as a matching bought item
- **AND** the "Add" action remains enabled
- **AND** confirming creates a new item named "egg"

#### Scenario: Cancel the create dialog

- **GIVEN** the create-item dialog is open
- **WHEN** the user cancels
- **THEN** the dialog closes
- **AND** no item is created

#### Scenario: Blank name is not submitted

- **GIVEN** the create-item dialog is open
- **WHEN** the user enters only whitespace and confirms
- **THEN** no item is created
- **AND** the dialog remains open (or the confirm action is disabled)

package org.mateuszmidor.shoplist.ui.items

import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import java.util.UUID
import org.mateuszmidor.shoplist.data.ShoppingItemEntity
import org.mateuszmidor.shoplist.domain.ListonicImportParser
import org.mateuszmidor.shoplist.ui.common.NameDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemsScreen(
    uiState: ItemsUiState,
    onAddItem: (String) -> Unit,
    onRenameItem: (UUID, String) -> Unit,
    onDeleteItem: (UUID) -> Unit,
    onToggleBought: (UUID) -> Unit,
    onImportText: (String) -> Unit,
    onBack: () -> Unit,
) {
    var createDialogVisible by rememberSaveable { mutableStateOf(false) }
    var newItemQuery by rememberSaveable { mutableStateOf("") }
    var importDialogVisible by rememberSaveable { mutableStateOf(false) }
    var fabMenuVisible by rememberSaveable { mutableStateOf(false) }
    var menuTargetId by rememberSaveable { mutableStateOf<UUID?>(null) }
    var renameTargetId by rememberSaveable { mutableStateOf<UUID?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(uiState.listName ?: "Items") },
                navigationIcon = {
                    TextButton(onClick = onBack) { Text("Back") }
                },
            )
        },
        floatingActionButton = {
            Box {
                FloatingActionButton(onClick = { fabMenuVisible = true }) {
                    Text("+", style = MaterialTheme.typography.headlineMedium)
                }
                DropdownMenu(
                    expanded = fabMenuVisible,
                    onDismissRequest = { fabMenuVisible = false },
                ) {
                    DropdownMenuItem(
                        text = { Text("Add item") },
                        onClick = {
                            fabMenuVisible = false
                            newItemQuery = ""
                            createDialogVisible = true
                        },
                    )
                    DropdownMenuItem(
                        text = { Text("Import from Listonic") },
                        onClick = {
                            fabMenuVisible = false
                            importDialogVisible = true
                        },
                    )
                }
            }
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            if (uiState.items.isEmpty()) {
                ItemEmptyHint()
            } else {
                ItemContent(
                    items = uiState.items,
                    menuTargetId = menuTargetId,
                    onMenuChange = { menuTargetId = it },
                    onToggleBought = { id -> onToggleBought(id) },
                    onRename = { id -> renameTargetId = id; menuTargetId = null },
                    onDelete = { id -> onDeleteItem(id); menuTargetId = null },
                )
            }
        }
    }

    if (createDialogVisible) {
        AddItemDialog(
            query = newItemQuery,
            matches = matchingBoughtItems(newItemQuery, uiState.items),
            addEnabled = canAddItem(newItemQuery, uiState.items),
            onQueryChange = { newItemQuery = it },
            onUnmark = { id -> onToggleBought(id) },
            onConfirm = {
                onAddItem(newItemQuery)
                newItemQuery = ""
                createDialogVisible = false
            },
            onDismiss = {
                newItemQuery = ""
                createDialogVisible = false
            },
        )
    }

    if (importDialogVisible) {
        ImportDialog(
            onConfirm = { text ->
                onImportText(text)
                importDialogVisible = false
            },
            onDismiss = { importDialogVisible = false },
        )
    }

    uiState.items.firstOrNull { it.id == renameTargetId }?.let { target ->
        NameDialog(
            title = "Rename item",
            initialName = target.name,
            confirmLabel = "Rename",
            onConfirm = { name ->
                onRenameItem(target.id, name)
                renameTargetId = null
            },
            onDismiss = { renameTargetId = null },
        )
    }
}

@Composable
private fun AddItemDialog(
    query: String,
    matches: List<ShoppingItemEntity>,
    addEnabled: Boolean,
    onQueryChange: (String) -> Unit,
    onUnmark: (UUID) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    val focusRequester = remember { FocusRequester() }
    LaunchedEffect(Unit) { focusRequester.requestFocus() }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false,
        ),
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .padding(16.dp)
                .imePadding(),
            shape = MaterialTheme.shapes.large,
            color = MaterialTheme.colorScheme.surface,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
            ) {
                Text("New item", style = MaterialTheme.typography.headlineSmall)
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = query,
                    onValueChange = onQueryChange,
                    label = { Text("Name") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester),
                )
                if (query.isNotBlank() && !addEnabled) {
                    Text(
                        text = "Already on this list",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 4.dp),
                    )
                }
                Spacer(Modifier.height(8.dp))
                if (matches.isEmpty()) {
                    Text(
                        text = if (query.isBlank()) "No bought items in this list." else "No matching bought items.",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(vertical = 16.dp),
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        contentPadding = PaddingValues(vertical = 4.dp),
                    ) {
                        items(matches, key = { it.id }) { item ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onUnmark(item.id) }
                                    .padding(horizontal = 4.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Checkbox(
                                    checked = true,
                                    onCheckedChange = { onUnmark(item.id) },
                                )
                                Text(
                                    text = item.name,
                                    style = MaterialTheme.typography.bodyLarge,
                                    modifier = Modifier.padding(start = 8.dp),
                                )
                            }
                            HorizontalDivider()
                        }
                    }
                }
                Spacer(Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                ) {
                    TextButton(onClick = onDismiss) { Text("Cancel") }
                    TextButton(onClick = onConfirm, enabled = addEnabled) { Text("Add") }
                }
            }
        }
    }
}

@Composable
private fun ImportDialog(
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    var text by rememberSaveable { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Import from Listonic") },
        text = {
            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                label = { Text("Pasted list") },
                minLines = 4,
                maxLines = 6,
            )
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirm(text) },
                enabled = ListonicImportParser.parse(text).isNotEmpty(),
            ) { Text("Import") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        },
    )
}

@Composable
private fun ItemContent(
    items: List<ShoppingItemEntity>,
    menuTargetId: UUID?,
    onMenuChange: (UUID?) -> Unit,
    onToggleBought: (UUID) -> Unit,
    onRename: (UUID) -> Unit,
    onDelete: (UUID) -> Unit,
) {
    LazyColumn(contentPadding = PaddingValues(vertical = 8.dp)) {
        items(items, key = { it.id }) { item ->
            ItemRow(
                item = item,
                menuExpanded = menuTargetId == item.id,
                onToggleBought = { onToggleBought(item.id) },
                onLongClick = { onMenuChange(item.id) },
                onDismissMenu = { onMenuChange(null) },
                onRename = { onRename(item.id) },
                onDelete = { onDelete(item.id) },
            )
            HorizontalDivider()
        }
    }
}

@Composable
private fun ItemRow(
    item: ShoppingItemEntity,
    menuExpanded: Boolean,
    onToggleBought: () -> Unit,
    onLongClick: () -> Unit,
    onDismissMenu: () -> Unit,
    onRename: () -> Unit,
    onDelete: () -> Unit,
) {
    Box {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .combinedClickable(onClick = onToggleBought, onLongClick = onLongClick)
                .padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Checkbox(
                checked = item.bought,
                onCheckedChange = { onToggleBought() },
            )
            Text(
                text = item.name,
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = if (item.bought) MaterialTheme.colorScheme.onSurfaceVariant else Color.Unspecified,
                textDecoration = if (item.bought) TextDecoration.LineThrough else TextDecoration.None,
                modifier = Modifier.weight(1f),
            )
        }
        DropdownMenu(
            expanded = menuExpanded,
            onDismissRequest = onDismissMenu,
        ) {
            DropdownMenuItem(text = { Text("Rename") }, onClick = onRename)
            DropdownMenuItem(text = { Text("Delete") }, onClick = onDelete)
        }
    }
}

@Composable
private fun ItemEmptyHint() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "No items yet. Tap + to add one.",
            style = MaterialTheme.typography.bodyLarge,
        )
    }
}


package org.mateuszmidor.shoplist.ui.items

import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import java.util.UUID
import org.mateuszmidor.shoplist.data.ShoppingItemEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemsScreen(
    uiState: ItemsUiState,
    onAddItem: (String) -> Unit,
    onRenameItem: (UUID, String) -> Unit,
    onDeleteItem: (UUID) -> Unit,
    onToggleBought: (UUID) -> Unit,
    onBack: () -> Unit,
) {
    var createDialogVisible by rememberSaveable { mutableStateOf(false) }
    var menuTargetId by rememberSaveable { mutableStateOf<UUID?>(null) }
    var renameTargetId by rememberSaveable { mutableStateOf<UUID?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Items") },
                navigationIcon = {
                    TextButton(onClick = onBack) { Text("Back") }
                },
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { createDialogVisible = true }) {
                Text("+", style = MaterialTheme.typography.headlineMedium)
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
        NameDialog(
            title = "New item",
            initialName = "",
            confirmLabel = "Add",
            onConfirm = { name ->
                onAddItem(name)
                createDialogVisible = false
            },
            onDismiss = { createDialogVisible = false },
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

@Composable
private fun NameDialog(
    title: String,
    initialName: String,
    confirmLabel: String,
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    var name by rememberSaveable(initialName) { mutableStateOf(initialName) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Name") },
                singleLine = true,
            )
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirm(name) },
                enabled = name.trim().isNotEmpty(),
            ) { Text(confirmLabel) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        },
    )
}

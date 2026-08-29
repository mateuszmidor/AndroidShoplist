package org.mateuszmidor.shoplist.ui.lists

import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import java.util.UUID
import org.mateuszmidor.shoplist.data.ShoppingListEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListsScreen(
    uiState: ListsUiState,
    onCreateList: (String) -> Unit,
    onRenameList: (UUID, String) -> Unit,
    onDeleteList: (UUID) -> Unit,
    onOpenList: (UUID) -> Unit,
) {
    var createDialogVisible by rememberSaveable { mutableStateOf(false) }
    var menuTargetId by rememberSaveable { mutableStateOf<UUID?>(null) }
    var renameTargetId by rememberSaveable { mutableStateOf<UUID?>(null) }

    Scaffold(
        topBar = { TopAppBar(title = { Text("ShopList") }) },
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
            if (uiState.lists.isEmpty()) {
                ListEmptyHint()
            } else {
                ListContent(
                    lists = uiState.lists,
                    menuTargetId = menuTargetId,
                    onOpenList = onOpenList,
                    onMenuChange = { menuTargetId = it },
                    onRename = { id -> renameTargetId = id; menuTargetId = null },
                    onDelete = { id -> onDeleteList(id); menuTargetId = null },
                )
            }
        }
    }

    if (createDialogVisible) {
        NameDialog(
            title = "New list",
            initialName = "",
            confirmLabel = "Create",
            onConfirm = { name ->
                onCreateList(name)
                createDialogVisible = false
            },
            onDismiss = { createDialogVisible = false },
        )
    }

    uiState.lists.firstOrNull { it.id == renameTargetId }?.let { target ->
        NameDialog(
            title = "Rename list",
            initialName = target.name,
            confirmLabel = "Rename",
            onConfirm = { name ->
                onRenameList(target.id, name)
                renameTargetId = null
            },
            onDismiss = { renameTargetId = null },
        )
    }
}

@Composable
private fun ListContent(
    lists: List<ShoppingListEntity>,
    menuTargetId: UUID?,
    onOpenList: (UUID) -> Unit,
    onMenuChange: (UUID?) -> Unit,
    onRename: (UUID) -> Unit,
    onDelete: (UUID) -> Unit,
) {
    LazyColumn(contentPadding = PaddingValues(vertical = 8.dp)) {
        items(lists, key = { it.id }) { list ->
            ListRow(
                list = list,
                menuExpanded = menuTargetId == list.id,
                onClick = { onOpenList(list.id) },
                onLongClick = { onMenuChange(list.id) },
                onDismissMenu = { onMenuChange(null) },
                onRename = { onRename(list.id) },
                onDelete = { onDelete(list.id) },
            )
            HorizontalDivider()
        }
    }
}

@Composable
private fun ListRow(
    list: ShoppingListEntity,
    menuExpanded: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    onDismissMenu: () -> Unit,
    onRename: () -> Unit,
    onDelete: () -> Unit,
) {
    Box {
        Text(
            text = list.name,
            style = MaterialTheme.typography.bodyLarge,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .fillMaxWidth()
                .combinedClickable(onClick = onClick, onLongClick = onLongClick)
                .padding(horizontal = 16.dp, vertical = 16.dp),
        )
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
private fun ListEmptyHint() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "No lists yet. Tap + to create one.",
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
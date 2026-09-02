package org.mateuszmidor.shoplist.ui.lists

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import java.util.UUID
import org.mateuszmidor.shoplist.data.ListSummary
import org.mateuszmidor.shoplist.ui.common.NameDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListsScreen(
    uiState: ListsUiState,
    onCreateList: (String) -> Unit,
    onRenameList: (UUID, String) -> Unit,
    onDeleteList: (UUID) -> Unit,
    onExportItems: (UUID) -> Unit,
    onOpenList: (UUID) -> Unit,
    onEnterSelection: (UUID) -> Unit,
    onToggleSelection: (UUID) -> Unit,
    onClearSelection: () -> Unit,
    onCombine: (List<UUID>) -> Unit,
) {
    var createDialogVisible by rememberSaveable { mutableStateOf(false) }
    var renameTargetId by rememberSaveable { mutableStateOf<UUID?>(null) }
    var deleteTargetId by rememberSaveable { mutableStateOf<UUID?>(null) }
    var menuExpandedId by rememberSaveable { mutableStateOf<UUID?>(null) }

    BackHandler(enabled = uiState.selectionMode, onBack = onClearSelection)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (uiState.selectionMode) "Select lists" else "ShopList") },
                navigationIcon = {
                    if (uiState.selectionMode) {
                        TextButton(onClick = onClearSelection) { Text("Cancel") }
                    }
                },
                actions = {
                    if (uiState.selectionMode) {
                        TextButton(
                            onClick = { onCombine(uiState.selectedIds.toList()) },
                            enabled = uiState.selectedIds.isNotEmpty(),
                        ) { Text("Combine") }
                    }
                },
            )
        },
        floatingActionButton = {
            if (!uiState.selectionMode) {
                FloatingActionButton(onClick = { createDialogVisible = true }) {
                    Text("+", style = MaterialTheme.typography.headlineMedium)
                }
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
                    selectionMode = uiState.selectionMode,
                    selectedIds = uiState.selectedIds,
                    onOpenList = onOpenList,
                    onEnterSelection = onEnterSelection,
                    onToggleSelection = onToggleSelection,
                    onRename = { id -> renameTargetId = id },
                    onDelete = { id -> deleteTargetId = id },
                    onExport = onExportItems,
                    menuExpandedId = menuExpandedId,
                    onMenuExpand = { id -> menuExpandedId = id },
                    onMenuDismiss = { menuExpandedId = null },
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

    uiState.lists.firstOrNull { it.id == deleteTargetId }?.let { target ->
        AlertDialog(
            onDismissRequest = { deleteTargetId = null },
            title = { Text("Delete list") },
            text = {
                Text("Delete list \"${target.name}\" and its ${target.totalCount} items?")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDeleteList(target.id)
                        deleteTargetId = null
                    },
                ) { Text("Delete") }
            },
            dismissButton = {
                TextButton(onClick = { deleteTargetId = null }) { Text("Cancel") }
            },
        )
    }
}

@Composable
private fun ListContent(
    lists: List<ListSummary>,
    selectionMode: Boolean,
    selectedIds: Set<UUID>,
    onOpenList: (UUID) -> Unit,
    onEnterSelection: (UUID) -> Unit,
    onToggleSelection: (UUID) -> Unit,
    onRename: (UUID) -> Unit,
    onDelete: (UUID) -> Unit,
    onExport: (UUID) -> Unit,
    menuExpandedId: UUID?,
    onMenuExpand: (UUID) -> Unit,
    onMenuDismiss: () -> Unit,
) {
    LazyColumn(contentPadding = PaddingValues(vertical = 8.dp)) {
        items(lists, key = { it.id }) { list ->
            ListRow(
                list = list,
                selected = list.id in selectedIds,
                selectionMode = selectionMode,
                onClick = {
                    if (selectionMode) onToggleSelection(list.id) else onOpenList(list.id)
                },
                onLongClick = { onEnterSelection(list.id) },
                onRename = { onRename(list.id) },
                onDelete = { onDelete(list.id) },
                onExport = { onExport(list.id) },
                menuExpanded = menuExpandedId == list.id,
                onMenuExpand = { onMenuExpand(list.id) },
                onMenuDismiss = onMenuDismiss,
            )
            HorizontalDivider()
        }
        item { Spacer(Modifier.height(62.dp)) }
    }
}

@Composable
private fun ListRow(
    list: ListSummary,
    selected: Boolean,
    selectionMode: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    onRename: () -> Unit,
    onDelete: () -> Unit,
    onExport: () -> Unit,
    menuExpanded: Boolean,
    onMenuExpand: () -> Unit,
    onMenuDismiss: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(onClick = onClick, onLongClick = onLongClick)
            .background(
                if (selected) MaterialTheme.colorScheme.primaryContainer
                else Color.Transparent,
            )
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (selectionMode) {
            Checkbox(
                checked = selected,
                onCheckedChange = { onClick() },
            )
        }
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 8.dp, vertical = 4.dp),
        ) {
            Text(
                text = list.name,
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = "${list.totalCount} items · ${list.boughtCount} bought",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        if (!selectionMode) {
            Box {
                IconButton(onClick = onMenuExpand) {
                    Icon(Icons.Default.MoreVert, contentDescription = "Menu")
                }
                DropdownMenu(
                    expanded = menuExpanded,
                    onDismissRequest = onMenuDismiss,
                ) {
                    DropdownMenuItem(
                        text = { Text("Rename") },
                        onClick = { onMenuDismiss(); onRename() },
                    )
                    DropdownMenuItem(
                        text = { Text("Export items") },
                        onClick = { onMenuDismiss(); onExport() },
                    )
                    DropdownMenuItem(
                        text = { Text("Delete") },
                        onClick = { onMenuDismiss(); onDelete() },
                    )
                }
            }
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
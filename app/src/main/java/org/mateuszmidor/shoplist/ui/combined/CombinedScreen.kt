package org.mateuszmidor.shoplist.ui.combined

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import java.util.UUID

/**
 * The transient combined view over the lists selected on the lists screen.
 * Presents a flat, name-sorted merge of the selected lists' items, each row
 * captioned with its source list. Only check/uncheck is offered; the toggle
 * writes through to the owning list, and navigating away (system back discards
 * all) leaves no combined state behind.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CombinedScreen(
    uiState: CombinedUiState,
    onToggleBought: (UUID) -> Unit,
    onBack: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(buildString {
                    append("Combined")
                    uiState.sourceListNames.takeIf { it.isNotEmpty() }?.let { names ->
                        append(": ")
                        append(names.joinToString(", "))
                    }
                }) },
                navigationIcon = {
                    TextButton(onClick = onBack) { Text("Back") }
                },
            )
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            if (uiState.items.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "No items in the combined lists.",
                        style = MaterialTheme.typography.bodyLarge,
                    )
                }
            } else {
                CombinedContent(
                    items = uiState.items,
                    onToggleBought = { id -> onToggleBought(id) },
                )
            }
        }
    }
}

@Composable
private fun CombinedContent(
    items: List<CombinedItem>,
    onToggleBought: (UUID) -> Unit,
) {
    LazyColumn(contentPadding = PaddingValues(vertical = 8.dp)) {
        items(items, key = { it.item.id }) { row ->
            CombinedItemRow(
                item = row,
                onToggleBought = { onToggleBought(row.item.id) },
            )
            HorizontalDivider()
        }
    }
}

@Composable
private fun CombinedItemRow(
    item: CombinedItem,
    onToggleBought: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Checkbox(
            checked = item.item.bought,
            onCheckedChange = { onToggleBought() },
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.item.name,
                style = MaterialTheme.typography.bodyLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = if (item.item.bought) MaterialTheme.colorScheme.onSurfaceVariant else Color.Unspecified,
                textDecoration = if (item.item.bought) TextDecoration.LineThrough else TextDecoration.None,
            )
            Text(
                text = item.sourceListName,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}
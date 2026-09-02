package org.mateuszmidor.shoplist.ui.lists

import org.mateuszmidor.shoplist.data.ListSummary
import java.util.UUID

data class ListsUiState(
    val lists: List<ListSummary> = emptyList(),
    val selectionMode: Boolean = false,
    val selectedIds: Set<UUID> = emptySet(),
)
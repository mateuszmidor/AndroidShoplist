package org.mateuszmidor.shoplist.ui.lists

import org.mateuszmidor.shoplist.data.ListSummary

data class ListsUiState(
    val lists: List<ListSummary> = emptyList(),
)

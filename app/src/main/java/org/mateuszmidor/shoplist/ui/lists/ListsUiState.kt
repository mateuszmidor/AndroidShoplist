package org.mateuszmidor.shoplist.ui.lists

import org.mateuszmidor.shoplist.data.ShoppingListEntity

data class ListsUiState(
    val lists: List<ShoppingListEntity> = emptyList(),
)
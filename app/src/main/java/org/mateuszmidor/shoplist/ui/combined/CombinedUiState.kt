package org.mateuszmidor.shoplist.ui.combined

import org.mateuszmidor.shoplist.data.ShoppingItemEntity

data class CombinedItem(
    val item: ShoppingItemEntity,
    val sourceListName: String,
)

data class CombinedUiState(
    val items: List<CombinedItem> = emptyList(),
    val sourceListNames: List<String> = emptyList(),
)
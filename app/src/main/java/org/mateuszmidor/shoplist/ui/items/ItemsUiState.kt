package org.mateuszmidor.shoplist.ui.items

import org.mateuszmidor.shoplist.data.ShoppingItemEntity

data class ItemsUiState(
    val items: List<ShoppingItemEntity> = emptyList(),
)

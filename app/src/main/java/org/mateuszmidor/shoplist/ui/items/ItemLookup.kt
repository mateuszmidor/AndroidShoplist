package org.mateuszmidor.shoplist.ui.items

import org.mateuszmidor.shoplist.data.ShoppingItemEntity

internal fun matchingBoughtItems(
    query: String,
    items: List<ShoppingItemEntity>,
): List<ShoppingItemEntity> {
    val trimmed = query.trim()
    return items.filter { item ->
        item.bought && (trimmed.isEmpty() || item.name.contains(trimmed, ignoreCase = true))
    }
}

internal fun isExactNameMatch(
    query: String,
    items: List<ShoppingItemEntity>,
): Boolean {
    val trimmed = query.trim()
    return trimmed.isNotEmpty() &&
        items.any { it.name.trim().equals(trimmed, ignoreCase = true) }
}

internal fun canAddItem(
    query: String,
    items: List<ShoppingItemEntity>,
): Boolean = query.trim().isNotEmpty() && !isExactNameMatch(query, items)

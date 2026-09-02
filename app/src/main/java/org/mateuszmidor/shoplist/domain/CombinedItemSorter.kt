package org.mateuszmidor.shoplist.domain

import org.mateuszmidor.shoplist.data.ShoppingItemEntity

/**
 * Pure, stateless sorter that orders the merged items of a combined shopping
 * view: unchecked items first, then checked, each section ordered by name,
 * equal-named items ordered by creation time, and identical
 * (name, createdAt) rows kept in a deterministic order by id.
 */
object CombinedItemSorter {

    fun sort(items: List<ShoppingItemEntity>): List<ShoppingItemEntity> =
        items.sortedWith(
            compareBy<ShoppingItemEntity> { it.bought }
                .thenBy { it.name }
                .thenBy { it.createdAt }
                .thenBy { it.id },
        )
}
package org.mateuszmidor.shoplist.domain

import org.mateuszmidor.shoplist.data.ShoppingItemEntity

/**
 * Pure, stateless formatter that converts the items of a shopping list into the
 * plain-text export format accepted by [ListonicImportParser]: one `• `-prefixed
 * line per item, in the order given. Every item is included regardless of its
 * `bought` status, a blank item name still yields its bullet-prefixed line, and
 * an empty list produces an empty string.
 */
object ListonicExportFormatter {

    fun format(items: List<ShoppingItemEntity>): String =
        if (items.isEmpty()) {
            ""
        } else {
            items.joinToString(separator = "\n") { "\u2022 ${it.name}" } + "\n"
        }
}

package org.mateuszmidor.shoplist.ui.items

import java.text.Normalizer
import java.util.Locale
import org.mateuszmidor.shoplist.data.ShoppingItemEntity

private val combiningMarks = Regex("\\p{M}+")

/**
 * Folds text for lookup only: case, Polish diacritics and the `ł` stroke letter
 * are ignored, so `nić`/`nic` and `łosoś`/`losos` compare equal in both directions.
 * Stored names and displayed labels must never be folded. Combining marks in
 * other scripts are stripped too; this is accepted for this Polish-focused app.
 */
private fun foldForLookup(text: String): String =
    combiningMarks
        .replace(Normalizer.normalize(text.lowercase(Locale.ROOT), Normalizer.Form.NFD), "")
        .replace("ł", "l")

internal fun matchingBoughtItems(
    query: String,
    items: List<ShoppingItemEntity>,
): List<ShoppingItemEntity> {
    val trimmed = foldForLookup(query.trim())
    return items.filter { item ->
        item.bought && (trimmed.isEmpty() || foldForLookup(item.name).contains(trimmed))
    }
}

internal fun isExactNameMatch(
    query: String,
    items: List<ShoppingItemEntity>,
): Boolean {
    val trimmed = foldForLookup(query.trim())
    return trimmed.isNotEmpty() &&
        items.any { foldForLookup(it.name.trim()) == trimmed }
}

internal fun canAddItem(
    query: String,
    items: List<ShoppingItemEntity>,
): Boolean = query.trim().isNotEmpty() && !isExactNameMatch(query, items)

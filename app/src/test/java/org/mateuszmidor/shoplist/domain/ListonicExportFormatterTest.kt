package org.mateuszmidor.shoplist.domain

import java.util.UUID
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mateuszmidor.shoplist.data.ShoppingItemEntity

class ListonicExportFormatterTest {

    @Test
    fun format_typicalItems_joinsEachIntoBulletPrefixedLineInGivenOrder() {
        val items = listOf(item("mleko"), item("jajka"), item("chleb ciemny/bułki"))
        assertEquals(
            "\u2022 mleko\n\u2022 jajka\n\u2022 chleb ciemny/bułki\n",
            ListonicExportFormatter.format(items),
        )
    }

    @Test
    fun format_includesBoughtItems() {
        val items = listOf(item("mleko", bought = false), item("jajka", bought = true))
        assertEquals("\u2022 mleko\n\u2022 jajka\n", ListonicExportFormatter.format(items))
    }

    @Test
    fun format_preservesTheGivenOrder() {
        val items = listOf(item("mleko", bought = false), item("jajka", bought = true))
        val lines = ListonicExportFormatter.format(items).trim().split("\n")
        assertEquals(listOf("\u2022 mleko", "\u2022 jajka"), lines)
    }

    @Test
    fun format_blankItemName_stillYieldsItsBulletLine() {
        val items = listOf(item("mleko"), item(""), item("jajka"))
        assertEquals("\u2022 mleko\n\u2022 \n\u2022 jajka\n", ListonicExportFormatter.format(items))
    }

    @Test
    fun format_emptyList_returnsEmptyString() {
        assertEquals("", ListonicExportFormatter.format(emptyList()))
    }

    private fun item(name: String, bought: Boolean = false): ShoppingItemEntity =
        ShoppingItemEntity(
            id = UUID.randomUUID(),
            listId = UUID.randomUUID(),
            name = name,
            bought = bought,
            createdAt = 0,
        )
}
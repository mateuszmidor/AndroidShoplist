package org.mateuszmidor.shoplist.ui.items

import java.util.UUID
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.mateuszmidor.shoplist.data.ShoppingItemEntity

class ItemLookupTest {

    private val listId = UUID.randomUUID()

    private fun item(
        name: String,
        bought: Boolean = false,
        id: UUID = UUID.nameUUIDFromBytes(name.toByteArray()),
    ) = ShoppingItemEntity(
        id = id,
        listId = listId,
        name = name,
        bought = bought,
        createdAt = 0,
    )

    @Test
    fun matchingBoughtItems_emptyQuery_returnsOnlyBoughtItems() {
        val bought = item("Milk", bought = true)
        val unbought = item("Bread")
        val items = listOf(bought, unbought)

        val matches = matchingBoughtItems("", items)

        assertEquals(listOf(bought), matches)
    }

    @Test
    fun matchingBoughtItems_trimsQueryAndIgnoresCase() {
        val milk = item("Oat milk", bought = true)
        val bread = item("Bread", bought = true)
        val items = listOf(milk, bread)

        val matches = matchingBoughtItems("  MILK  ", items)

        assertEquals(listOf(milk), matches)
    }

    @Test
    fun matchingBoughtItems_partialName_isIncluded() {
        val eggplant = item("Eggplant", bought = true)
        val items = listOf(eggplant)

        val matches = matchingBoughtItems("egg", items)

        assertEquals(listOf(eggplant), matches)
    }

    @Test
    fun isExactNameMatch_trimsInputAndIgnoresCase() {
        val items = listOf(item("Milk"))

        assertTrue(isExactNameMatch("  milk  ", items))
    }

    @Test
    fun isExactNameMatch_partialName_isNotAMatch() {
        val items = listOf(item("Eggplant"))

        assertFalse(isExactNameMatch("egg", items))
    }

    @Test
    fun isExactNameMatch_unboughtItem_isAMatch() {
        val items = listOf(item("Milk", bought = false))

        assertTrue(isExactNameMatch("milk", items))
    }

    @Test
    fun isExactNameMatch_blankInput_isNotAMatch() {
        val items = listOf(item("Milk"))

        assertFalse(isExactNameMatch("   ", items))
    }

    @Test
    fun canAddItem_blankInput_isDisabled() {
        assertFalse(canAddItem("   ", listOf(item("Milk"))))
    }

    @Test
    fun canAddItem_exactMatch_isDisabled() {
        assertFalse(canAddItem("milk", listOf(item("Milk"))))
    }

    @Test
    fun canAddItem_partialMatch_isEnabled() {
        assertTrue(canAddItem("egg", listOf(item("Eggplant"))))
    }
}

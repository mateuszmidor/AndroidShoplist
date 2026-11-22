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

    @Test
    fun matchingBoughtItems_plainQuery_findsEveryPolishDiacritic() {
        val cases = listOf(
            "maka" to "mąka",                  // ą
            "nic" to "nić dentystyczna",       // ć
            "wedliny" to "wędliny",            // ę
            "losos" to "łosoś",                // ł + ś
            "slon" to "słoń",                  // ń
            "zolty" to "żółty ser",            // ż + ó
            "sledz" to "śledź",                // ś + ź
        )

        cases.forEach { (query, name) ->
            val match = item(name, bought = true)
            assertEquals(
                "plain query '$query' should find '$name'",
                listOf(match),
                matchingBoughtItems(query, listOf(match)),
            )
        }
    }

    @Test
    fun matchingBoughtItems_upperCaseQuery_findsUpperCaseDiacriticNames() {
        val cases = listOf(
            "MAKA" to "MĄKA",
            "NIC" to "NIĆ DENTYSTYCZNA",
            "WEDLINY" to "WĘDLINY",
            "LOSOS" to "ŁOSOŚ",
            "SLON" to "SŁOŃ",
            "ZOLTY" to "ŻÓŁTY SER",
            "SLEDZ" to "ŚLEDŹ",
        )

        cases.forEach { (query, name) ->
            val match = item(name, bought = true)
            assertEquals(
                "uppercase query '$query' should find '$name'",
                listOf(match),
                matchingBoughtItems(query, listOf(match)),
            )
        }
    }

    @Test
    fun matchingBoughtItems_diacriticQuery_findsPlainNames() {
        val cases = listOf(
            "mąka" to "maka",
            "nić dentystyczna" to "nic dentystyczna",
            "wędliny" to "wedliny",
            "łosoś" to "losos",
            "słoń" to "slon",
            "żółty ser" to "zolty ser",
            "śledź" to "sledz",
        )

        cases.forEach { (query, name) ->
            val match = item(name, bought = true)
            assertEquals(
                "diacritic query '$query' should find '$name'",
                listOf(match),
                matchingBoughtItems(query, listOf(match)),
            )
        }
    }

    @Test
    fun matchingBoughtItems_decomposedInput_matchesPrecomposedName() {
        val precomposed = item("nić dentystyczna", bought = true)
        val decomposed = item("nic\u0301 dentystyczna", bought = true)

        assertEquals(listOf(precomposed), matchingBoughtItems("nic\u0301", listOf(precomposed)))
        assertEquals(listOf(decomposed), matchingBoughtItems("nic", listOf(decomposed)))
    }

    @Test
    fun isExactNameMatch_ignoresDiacritics() {
        assertTrue(isExactNameMatch("nic", listOf(item("nić"))))
        assertTrue(isExactNameMatch("NIĆ", listOf(item("nic"))))
        assertTrue(isExactNameMatch("losos", listOf(item("ŁOŚOS"))))
    }

    @Test
    fun canAddItem_diacriticExactVariant_isDisabled() {
        assertFalse(canAddItem("nic", listOf(item("nić"))))
        assertFalse(canAddItem("łosoś", listOf(item("losos"))))
    }
}

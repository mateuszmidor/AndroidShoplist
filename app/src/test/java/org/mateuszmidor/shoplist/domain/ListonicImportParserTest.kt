package org.mateuszmidor.shoplist.domain

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ListonicImportParserTest {

    @Test
    fun parse_typicalListonicExport_preservesOrderAndSlashes() {
        val text = "\u2022 mleko\n\u2022 jajka\n\u2022 chleb ciemny/bułki"
        assertEquals(listOf("mleko", "jajka", "chleb ciemny/bułki"), ListonicImportParser.parse(text))
    }

    @Test
    fun parse_dashAndAsteriskBullets_areStrippedWithWhitespace() {
        val text = "-mleko\n * bułki \njajka"
        assertEquals(listOf("mleko", "bułki", "jajka"), ListonicImportParser.parse(text))
    }

    @Test
    fun parse_blankAndBareBulletLines_areSkipped() {
        val text = "mleko\n\n   \n\u2022\njajka"
        assertEquals(listOf("mleko", "jajka"), ListonicImportParser.parse(text))
    }

    @Test
    fun parse_duplicates_areKeptInPastedOrder() {
        assertEquals(listOf("mleko", "mleko"), ListonicImportParser.parse("mleko\nmleko"))
    }

    @Test
    fun parse_crlfLineEndings_areTolerated() {
        val text = "\u2022 mleko\r\n\u2022 jajka\r\n"
        assertEquals(listOf("mleko", "jajka"), ListonicImportParser.parse(text))
    }

    @Test
    fun parse_noParseableItem_returnsEmptyList() {
        assertTrue(ListonicImportParser.parse("").isEmpty())
        assertTrue(ListonicImportParser.parse("   ").isEmpty())
        assertTrue(ListonicImportParser.parse("\u2022").isEmpty())
    }

    @Test
    fun parse_leadingWhitespaceBeforeBullet_isStripped() {
        assertEquals(listOf("mleko"), ListonicImportParser.parse("  \u2022 mleko"))
    }
}

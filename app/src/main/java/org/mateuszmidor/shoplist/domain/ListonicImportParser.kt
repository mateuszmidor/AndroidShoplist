package org.mateuszmidor.shoplist.domain

/**
 * Pure, stateless parser that converts pasted plain text (e.g. a Listonic
 * export) into a list of item names: one per line, at most one leading bullet
 * (`•`, `-`, `*`) plus any following whitespace stripped, surrounding
 * whitespace trimmed, and blank/bare-bullet lines skipped. Duplicates are
 * kept and `/` characters within a name are preserved verbatim. A paste that
 * yields no names produces an empty result.
 */
object ListonicImportParser {

    private val bullets = setOf('\u2022', '-', '*')

    fun parse(text: String): List<String> =
        text.split('\n')
            .mapNotNull { line ->
                var name = line.trim()
                if (name.isNotEmpty() && name.first() in bullets) {
                    name = name.drop(1).trimStart()
                }
                name.ifEmpty { null }
            }
}

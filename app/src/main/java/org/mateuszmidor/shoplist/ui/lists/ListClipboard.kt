package org.mateuszmidor.shoplist.ui.lists

/**
 * Minimal clipboard seam so the export action can be JVM-unit-tested without an
 * Android framework clipboard. The production implementation copies plain text
 * and shows a Toast.
 */
fun interface ListClipboard {

    fun copy(text: String)
}
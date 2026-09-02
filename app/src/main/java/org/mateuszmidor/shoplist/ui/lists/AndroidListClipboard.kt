package org.mateuszmidor.shoplist.ui.lists

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context

/**
 * Production [ListClipboard] backed by the system clipboard service. Copies the
 * exported text as a "ShopList"-labelled plain-text clip. Android 12+ shows a
 * system-level confirmation automatically; no app-level Toast is needed.
 */
class AndroidListClipboard(
    private val context: Context,
) : ListClipboard {

    override fun copy(text: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        clipboard.setPrimaryClip(ClipData.newPlainText(LABEL, text))
    }

    private companion object {
        const val LABEL = "ShopList"
    }
}
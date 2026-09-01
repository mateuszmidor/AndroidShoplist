package org.mateuszmidor.shoplist.ui.lists

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast

/**
 * Production [ListClipboard] backed by the system clipboard service. Copies the
 * exported text as a "ShopList"-labelled plain-text clip and confirms with a
 * Toast.
 */
class AndroidListClipboard(
    private val context: Context,
) : ListClipboard {

    override fun copy(text: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        clipboard.setPrimaryClip(ClipData.newPlainText(LABEL, text))
        Toast.makeText(context, MESSAGE, Toast.LENGTH_SHORT).show()
    }

    private companion object {
        const val LABEL = "ShopList"
        const val MESSAGE = "Items copied to clipboard"
    }
}
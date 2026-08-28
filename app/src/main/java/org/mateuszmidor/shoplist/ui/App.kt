package org.mateuszmidor.shoplist.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import org.mateuszmidor.shoplist.di.AppContainer

/**
 * Root composable of the app.
 *
 * This is the stable UI entry point rendered by [org.mateuszmidor.shoplist.MainActivity].
 * Later changes (02/03) mount the lists and items screens navigation here; for
 * now it shows a placeholder so the app is verifiable on device.
 */
@Composable
fun App(container: AppContainer) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "ShopList")
    }
}

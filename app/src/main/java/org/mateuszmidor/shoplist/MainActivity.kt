package org.mateuszmidor.shoplist

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import org.mateuszmidor.shoplist.di.AppContainer
import org.mateuszmidor.shoplist.ui.App
import org.mateuszmidor.shoplist.ui.theme.ShopListTheme

class MainActivity : ComponentActivity() {

    private val container: AppContainer by lazy {
        (application as ShopListApp).container
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ShopListTheme {
                App(container = container)
            }
        }
    }
}

package org.mateuszmidor.shoplist

import android.app.Application
import org.mateuszmidor.shoplist.di.AppContainer

class ShopListApp : Application() {

    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer()
    }
}

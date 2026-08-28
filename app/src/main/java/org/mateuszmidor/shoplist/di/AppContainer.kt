package org.mateuszmidor.shoplist.di

import android.content.Context
import androidx.room3.Room
import org.mateuszmidor.shoplist.data.RoomShoppingListRepository
import org.mateuszmidor.shoplist.data.ShoppingDatabase
import org.mateuszmidor.shoplist.data.ShoppingListRepository

/**
 * Manual dependency injection container.
 *
 * Owns the long-lived dependencies of the app: the Room database and the
 * shopping list repository. A single instance is created in
 * [org.mateuszmidor.shoplist.ShopListApp] and shared across the app.
 */
class AppContainer(context: Context) {

    private val database: ShoppingDatabase =
        Room.databaseBuilder<ShoppingDatabase>(context, "shoplist.db").build()

    val shoppingListRepository: ShoppingListRepository =
        RoomShoppingListRepository(database.shoppingListDao())
}
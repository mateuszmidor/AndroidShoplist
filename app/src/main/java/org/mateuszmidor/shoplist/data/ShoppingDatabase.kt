package org.mateuszmidor.shoplist.data

import androidx.room3.ColumnTypeConverters
import androidx.room3.Database
import androidx.room3.RoomDatabase

@Database(
    entities = [ShoppingListEntity::class, ShoppingItemEntity::class],
    version = 1,
    exportSchema = true,
)
@ColumnTypeConverters(Converters::class)
abstract class ShoppingDatabase : RoomDatabase() {

    abstract fun shoppingListDao(): ShoppingListDao

    abstract fun shoppingItemDao(): ShoppingItemDao
}
package org.mateuszmidor.shoplist.data

import androidx.room3.ColumnInfo
import androidx.room3.Entity
import androidx.room3.PrimaryKey
import java.util.UUID

@Entity(tableName = "shopping_lists")
data class ShoppingListEntity(
    @PrimaryKey val id: UUID,
    val name: String,
    @ColumnInfo(name = "created_at") val createdAt: Long,
)
package org.mateuszmidor.shoplist.data

import androidx.room3.ColumnInfo
import androidx.room3.Entity
import androidx.room3.ForeignKey
import androidx.room3.Index
import androidx.room3.PrimaryKey
import java.util.UUID

@Entity(
    tableName = "shopping_items",
    foreignKeys = [
        ForeignKey(
            entity = ShoppingListEntity::class,
            parentColumns = ["id"],
            childColumns = ["list_id"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("list_id")],
)
data class ShoppingItemEntity(
    @PrimaryKey val id: UUID,
    @ColumnInfo(name = "list_id") val listId: UUID,
    val name: String,
    val bought: Boolean = false,
    @ColumnInfo(name = "created_at") val createdAt: Long,
)

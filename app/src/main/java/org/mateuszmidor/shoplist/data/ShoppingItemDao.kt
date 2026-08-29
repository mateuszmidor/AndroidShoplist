package org.mateuszmidor.shoplist.data

import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.Query
import java.util.UUID
import kotlinx.coroutines.flow.Flow

@Dao
interface ShoppingItemDao {

    @Query("SELECT * FROM shopping_items WHERE list_id = :listId ORDER BY bought ASC, created_at ASC, id ASC")
    fun observeByList(listId: UUID): Flow<List<ShoppingItemEntity>>

    @Insert
    suspend fun insert(item: ShoppingItemEntity)

    @Query("UPDATE shopping_items SET bought = NOT bought WHERE id = :id")
    suspend fun toggleBought(id: UUID)

    @Query("UPDATE shopping_items SET name = :name WHERE id = :id")
    suspend fun renameById(id: UUID, name: String)

    @Query("DELETE FROM shopping_items WHERE id = :id")
    suspend fun deleteById(id: UUID)
}

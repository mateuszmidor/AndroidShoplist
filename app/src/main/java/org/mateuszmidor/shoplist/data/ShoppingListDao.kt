package org.mateuszmidor.shoplist.data

import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.Query
import java.util.UUID
import kotlinx.coroutines.flow.Flow

@Dao
interface ShoppingListDao {

    @Query("SELECT * FROM shopping_lists ORDER BY created_at ASC, id ASC")
    fun observeAll(): Flow<List<ShoppingListEntity>>

    @Insert
    suspend fun insert(list: ShoppingListEntity)

    @Query("UPDATE shopping_lists SET name = :name WHERE id = :id")
    suspend fun renameById(id: UUID, name: String)

    @Query("DELETE FROM shopping_lists WHERE id = :id")
    suspend fun deleteById(id: UUID)
}
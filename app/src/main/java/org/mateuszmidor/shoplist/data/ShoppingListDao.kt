package org.mateuszmidor.shoplist.data

import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.Query
import java.util.UUID
import kotlinx.coroutines.flow.Flow

@Dao
interface ShoppingListDao {

    @Query(
        """
        SELECT l.id AS id,
               l.name AS name,
               l.created_at AS createdAt,
               (SELECT COUNT(*) FROM shopping_items i WHERE i.list_id = l.id) AS totalCount,
               (SELECT COUNT(*) FROM shopping_items i WHERE i.list_id = l.id AND i.bought = 1) AS boughtCount
        FROM shopping_lists l
        ORDER BY l.created_at ASC, l.id ASC
        """,
    )
    fun observeListSummaries(): Flow<List<ListSummary>>

    @Query("SELECT * FROM shopping_lists WHERE id = :id")
    fun observeById(id: UUID): Flow<ShoppingListEntity?>

    @Insert
    suspend fun insert(list: ShoppingListEntity)

    @Query("UPDATE shopping_lists SET name = :name WHERE id = :id")
    suspend fun renameById(id: UUID, name: String)

    @Query("DELETE FROM shopping_lists WHERE id = :id")
    suspend fun deleteById(id: UUID)
}
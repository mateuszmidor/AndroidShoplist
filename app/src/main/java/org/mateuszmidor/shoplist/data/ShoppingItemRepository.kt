package org.mateuszmidor.shoplist.data

import java.util.UUID
import kotlinx.coroutines.flow.Flow

interface ShoppingItemRepository {

    fun observeItems(listId: UUID): Flow<List<ShoppingItemEntity>>

    suspend fun create(listId: UUID, name: String): UUID

    suspend fun rename(id: UUID, name: String)

    suspend fun delete(id: UUID)
}

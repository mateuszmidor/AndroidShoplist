package org.mateuszmidor.shoplist.data

import java.util.UUID
import kotlinx.coroutines.flow.Flow

interface ShoppingListRepository {

    fun observeLists(): Flow<List<ShoppingListEntity>>

    suspend fun create(name: String): UUID

    suspend fun rename(id: UUID, name: String)

    suspend fun delete(id: UUID)
}
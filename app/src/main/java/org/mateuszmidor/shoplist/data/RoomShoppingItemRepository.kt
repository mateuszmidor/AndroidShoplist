package org.mateuszmidor.shoplist.data

import java.util.UUID
import kotlinx.coroutines.flow.Flow

class RoomShoppingItemRepository(
    private val dao: ShoppingItemDao,
) : ShoppingItemRepository {

    override fun observeItems(listId: UUID): Flow<List<ShoppingItemEntity>> =
        dao.observeByList(listId)

    override suspend fun create(listId: UUID, name: String): UUID {
        val id = UUID.randomUUID()
        dao.insert(ShoppingItemEntity(id = id, listId = listId, name = name, createdAt = System.currentTimeMillis()))
        return id
    }

    override suspend fun rename(id: UUID, name: String) = dao.renameById(id, name)

    override suspend fun delete(id: UUID) = dao.deleteById(id)
}

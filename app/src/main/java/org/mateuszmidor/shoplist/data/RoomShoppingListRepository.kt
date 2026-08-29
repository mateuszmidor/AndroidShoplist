package org.mateuszmidor.shoplist.data

import java.util.UUID
import kotlinx.coroutines.flow.Flow

class RoomShoppingListRepository(
    private val dao: ShoppingListDao,
) : ShoppingListRepository {

    override fun observeLists(): Flow<List<ListSummary>> = dao.observeListSummaries()

    override fun observeList(id: UUID): Flow<ShoppingListEntity?> = dao.observeById(id)

    override suspend fun create(name: String): UUID {
        val id = UUID.randomUUID()
        dao.insert(ShoppingListEntity(id = id, name = name, createdAt = System.currentTimeMillis()))
        return id
    }

    override suspend fun rename(id: UUID, name: String) = dao.renameById(id, name)

    override suspend fun delete(id: UUID) = dao.deleteById(id)
}
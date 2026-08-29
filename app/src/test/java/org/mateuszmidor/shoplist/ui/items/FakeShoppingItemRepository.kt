package org.mateuszmidor.shoplist.ui.items

import java.util.UUID
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import org.mateuszmidor.shoplist.data.ShoppingItemEntity
import org.mateuszmidor.shoplist.data.ShoppingItemRepository

/**
 * In-memory [ShoppingItemRepository] for unit tests. Emission order mirrors
 * append order within a list (the Room DAO orders by creation time).
 */
class FakeShoppingItemRepository : ShoppingItemRepository {

    private val items = MutableStateFlow<List<ShoppingItemEntity>>(emptyList())

    private var nextCreatedAt = 0L

    override fun observeItems(listId: UUID): Flow<List<ShoppingItemEntity>> =
        items.map { list -> list.filter { it.listId == listId } }

    override suspend fun create(listId: UUID, name: String): UUID {
        val id = UUID.randomUUID()
        items.value = items.value +
            ShoppingItemEntity(id = id, listId = listId, name = name, createdAt = nextCreatedAt++)
        return id
    }

    override suspend fun rename(id: UUID, name: String) {
        items.value = items.value.map { if (it.id == id) it.copy(name = name) else it }
    }

    override suspend fun delete(id: UUID) {
        items.value = items.value.filterNot { it.id == id }
    }
}

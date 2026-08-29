package org.mateuszmidor.shoplist.ui.items

import java.util.UUID
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import org.mateuszmidor.shoplist.data.ShoppingItemEntity
import org.mateuszmidor.shoplist.data.ShoppingItemRepository

/**
 * In-memory [ShoppingItemRepository] for unit tests. After every mutation the
 * stored items are re-sorted in the same order the Room DAO enforces:
 * unchecked items first, then checked, each section by creation time with the
 * UUID as a stable tiebreak.
 */
class FakeShoppingItemRepository : ShoppingItemRepository {

    private val items = MutableStateFlow<List<ShoppingItemEntity>>(emptyList())

    private var nextCreatedAt = 0L

    override fun observeItems(listId: UUID): Flow<List<ShoppingItemEntity>> =
        items.map { list -> list.filter { it.listId == listId } }

    override suspend fun create(listId: UUID, name: String): UUID {
        val id = UUID.randomUUID()
        items.value = order(
            items.value +
                ShoppingItemEntity(id = id, listId = listId, name = name, createdAt = nextCreatedAt++),
        )
        return id
    }

    override suspend fun createAll(listId: UUID, names: List<String>): List<UUID> {
        val added = names.map { name ->
            ShoppingItemEntity(
                id = UUID.randomUUID(),
                listId = listId,
                name = name,
                createdAt = nextCreatedAt++,
            )
        }
        items.value = order(items.value + added)
        return added.map { it.id }
    }

    override suspend fun rename(id: UUID, name: String) {
        items.value = order(items.value.map { if (it.id == id) it.copy(name = name) else it })
    }

    override suspend fun delete(id: UUID) {
        items.value = order(items.value.filterNot { it.id == id })
    }

    override suspend fun toggleBought(id: UUID) {
        items.value = order(items.value.map { if (it.id == id) it.copy(bought = !it.bought) else it })
    }

    private fun order(list: List<ShoppingItemEntity>): List<ShoppingItemEntity> =
        list.sortedWith(
            compareBy<ShoppingItemEntity> { it.bought }
                .thenBy { it.createdAt }
                .thenBy { it.id },
        )
}

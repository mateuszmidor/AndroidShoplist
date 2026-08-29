package org.mateuszmidor.shoplist.ui.lists

import java.util.UUID
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.mateuszmidor.shoplist.data.ShoppingListEntity
import org.mateuszmidor.shoplist.data.ShoppingListRepository

/**
 * In-memory [ShoppingListRepository] for unit tests. Emission order mirrors
 * append order (the Room DAO orders by creation time).
 */
class FakeShoppingListRepository : ShoppingListRepository {

    private val lists = MutableStateFlow<List<ShoppingListEntity>>(emptyList())

    private var nextCreatedAt = 0L

    override fun observeLists(): Flow<List<ShoppingListEntity>> = lists.asStateFlow()

    override suspend fun create(name: String): UUID {
        val id = UUID.randomUUID()
        lists.value = lists.value + ShoppingListEntity(id = id, name = name, createdAt = nextCreatedAt++)
        return id
    }

    override suspend fun rename(id: UUID, name: String) {
        lists.value = lists.value.map { if (it.id == id) it.copy(name = name) else it }
    }

    override suspend fun delete(id: UUID) {
        lists.value = lists.value.filterNot { it.id == id }
    }

    suspend fun seed(vararg names: String) {
        names.forEach { create(it) }
    }
}
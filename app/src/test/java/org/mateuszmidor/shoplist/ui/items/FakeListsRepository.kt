package org.mateuszmidor.shoplist.ui.items

import java.util.UUID
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import org.mateuszmidor.shoplist.data.ListSummary
import org.mateuszmidor.shoplist.data.ShoppingListEntity
import org.mateuszmidor.shoplist.data.ShoppingListRepository

/**
 * Minimal in-memory [ShoppingListRepository] for the items test package. Only
 * the list-observation operations used by [ItemsViewModel] are surfaced with
 * deterministic values; the rest mirror the single-write-path contract.
 */
class FakeListsRepository : ShoppingListRepository {

    private val lists = MutableStateFlow<List<ShoppingListEntity>>(emptyList())

    private var nextCreatedAt = 0L

    override fun observeLists(): Flow<List<ListSummary>> =
        lists.map { entities ->
            entities.map { list ->
                ListSummary(
                    id = list.id,
                    name = list.name,
                    createdAt = list.createdAt,
                    totalCount = 0,
                    boughtCount = 0,
                )
            }
        }

    override fun observeList(id: UUID): Flow<ShoppingListEntity?> =
        lists.map { entities -> entities.firstOrNull { it.id == id } }

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

    fun seed(vararg entries: Pair<UUID, String>) {
        entries.forEach { (id, name) ->
            lists.value = lists.value + ShoppingListEntity(id = id, name = name, createdAt = nextCreatedAt++)
        }
    }
}

package org.mateuszmidor.shoplist.ui.combined

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import java.util.UUID
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.mateuszmidor.shoplist.data.ShoppingItemEntity
import org.mateuszmidor.shoplist.data.ShoppingItemRepository
import org.mateuszmidor.shoplist.data.ShoppingListEntity
import org.mateuszmidor.shoplist.data.ShoppingListRepository
import org.mateuszmidor.shoplist.domain.CombinedItemSorter

class CombinedViewModel(
    private val itemRepository: ShoppingItemRepository,
    private val listRepository: ShoppingListRepository,
    private val listIds: List<UUID>,
) : ViewModel() {

    private val itemFlows: List<Flow<List<ShoppingItemEntity>>> =
        listIds.map { id -> itemRepository.observeItems(id) }

    private val nameFlows: List<Flow<ShoppingListEntity?>> =
        listIds.map { id -> listRepository.observeList(id) }

    val uiState: StateFlow<CombinedUiState> =
        combine(
            combine(itemFlows) { lists -> lists.flatMap { it } },
            combine(nameFlows) { lists -> lists.toList() },
        ) { items, lists ->
            val namesById = lists.mapNotNull { it }.associate { it.id to it.name }
            CombinedUiState(
                items = CombinedItemSorter.sort(items).map { entity ->
                    CombinedItem(
                        item = entity,
                        sourceListName = namesById[entity.listId] ?: "",
                    )
                },
                sourceListNames = lists.mapNotNull { it?.name },
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = CombinedUiState(),
        )

    fun toggleItemBought(id: UUID) {
        viewModelScope.launch { itemRepository.toggleBought(id) }
    }
}
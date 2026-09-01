package org.mateuszmidor.shoplist.ui.lists

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import java.util.UUID
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.mateuszmidor.shoplist.data.ShoppingItemRepository
import org.mateuszmidor.shoplist.data.ShoppingListRepository
import org.mateuszmidor.shoplist.domain.ListonicExportFormatter

class ListsViewModel(
    private val listRepository: ShoppingListRepository,
    private val itemRepository: ShoppingItemRepository,
    private val clipboard: ListClipboard,
) : ViewModel() {

    val uiState: StateFlow<ListsUiState> =
        listRepository.observeLists()
            .map { ListsUiState(lists = it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = ListsUiState(),
            )

    fun createList(name: String) {
        val trimmed = name.trim()
        if (trimmed.isEmpty()) return
        viewModelScope.launch { listRepository.create(trimmed) }
    }

    fun renameList(id: UUID, name: String) {
        val trimmed = name.trim()
        if (trimmed.isEmpty()) return
        viewModelScope.launch { listRepository.rename(id, trimmed) }
    }

    fun deleteList(id: UUID) {
        viewModelScope.launch { listRepository.delete(id) }
    }

    fun exportListItems(listId: UUID) {
        viewModelScope.launch {
            val items = itemRepository.getAllByList(listId)
            clipboard.copy(ListonicExportFormatter.format(items))
        }
    }
}
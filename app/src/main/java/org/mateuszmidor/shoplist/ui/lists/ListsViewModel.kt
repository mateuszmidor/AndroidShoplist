package org.mateuszmidor.shoplist.ui.lists

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import java.util.UUID
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
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

    private val selectionMode = MutableStateFlow(false)
    private val selectedIds = MutableStateFlow<Set<UUID>>(emptySet())

    val uiState: StateFlow<ListsUiState> =
        combine(
            listRepository.observeLists(),
            selectionMode,
            selectedIds,
        ) { lists, mode, selected ->
            ListsUiState(lists = lists, selectionMode = mode, selectedIds = selected)
        }.stateIn(
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

    fun enterSelectionMode(id: UUID) {
        selectionMode.value = true
        selectedIds.value = selectedIds.value + id
    }

    fun toggleSelected(id: UUID) {
        val current = selectedIds.value
        selectedIds.value = if (id in current) current - id else current + id
    }

    fun clearSelection() {
        selectionMode.value = false
        selectedIds.value = emptySet()
    }
}
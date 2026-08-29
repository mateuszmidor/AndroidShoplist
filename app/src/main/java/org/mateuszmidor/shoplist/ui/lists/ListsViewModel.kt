package org.mateuszmidor.shoplist.ui.lists

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import java.util.UUID
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.mateuszmidor.shoplist.data.ShoppingListRepository

class ListsViewModel(
    private val repository: ShoppingListRepository,
) : ViewModel() {

    val uiState: StateFlow<ListsUiState> =
        repository.observeLists()
            .map { ListsUiState(lists = it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = ListsUiState(),
            )

    fun createList(name: String) {
        val trimmed = name.trim()
        if (trimmed.isEmpty()) return
        viewModelScope.launch { repository.create(trimmed) }
    }

    fun renameList(id: UUID, name: String) {
        val trimmed = name.trim()
        if (trimmed.isEmpty()) return
        viewModelScope.launch { repository.rename(id, trimmed) }
    }

    fun deleteList(id: UUID) {
        viewModelScope.launch { repository.delete(id) }
    }
}
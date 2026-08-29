package org.mateuszmidor.shoplist.ui.items

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import java.util.UUID
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.mateuszmidor.shoplist.data.ShoppingItemRepository

class ItemsViewModel(
    private val repository: ShoppingItemRepository,
    private val listId: UUID,
) : ViewModel() {

    val uiState: StateFlow<ItemsUiState> =
        repository.observeItems(listId)
            .map { ItemsUiState(items = it) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = ItemsUiState(),
            )

    fun addItem(name: String) {
        val trimmed = name.trim()
        if (trimmed.isEmpty()) return
        viewModelScope.launch { repository.create(listId, trimmed) }
    }

    fun renameItem(id: UUID, name: String) {
        val trimmed = name.trim()
        if (trimmed.isEmpty()) return
        viewModelScope.launch { repository.rename(id, trimmed) }
    }

    fun deleteItem(id: UUID) {
        viewModelScope.launch { repository.delete(id) }
    }
}

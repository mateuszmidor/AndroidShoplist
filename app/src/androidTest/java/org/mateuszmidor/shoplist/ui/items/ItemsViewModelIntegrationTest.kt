package org.mateuszmidor.shoplist.ui.items

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.room3.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import java.util.UUID
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mateuszmidor.shoplist.data.RoomShoppingItemRepository
import org.mateuszmidor.shoplist.data.RoomShoppingListRepository
import org.mateuszmidor.shoplist.data.ShoppingDatabase

/**
 * End-to-end wiring of [ItemsViewModel] over the real Room-backed
 * [RoomShoppingItemRepository] on an in-memory [ShoppingDatabase] (ADR-0008:
 * ViewModel methods are the single write path; emitted state is the
 * repository flow).
 */
@RunWith(AndroidJUnit4::class)
class ItemsViewModelIntegrationTest {

    private lateinit var database: ShoppingDatabase
    private lateinit var viewModelStore: ViewModelStore
    private lateinit var viewModel: ItemsViewModel
    private lateinit var listId: UUID

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder<ShoppingDatabase>(context).build()
        val listRepository = RoomShoppingListRepository(database.shoppingListDao())
        val repository = RoomShoppingItemRepository(database.shoppingItemDao())
        listId = runBlocking { listRepository.create("Groceries") }
        viewModelStore = ViewModelStore()
        viewModel = ViewModelProvider(
            viewModelStore,
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T =
                    ItemsViewModel(repository, listId) as T
            },
        )[ItemsViewModel::class.java]
    }

    @After
    fun tearDown() {
        viewModelStore.clear()
        database.close()
    }

    @Test
    fun addItem_surfacesTheTrimmedItemInUiState() = runBlocking {
        viewModel.addItem("  Milk  ")

        val state = uiStateUntil { it.items.isNotEmpty() }

        val item = state.items.single()
        assertEquals("Milk", item.name)
        assertEquals(listId, item.listId)
    }

    @Test
    fun renameAndDelete_propagateToUiStateAndDatabase() = runBlocking {
        viewModel.addItem("Milk")
        val id = uiStateUntil { it.items.isNotEmpty() }.items.single().id
        viewModel.addItem("Bread")
        val ids = uiStateUntil { it.items.size == 2 }.items.map { it.id }.toSet()

        viewModel.renameItem(id, "Soy milk")
        uiStateUntil { state -> state.items.first { it.id == id }.name == "Soy milk" }
        database.shoppingItemDao().observeByList(listId)
            .first { items -> items.first { it.id == id }.name == "Soy milk" }

        viewModel.deleteItem(id)
        uiStateUntil { state -> state.items.map { it.id }.toSet() == ids - id }
        val dbItems = database.shoppingItemDao().observeByList(listId).first { items -> items.size == 1 }
        val remaining = dbItems.single()

        assertEquals((ids - id).single(), remaining.id)
        assertEquals("Bread", remaining.name)
        assertEquals(listOf("Bread"), viewModel.uiState.value.items.map { it.name })
    }

    @Test
    fun toggleBoughtRoundTrip_reordersUiStateAndDomain_reflectsBothTransitionsInDatabase() = runBlocking {
        viewModel.addItem("Milk")
        viewModel.addItem("Bread")
        viewModel.addItem("Eggs")
        val items = uiStateUntil { it.items.size == 3 }.items
        val milk = items.first { it.name == "Milk" }
        val bread = items.first { it.name == "Bread" }
        val eggs = items.first { it.name == "Eggs" }
        assertEquals(listOf("Milk", "Bread", "Eggs"), viewModel.uiState.value.items.map { it.name })

        viewModel.toggleItemBought(bread.id)
        uiStateUntil { state -> state.items.map { it.name } == listOf("Milk", "Eggs", "Bread") }
        val dbChecked = database.shoppingItemDao().observeByList(listId)
            .first { items -> items.first { it.id == bread.id }.bought }
        assertTrue(dbChecked.first { it.id == bread.id }.bought)
        assertEquals(listOf(milk.id, eggs.id, bread.id), dbChecked.map { it.id })

        viewModel.toggleItemBought(bread.id)
        uiStateUntil { state -> state.items.map { it.name } == listOf("Milk", "Bread", "Eggs") }
        val dbUnchecked = database.shoppingItemDao().observeByList(listId)
            .first { items -> !items.first { it.id == bread.id }.bought }
        assertFalse(dbUnchecked.first { it.id == bread.id }.bought)
        assertEquals(listOf(milk.id, bread.id, eggs.id), dbUnchecked.map { it.id })
    }

    private suspend fun uiStateUntil(predicate: (ItemsUiState) -> Boolean): ItemsUiState =
        withTimeout(5_000) { viewModel.uiState.first(predicate) }
}

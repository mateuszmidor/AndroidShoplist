package org.mateuszmidor.shoplist.ui.combined

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
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mateuszmidor.shoplist.data.RoomShoppingItemRepository
import org.mateuszmidor.shoplist.data.RoomShoppingListRepository
import org.mateuszmidor.shoplist.data.ShoppingDatabase
import org.mateuszmidor.shoplist.data.ShoppingItemEntity

/**
 * End-to-end wiring of [CombinedViewModel] over real Room-backed
 * repositories on an in-memory [ShoppingDatabase]: the merged view reflects
 * all selected lists' items, and toggling writes through to the owning list
 * (ADR-0008 / ADR-0009 write-through).
 */
@RunWith(AndroidJUnit4::class)
class CombinedViewModelIntegrationTest {

    private lateinit var database: ShoppingDatabase
    private lateinit var viewModelStore: ViewModelStore
    private lateinit var listRepository: RoomShoppingListRepository
    private lateinit var itemRepository: RoomShoppingItemRepository

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder<ShoppingDatabase>(context).build()
        listRepository = RoomShoppingListRepository(database.shoppingListDao())
        itemRepository = RoomShoppingItemRepository(database.shoppingItemDao())
        viewModelStore = ViewModelStore()
    }

    @After
    fun tearDown() {
        viewModelStore.clear()
        database.close()
    }

    @Test
    fun mergedView_showsAllItemsFromSelectedListsWithSourceCaptions() = runBlocking {
        val pizzaId = listRepository.create("Pizza products")
        val saladId = listRepository.create("Cesar salad products")
        itemRepository.create(pizzaId, "mozzarella")
        itemRepository.create(saladId, "cheese")
        itemRepository.create(saladId, "lettuce")
        val viewModel = viewModel(pizzaId, saladId)

        val state = uiStateUntil(viewModel) { it.items.size == 3 }

        val rows = state.items.map { it.item.name to it.sourceListName }
        assertEquals(
            listOf(
                "cheese" to "Cesar salad products",
                "lettuce" to "Cesar salad products",
                "mozzarella" to "Pizza products",
            ),
            rows,
        )
        assertEquals(listOf("Pizza products", "Cesar salad products"), state.sourceListNames)
    }

    @Test
    fun mergedView_reordersByNameSectionsWhileReflectingBoughtState() = runBlocking {
        val listId = listRepository.create("Groceries")
        val itemDao = database.shoppingItemDao()
        val eggs = ShoppingItemEntity(id = UUID.randomUUID(), listId = listId, name = "eggs", createdAt = 2)
        val chips = ShoppingItemEntity(id = UUID.randomUUID(), listId = listId, name = "chips", createdAt = 1)
        val milk = ShoppingItemEntity(id = UUID.randomUUID(), listId = listId, name = "milk", bought = true, createdAt = 3)
        itemDao.insert(eggs)
        itemDao.insert(chips)
        itemDao.insert(milk)
        val viewModel = viewModel(listId)

        val state = uiStateUntil(viewModel) { it.items.size == 3 }

        assertEquals(listOf("chips", "eggs", "milk"), state.items.map { it.item.name })
    }

    @Test
    fun toggle_writesThroughToTheOwningListAndBack() = runBlocking {
        val pizzaId = listRepository.create("Pizza products")
        val saladId = listRepository.create("Cesar salad products")
        val mozzarella = itemRepository.create(pizzaId, "mozzarella")
        val viewModel = viewModel(pizzaId, saladId)

        viewModel.toggleItemBought(mozzarella)
        uiStateUntil(viewModel) { state -> state.items.firstOrNull { it.item.id == mozzarella }?.item?.bought == true }
        val dbChecked = database.shoppingItemDao().observeByList(pizzaId)
            .first { items -> items.first { it.id == mozzarella }.bought }
        assertTrue(dbChecked.first { it.id == mozzarella }.bought)

        viewModel.toggleItemBought(mozzarella)
        uiStateUntil(viewModel) { state -> state.items.firstOrNull { it.item.id == mozzarella }?.item?.bought == false }
        val dbUnchecked = database.shoppingItemDao().observeByList(pizzaId)
            .first { items -> !items.first { it.id == mozzarella }.bought }
        assertTrue(!dbUnchecked.first { it.id == mozzarella }.bought)
    }

    private fun viewModel(vararg ids: UUID): CombinedViewModel =
        ViewModelProvider(
            viewModelStore,
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T =
                    CombinedViewModel(itemRepository, listRepository, ids.toList()) as T
            },
        )[CombinedViewModel::class.java]

    private suspend fun uiStateUntil(
        viewModel: CombinedViewModel,
        predicate: (CombinedUiState) -> Boolean,
    ): CombinedUiState = withTimeout(5_000) { viewModel.uiState.first(predicate) }
}
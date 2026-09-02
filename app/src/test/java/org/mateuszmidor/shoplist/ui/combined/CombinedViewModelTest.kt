package org.mateuszmidor.shoplist.ui.combined

import java.util.UUID
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mateuszmidor.shoplist.ui.items.FakeListsRepository
import org.mateuszmidor.shoplist.ui.items.FakeShoppingItemRepository

@OptIn(ExperimentalCoroutinesApi::class)
class CombinedViewModelTest {

    private val dispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun observe_mergesItemsFromSeveralListsWithSourceCaptions() = runTest(dispatcher) {
        val pizzaId = UUID.randomUUID()
        val saladId = UUID.randomUUID()
        val listRepository = FakeListsRepository().apply {
            seed(pizzaId to "Pizza products", saladId to "Cesar salad products")
        }
        val itemRepository = FakeShoppingItemRepository()
        itemRepository.create(pizzaId, "mozzarella")
        itemRepository.create(saladId, "lettuce")
        val viewModel = CombinedViewModel(itemRepository, listRepository, listOf(pizzaId, saladId))
        collectState(viewModel)

        advanceUntilIdle()

        val rows = viewModel.uiState.value.items.map { it.item.name to it.sourceListName }
        assertEquals(
            listOf(
                "lettuce" to "Cesar salad products",
                "mozzarella" to "Pizza products",
            ),
            rows,
        )
    }

    @Test
    fun observe_concatenatesDuplicateNamesOncePerOwningList() = runTest(dispatcher) {
        val pizzaId = UUID.randomUUID()
        val saladId = UUID.randomUUID()
        val listRepository = FakeListsRepository().apply {
            seed(pizzaId to "Pizza products", saladId to "Cesar salad products")
        }
        val itemRepository = FakeShoppingItemRepository()
        val pizzaCheese = itemRepository.create(pizzaId, "cheese")
        val saladCheese = itemRepository.create(saladId, "cheese")
        val viewModel = CombinedViewModel(itemRepository, listRepository, listOf(pizzaId, saladId))
        collectState(viewModel)

        advanceUntilIdle()

        val rows = viewModel.uiState.value.items.map { it.item.id to it.sourceListName }
        assertEquals(
            listOf(
                pizzaCheese to "Pizza products",
                saladCheese to "Cesar salad products",
            ),
            rows,
        )
    }

    @Test
    fun observe_surfacesSourceListNamesOfCombinedLists() = runTest(dispatcher) {
        val pizzaId = UUID.randomUUID()
        val saladId = UUID.randomUUID()
        val listRepository = FakeListsRepository().apply {
            seed(pizzaId to "Pizza products", saladId to "Cesar salad products")
        }
        val viewModel =
            CombinedViewModel(FakeShoppingItemRepository(), listRepository, listOf(pizzaId, saladId))
        collectState(viewModel)

        advanceUntilIdle()

        assertEquals(listOf("Pizza products", "Cesar salad products"), viewModel.uiState.value.sourceListNames)
    }

    @Test
    fun observe_ordersUncheckedFirstThenChecked_eachByName() = runTest(dispatcher) {
        val listId = UUID.randomUUID()
        val listRepository = FakeListsRepository().apply { seed(listId to "Groceries") }
        val itemRepository = FakeShoppingItemRepository()
        val bought = itemRepository.create(listId, "milk")
        itemRepository.create(listId, "chips")
        itemRepository.create(listId, "eggs")
        itemRepository.toggleBought(bought)
        val viewModel = CombinedViewModel(itemRepository, listRepository, listOf(listId))
        collectState(viewModel)

        advanceUntilIdle()

        assertEquals(listOf("chips", "eggs", "milk"), viewModel.uiState.value.items.map { it.item.name })
    }

    @Test
    fun observe_equalNamedItemsOrderedByCreationTime() = runTest(dispatcher) {
        val listId = UUID.randomUUID()
        val listRepository = FakeListsRepository().apply { seed(listId to "Groceries") }
        val itemRepository = FakeShoppingItemRepository()
        val older = itemRepository.create(listId, "cheese")
        val newer = itemRepository.create(listId, "cheese")
        val viewModel = CombinedViewModel(itemRepository, listRepository, listOf(listId))
        collectState(viewModel)

        advanceUntilIdle()

        assertEquals(older, viewModel.uiState.value.items[0].item.id)
        assertEquals(newer, viewModel.uiState.value.items[1].item.id)
    }

    @Test
    fun toggleItemBought_writesThroughToOwningList() = runTest(dispatcher) {
        val listId = UUID.randomUUID()
        val listRepository = FakeListsRepository().apply { seed(listId to "Groceries") }
        val itemRepository = FakeShoppingItemRepository()
        val id = itemRepository.create(listId, "mozzarella")
        val viewModel = CombinedViewModel(itemRepository, listRepository, listOf(listId))
        collectState(viewModel)

        viewModel.toggleItemBought(id)
        advanceUntilIdle()
        assertTrue(viewModel.uiState.value.items.single().item.bought)

        viewModel.toggleItemBought(id)
        advanceUntilIdle()
        assertFalse(viewModel.uiState.value.items.single().item.bought)
    }

    private fun TestScope.collectState(viewModel: CombinedViewModel) {
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect { }
        }
    }
}
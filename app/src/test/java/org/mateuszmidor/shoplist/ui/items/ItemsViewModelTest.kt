package org.mateuszmidor.shoplist.ui.items

import java.util.UUID
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
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

@OptIn(ExperimentalCoroutinesApi::class)
class ItemsViewModelTest {

    private val dispatcher = StandardTestDispatcher()
    private val listId: UUID = UUID.randomUUID()

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun observe_listsItemsInRepositoryEmissionOrder() = runTest(dispatcher) {
        val repository = FakeShoppingItemRepository()
        repository.create(listId, "One")
        repository.create(listId, "Two")
        val viewModel = ItemsViewModel(repository, listId)
        collectState(viewModel)

        advanceUntilIdle()

        assertEquals(listOf("One", "Two"), viewModel.uiState.value.items.map { it.name })
    }

    @Test
    fun addItem_withTrimmedName_appendsNewItemAsLast() = runTest(dispatcher) {
        val repository = FakeShoppingItemRepository()
        repository.create(listId, "One")
        val viewModel = ItemsViewModel(repository, listId)
        collectState(viewModel)

        viewModel.addItem("  Milk  ")
        advanceUntilIdle()

        assertEquals(listOf("One", "Milk"), viewModel.uiState.value.items.map { it.name })
    }

    @Test
    fun observe_excludesItemsOfOtherLists() = runTest(dispatcher) {
        val repository = FakeShoppingItemRepository()
        val otherListId = UUID.randomUUID()
        repository.create(listId, "Mine")
        repository.create(otherListId, "Theirs")
        val viewModel = ItemsViewModel(repository, listId)
        collectState(viewModel)

        advanceUntilIdle()

        assertEquals(listOf("Mine"), viewModel.uiState.value.items.map { it.name })
    }

    @Test
    fun addItem_withBlankName_emitsNoChange() = runTest(dispatcher) {
        val repository = FakeShoppingItemRepository()
        repository.create(listId, "One")
        val viewModel = ItemsViewModel(repository, listId)
        collectState(viewModel)

        viewModel.addItem("   ")
        advanceUntilIdle()

        assertEquals(listOf("One"), viewModel.uiState.value.items.map { it.name })
    }

    @Test
    fun renameItem_preservesIdAndCreationTime() = runTest(dispatcher) {
        val repository = FakeShoppingItemRepository()
        val id = repository.create(listId, "Milk")
        val created = repository.observeItems(listId).first().single()
        val viewModel = ItemsViewModel(repository, listId)
        collectState(viewModel)

        viewModel.renameItem(id, "Soy milk")
        advanceUntilIdle()

        val renamed = viewModel.uiState.value.items.single()
        assertEquals(id, renamed.id)
        assertEquals(created.createdAt, renamed.createdAt)
        assertEquals("Soy milk", renamed.name)
    }

    @Test
    fun deleteItem_removesItemFromState() = runTest(dispatcher) {
        val repository = FakeShoppingItemRepository()
        val toDelete = repository.create(listId, "Milk")
        val keep = repository.create(listId, "Bread")
        val viewModel = ItemsViewModel(repository, listId)
        collectState(viewModel)

        viewModel.deleteItem(toDelete)
        advanceUntilIdle()

        assertEquals(listOf(keep), viewModel.uiState.value.items.map { it.id })
    }

    @Test
    fun toggleItemBought_flipsBoughtFlagInState() = runTest(dispatcher) {
        val repository = FakeShoppingItemRepository()
        val id = repository.create(listId, "Milk")
        val viewModel = ItemsViewModel(repository, listId)
        collectState(viewModel)

        viewModel.toggleItemBought(id)
        advanceUntilIdle()
        assertTrue(viewModel.uiState.value.items.single().bought)

        viewModel.toggleItemBought(id)
        advanceUntilIdle()
        assertFalse(viewModel.uiState.value.items.single().bought)
    }

    @Test
    fun toggleItemBought_groupsUncheckedFirstThenChecked_eachByCreationTime() = runTest(dispatcher) {
        val repository = FakeShoppingItemRepository()
        val a = repository.create(listId, "A")
        val b = repository.create(listId, "B")
        val c = repository.create(listId, "C")
        val viewModel = ItemsViewModel(repository, listId)
        collectState(viewModel)

        viewModel.toggleItemBought(b)
        advanceUntilIdle()

        assertEquals(listOf("A", "C", "B"), viewModel.uiState.value.items.map { it.name })

        viewModel.toggleItemBought(b)
        advanceUntilIdle()

        assertEquals(listOf("A", "B", "C"), viewModel.uiState.value.items.map { it.name })
    }

    private fun TestScope.collectState(viewModel: ItemsViewModel) {
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect { }
        }
    }
}

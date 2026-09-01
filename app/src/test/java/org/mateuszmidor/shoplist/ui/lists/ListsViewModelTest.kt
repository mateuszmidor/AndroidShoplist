package org.mateuszmidor.shoplist.ui.lists

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
import org.junit.Before
import org.junit.Test
import java.util.UUID
import org.mateuszmidor.shoplist.ui.items.FakeShoppingItemRepository

@OptIn(ExperimentalCoroutinesApi::class)
class ListsViewModelTest {

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
    fun observe_listsEntitiesInRepositoryEmissionOrder() = runTest(dispatcher) {
        val repository = FakeShoppingListRepository()
        repository.seed("First", "Second")
        val viewModel = ListsViewModel(repository, FakeShoppingItemRepository(), NoopClipboard())
        collectState(viewModel)

        advanceUntilIdle()

        assertEquals(listOf("First", "Second"), viewModel.uiState.value.lists.map { it.name })
    }

    @Test
    fun createList_withTrimmedName_appendsNewListAsLast() = runTest(dispatcher) {
        val repository = FakeShoppingListRepository()
        repository.seed("First")
        val viewModel = ListsViewModel(repository, FakeShoppingItemRepository(), NoopClipboard())
        collectState(viewModel)

        viewModel.createList("  Weekly groceries  ")
        advanceUntilIdle()

        assertEquals(listOf("First", "Weekly groceries"), viewModel.uiState.value.lists.map { it.name })
    }

    @Test
    fun createList_withBlankName_emitsNoChange() = runTest(dispatcher) {
        val repository = FakeShoppingListRepository()
        repository.seed("First")
        val viewModel = ListsViewModel(repository, FakeShoppingItemRepository(), NoopClipboard())
        collectState(viewModel)

        viewModel.createList("   ")
        advanceUntilIdle()

        assertEquals(listOf("First"), viewModel.uiState.value.lists.map { it.name })
    }

    @Test
    fun renameList_preservesIdAndCreationTime() = runTest(dispatcher) {
        val repository = FakeShoppingListRepository()
        val id = repository.create("Groceries")
        val created = repository.observeLists().first().single()
        val viewModel = ListsViewModel(repository, FakeShoppingItemRepository(), NoopClipboard())
        collectState(viewModel)

        viewModel.renameList(id, "Weekly groceries")
        advanceUntilIdle()

        val renamed = viewModel.uiState.value.lists.single()
        assertEquals(id, renamed.id)
        assertEquals(created.createdAt, renamed.createdAt)
        assertEquals("Weekly groceries", renamed.name)
    }

    @Test
    fun deleteList_removesListFromState() = runTest(dispatcher) {
        val repository = FakeShoppingListRepository()
        val toDelete = repository.create("Groceries")
        val keep = repository.create("Books")
        val viewModel = ListsViewModel(repository, FakeShoppingItemRepository(), NoopClipboard())
        collectState(viewModel)

        viewModel.deleteList(toDelete)
        advanceUntilIdle()

        assertEquals(listOf(keep), viewModel.uiState.value.lists.map { it.id })
    }

    @Test
    fun observeLists_exposesSummariesFromRepository() = runTest(dispatcher) {
        val repository = FakeShoppingListRepository()
        val groceries = repository.create("Groceries")
        val books = repository.create("Books")
        repository.seedSummary(groceries, total = 3, bought = 1)
        repository.seedSummary(books, total = 0, bought = 0)
        val viewModel = ListsViewModel(repository, FakeShoppingItemRepository(), NoopClipboard())
        collectState(viewModel)

        advanceUntilIdle()

        val byId = viewModel.uiState.value.lists.associateBy { it.id }
        assertEquals(3, byId.getValue(groceries).totalCount)
        assertEquals(1, byId.getValue(groceries).boughtCount)
        assertEquals(0, byId.getValue(books).totalCount)
        assertEquals(0, byId.getValue(books).boughtCount)
    }

    @Test
    fun exportListItems_formatsFetchedItemsInDisplayOrderAndCopiesToClipboard() = runTest(dispatcher) {
        val itemRepository = FakeShoppingItemRepository()
        val listId = UUID.randomUUID()
        itemRepository.create(listId, "mleko")
        itemRepository.create(listId, "jajka")
        val itemToBuy = itemRepository.getAllByList(listId).first { it.name == "jajka" }
        itemRepository.toggleBought(itemToBuy.id)
        val clipboard = RecordingClipboard()
        val viewModel = ListsViewModel(FakeShoppingListRepository(), itemRepository, clipboard)

        viewModel.exportListItems(listId)
        advanceUntilIdle()

        assertEquals("\u2022 mleko\n\u2022 jajka\n", clipboard.copied)
    }

    @Test
    fun exportListItems_emptyList_copiesEmptyString() = runTest(dispatcher) {
        val itemRepository = FakeShoppingItemRepository()
        val clipboard = RecordingClipboard()
        val viewModel = ListsViewModel(FakeShoppingListRepository(), itemRepository, clipboard)

        viewModel.exportListItems(UUID.randomUUID())
        advanceUntilIdle()

        assertEquals("", clipboard.copied)
    }

    private fun TestScope.collectState(viewModel: ListsViewModel) {
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect { }
        }
    }

    private class NoopClipboard : ListClipboard {
        override fun copy(text: String) = Unit
    }

    private class RecordingClipboard : ListClipboard {
        var copied: String? = null
        override fun copy(text: String) {
            copied = text
        }
    }
}
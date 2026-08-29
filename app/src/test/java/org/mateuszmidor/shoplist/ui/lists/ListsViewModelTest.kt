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
        val viewModel = ListsViewModel(repository)
        collectState(viewModel)

        advanceUntilIdle()

        assertEquals(listOf("First", "Second"), viewModel.uiState.value.lists.map { it.name })
    }

    @Test
    fun createList_withTrimmedName_appendsNewListAsLast() = runTest(dispatcher) {
        val repository = FakeShoppingListRepository()
        repository.seed("First")
        val viewModel = ListsViewModel(repository)
        collectState(viewModel)

        viewModel.createList("  Weekly groceries  ")
        advanceUntilIdle()

        assertEquals(listOf("First", "Weekly groceries"), viewModel.uiState.value.lists.map { it.name })
    }

    @Test
    fun createList_withBlankName_emitsNoChange() = runTest(dispatcher) {
        val repository = FakeShoppingListRepository()
        repository.seed("First")
        val viewModel = ListsViewModel(repository)
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
        val viewModel = ListsViewModel(repository)
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
        val viewModel = ListsViewModel(repository)
        collectState(viewModel)

        viewModel.deleteList(toDelete)
        advanceUntilIdle()

        assertEquals(listOf(keep), viewModel.uiState.value.lists.map { it.id })
    }

    private fun TestScope.collectState(viewModel: ListsViewModel) {
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect { }
        }
    }
}
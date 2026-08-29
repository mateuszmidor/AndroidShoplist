package org.mateuszmidor.shoplist.ui.lists

import android.content.Context
import androidx.room3.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mateuszmidor.shoplist.data.RoomShoppingListRepository
import org.mateuszmidor.shoplist.data.ShoppingDatabase

/**
 * End-to-end wiring of [ListsViewModel] over the real Room-backed
 * [RoomShoppingListRepository] on an in-memory [ShoppingDatabase] (ADR-0008:
 * ViewModel methods are the single write path; emitted state is the
 * repository flow). The ViewModel runs on the device's real Main dispatcher;
 * waits use the DB-backed flows so assertions observe actual persistence.
 */
@RunWith(AndroidJUnit4::class)
class ListsViewModelIntegrationTest {

    private lateinit var database: ShoppingDatabase
    private lateinit var viewModel: ListsViewModel

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder<ShoppingDatabase>(context).build()
        val repository = RoomShoppingListRepository(database.shoppingListDao())
        viewModel = ListsViewModel(repository)
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun createList_surfacesTheTrimmedListInUiState() = runBlocking {
        viewModel.createList("  Weekly groceries  ")

        val state = uiStateUntil { it.lists.isNotEmpty() }

        val list = state.lists.single()
        assertEquals("Weekly groceries", list.name)
    }

    @Test
    fun renameAndDelete_propagateToUiStateAndDatabase() = runBlocking {
        viewModel.createList("Groceries")
        val id = uiStateUntil { it.lists.isNotEmpty() }.lists.single().id
        viewModel.createList("Books")
        val ids = uiStateUntil { it.lists.size == 2 }.lists.map { it.id }.toSet()

        viewModel.renameList(id, "Weekly")
        uiStateUntil { state -> state.lists.first { it.id == id }.name == "Weekly" }
        database.shoppingListDao().observeAll()
            .first { lists -> lists.first { it.id == id }.name == "Weekly" }

        viewModel.deleteList(id)
        uiStateUntil { state -> state.lists.map { it.id }.toSet() == ids - id }
        val dbLists = database.shoppingListDao().observeAll().first { lists -> lists.size == 1 }
        val remaining = dbLists.single()

        assertEquals((ids - id).single(), remaining.id)
        assertEquals("Books", remaining.name)
        assertEquals(listOf("Books"), viewModel.uiState.value.lists.map { it.name })
    }

    private suspend fun uiStateUntil(predicate: (ListsUiState) -> Boolean): ListsUiState =
        withTimeout(5_000) { viewModel.uiState.first(predicate) }
}
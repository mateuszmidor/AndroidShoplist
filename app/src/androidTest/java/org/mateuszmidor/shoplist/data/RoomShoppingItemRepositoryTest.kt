package org.mateuszmidor.shoplist.data

import android.content.Context
import androidx.room3.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import java.io.File
import java.util.UUID
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RoomShoppingItemRepositoryTest {

    private lateinit var database: ShoppingDatabase
    private lateinit var repository: RoomShoppingItemRepository
    private lateinit var listDao: ShoppingListDao

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder<ShoppingDatabase>(context).build()
        listDao = database.shoppingListDao()
        repository = RoomShoppingItemRepository(database.shoppingItemDao())
    }

    @After
    fun tearDown() {
        database.close()
    }

    private suspend fun createList(): UUID {
        val id = UUID.randomUUID()
        listDao.insert(ShoppingListEntity(id = id, name = "List", createdAt = 0))
        return id
    }

    @Test
    fun create_returnsStoredUuidScopedToList() = runTest {
        val listId = createList()
        val id = repository.create(listId, "Milk")

        val item = repository.observeItems(listId).first().single()
        assertEquals(id, item.id)
        assertEquals("Milk", item.name)
        assertEquals(listId, item.listId)
        assertEquals(false, item.bought)
    }

    @Test
    fun rename_preservesIdListAndCreationTime() = runTest {
        val listId = createList()
        val id = repository.create(listId, "Milk")
        val created = repository.observeItems(listId).first().single().createdAt

        repository.rename(id, "Soy milk")

        val item = repository.observeItems(listId).first().single()
        assertEquals(id, item.id)
        assertEquals(listId, item.listId)
        assertEquals(created, item.createdAt)
        assertEquals("Soy milk", item.name)
    }

    @Test
    fun delete_removesTheItem() = runTest {
        val listId = createList()
        val toDelete = repository.create(listId, "Milk")
        val keep = repository.create(listId, "Bread")

        repository.delete(toDelete)

        val items = repository.observeItems(listId).first()
        assertEquals(listOf(keep), items.map { it.id })
    }

    @Test
    fun observe_emitsItemsInCreationOrder() = runTest {
        val listId = createList()
        val first = repository.create(listId, "One")
        val second = repository.create(listId, "Two")
        val third = repository.create(listId, "Three")

        val ids = repository.observeItems(listId).first().map { it.id }

        assertEquals(listOf(first, second, third), ids)
    }

    @Test
    fun getAllByList_returnsOnlyItemsScopedToTheListInDisplayOrder() = runTest {
        val listId = createList()
        val otherListId = createList()
        val first = repository.create(listId, "One")
        val second = repository.create(listId, "Two")
        repository.create(otherListId, "Other")

        val items = repository.getAllByList(listId)

        assertEquals(listOf(first, second), items.map { it.id })
        assertEquals(listOf("One", "Two"), items.map { it.name })
    }

    @Test
    fun getAllByList_ordersUncheckedBeforeCheckedByCreationTime() = runTest {
        val listId = createList()
        val bread = repository.create(listId, "Bread")
        val milk = repository.create(listId, "Milk")
        val eggs = repository.create(listId, "Eggs")
        repository.toggleBought(milk)

        val names = repository.getAllByList(listId).map { it.name }

        assertEquals(listOf("Bread", "Eggs", "Milk"), names)
        assertEquals(listOf(bread, eggs, milk), repository.getAllByList(listId).map { it.id })
    }

    @Test
    fun getAllByList_listWithNoItems_returnsEmptyList() = runTest {
        val listId = createList()

        assertTrue(repository.getAllByList(listId).isEmpty())
    }

    @Test
    fun createAll_persistsEveryItemInPastedOrderAfterExistingUnchecked() = runTest {
        val listId = createList()
        repository.create(listId, "Bread")
        val ids = repository.createAll(listId, listOf("Milk", "Eggs", "Mleko"))

        val items = repository.observeItems(listId).first()
        assertEquals(
            listOf("Bread", "Milk", "Eggs", "Mleko"),
            items.map { it.name },
        )
        val imported = items.drop(1)
        assertEquals(ids, imported.map { it.id })
        assertEquals(listOf(false, false, false), imported.map { it.bought })
        assertEquals(listOf(listId, listId, listId), imported.map { it.listId })
        assertTrue(imported.map { it.createdAt }.distinct().size == imported.size)
    }

    @Test
    fun createAll_failingBatch_persistsNothing() = runTest {
        val listId = createList()
        repository.create(listId, "Bread")
        val missingListId = UUID.randomUUID()

        try {
            repository.createAll(missingListId, listOf("Milk", "Eggs"))
            throw AssertionError("expected the batch insert to throw")
        } catch (expected: Exception) {
            // FK violation on the non-existent list id throws — expected.
        }

        val items = repository.observeItems(listId).first()
        assertEquals(listOf("Bread"), items.map { it.name })
    }

    @Test
    fun createdItem_survivesDatabaseReopen() = runTest {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val dbFile = File(context.cacheDir, "item-repository-reopen-test.db")

        val created = createAndRecord(dbFile)
        verifyReopened(dbFile, created)
        deleteDatabase(dbFile)
    }

    private suspend fun createAndRecord(dbFile: File): Triple<UUID, String, Long> {
        val db = Room.databaseBuilder<ShoppingDatabase>(context(), dbFile.absolutePath).build()
        try {
            val listId = RoomShoppingListRepository(db.shoppingListDao()).create("List")
            val repo = RoomShoppingItemRepository(db.shoppingItemDao())
            val id = repo.create(listId, "Milk")
            val item = repo.observeItems(listId).first().single()
            return Triple(id, item.name, item.createdAt)
        } finally {
            db.close()
        }
    }

    private suspend fun verifyReopened(dbFile: File, expected: Triple<UUID, String, Long>) {
        val db = Room.databaseBuilder<ShoppingDatabase>(context(), dbFile.absolutePath).build()
        try {
            val listId = RoomShoppingListRepository(db.shoppingListDao())
                .observeLists().first().single().id
            val reopened = RoomShoppingItemRepository(db.shoppingItemDao())
                .observeItems(listId).first().single()
            assertEquals(expected.first, reopened.id)
            assertEquals(expected.second, reopened.name)
            assertEquals(expected.third, reopened.createdAt)
        } finally {
            db.close()
        }
    }

    private fun deleteDatabase(dbFile: File) {
        dbFile.delete()
        File(dbFile.absolutePath + "-wal").delete()
        File(dbFile.absolutePath + "-shm").delete()
    }

    private fun context(): Context =
        InstrumentationRegistry.getInstrumentation().targetContext
}

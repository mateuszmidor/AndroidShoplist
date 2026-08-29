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
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RoomShoppingListRepositoryTest {

    private lateinit var database: ShoppingDatabase
    private lateinit var repository: RoomShoppingListRepository

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder<ShoppingDatabase>(context).build()
        repository = RoomShoppingListRepository(database.shoppingListDao())
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun create_returnsStoredUuid() = runTest {
        val id = repository.create("Weekly groceries")

        val list = repository.observeLists().first().single()
        assertEquals(id, list.id)
        assertEquals("Weekly groceries", list.name)
    }

    @Test
    fun rename_preservesIdAndCreationTime() = runTest {
        val id = repository.create("Groceries")
        val created = repository.observeLists().first().single().createdAt

        repository.rename(id, "Weekly groceries")

        val list = repository.observeLists().first().single()
        assertEquals(id, list.id)
        assertEquals(created, list.createdAt)
        assertEquals("Weekly groceries", list.name)
    }

    @Test
    fun delete_removesTheList() = runTest {
        val id = repository.create("Groceries")
        val otherId = repository.create("Books")

        repository.delete(id)

        val lists = repository.observeLists().first()
        assertEquals(listOf(otherId), lists.map { it.id })
    }

    @Test
    fun observe_emitsListsInCreationOrder() = runTest {
        val first = repository.create("First")
        val second = repository.create("Second")
        val third = repository.create("Third")

        val ids = repository.observeLists().first().map { it.id }

        assertEquals(listOf(first, second, third), ids)
    }

    @Test
    fun observeLists_returnsSummaries() = runTest {
        val id = repository.create("Groceries")

        val summary = repository.observeLists().first().single()

        assertEquals(id, summary.id)
        assertEquals("Groceries", summary.name)
        assertEquals(0, summary.totalCount)
        assertEquals(0, summary.boughtCount)
    }

    @Test
    fun observeList_returnsTheListById_orNothingForUnknown() = runTest {
        val id = repository.create("Groceries")

        val known = repository.observeList(id).first()
        assertEquals(id, known?.id)
        assertEquals("Groceries", known?.name)

        val unknown = repository.observeList(UUID.randomUUID()).first()
        assertEquals(null, unknown)
    }

    @Test
    fun createdList_survivesDatabaseReopen() = runTest {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val dbFile = File(context.cacheDir, "repository-reopen-test.db")

        val created = createAndRecord(dbFile)
        verifyReopened(dbFile, created)
        deleteDatabase(dbFile)
    }

    private suspend fun createAndRecord(dbFile: File): Triple<UUID, String, Long> {
        val db = Room.databaseBuilder<ShoppingDatabase>(context(), dbFile.absolutePath).build()
        try {
            val repo = RoomShoppingListRepository(db.shoppingListDao())
            val id = repo.create("Weekly groceries")
            val list = repo.observeLists().first().single()
            return Triple(id, list.name, list.createdAt)
        } finally {
            db.close()
        }
    }

    private suspend fun verifyReopened(dbFile: File, expected: Triple<UUID, String, Long>) {
        val db = Room.databaseBuilder<ShoppingDatabase>(context(), dbFile.absolutePath).build()
        try {
            val repo = RoomShoppingListRepository(db.shoppingListDao())
            val reopened = repo.observeLists().first().single()
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
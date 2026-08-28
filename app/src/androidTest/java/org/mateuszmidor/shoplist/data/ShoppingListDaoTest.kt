package org.mateuszmidor.shoplist.data

import android.content.Context
import androidx.room3.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
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
class ShoppingListDaoTest {

    private lateinit var database: ShoppingDatabase
    private lateinit var dao: ShoppingListDao

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder<ShoppingDatabase>(context).build()
        dao = database.shoppingListDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun insert_thenObserve_emitsListsInCreationOrder() = runTest {
        dao.insert(entity("One", createdAt = 100))
        dao.insert(entity("Two", createdAt = 200))
        dao.insert(entity("Three", createdAt = 300))

        val lists = dao.observeAll().first()

        assertEquals(listOf("One", "Two", "Three"), lists.map { it.name })
    }

    @Test
    fun renameById_updatesNameAndKeepsIdAndCreatedAt() = runTest {
        val entity = entity("Groceries", createdAt = 100)
        dao.insert(entity)

        dao.renameById(entity.id, "Weekly groceries")

        val list = dao.observeAll().first().single()
        assertEquals("Weekly groceries", list.name)
        assertEquals(entity.id, list.id)
        assertEquals(entity.createdAt, list.createdAt)
    }

    @Test
    fun deleteById_removesListFromStream() = runTest {
        val entity = entity("Groceries", createdAt = 100)
        dao.insert(entity)

        dao.deleteById(entity.id)

        assertTrue(dao.observeAll().first().isEmpty())
    }

    @Test
    fun deleteById_unknownId_doesNotChangeDatabase() = runTest {
        val entity = entity("Groceries", createdAt = 100)
        dao.insert(entity)

        dao.deleteById(UUID.randomUUID())

        val list = dao.observeAll().first().single()
        assertEquals(entity.id, list.id)
        assertEquals("Groceries", list.name)
    }

    private fun entity(name: String, createdAt: Long) =
        ShoppingListEntity(id = UUID.randomUUID(), name = name, createdAt = createdAt)
}
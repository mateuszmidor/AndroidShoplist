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
    private lateinit var itemDao: ShoppingItemDao

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder<ShoppingDatabase>(context).build()
        dao = database.shoppingListDao()
        itemDao = database.shoppingItemDao()
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

        val lists = dao.observeListSummaries().first()

        assertEquals(listOf("One", "Two", "Three"), lists.map { it.name })
    }

    @Test
    fun renameById_updatesNameAndKeepsIdAndCreatedAt() = runTest {
        val entity = entity("Groceries", createdAt = 100)
        dao.insert(entity)

        dao.renameById(entity.id, "Weekly groceries")

        val list = dao.observeListSummaries().first().single()
        assertEquals("Weekly groceries", list.name)
        assertEquals(entity.id, list.id)
        assertEquals(entity.createdAt, list.createdAt)
    }

    @Test
    fun deleteById_removesListFromStream() = runTest {
        val entity = entity("Groceries", createdAt = 100)
        dao.insert(entity)

        dao.deleteById(entity.id)

        assertTrue(dao.observeListSummaries().first().isEmpty())
    }

    @Test
    fun deleteById_unknownId_doesNotChangeDatabase() = runTest {
        val entity = entity("Groceries", createdAt = 100)
        dao.insert(entity)

        dao.deleteById(UUID.randomUUID())

        val list = dao.observeListSummaries().first().single()
        assertEquals(entity.id, list.id)
        assertEquals("Groceries", list.name)
    }

    @Test
    fun summaries_carryTotalAndBoughtCounts() = runTest {
        val list = entity("Groceries", createdAt = 100)
        dao.insert(list)
        val a = item(list.id, "A", createdAt = 100)
        val b = item(list.id, "B", createdAt = 200)
        val c = item(list.id, "C", createdAt = 300)
        itemDao.insert(a)
        itemDao.insert(b)
        itemDao.insert(c)

        itemDao.toggleBought(b.id)

        val summary = dao.observeListSummaries().first().single()
        assertEquals(3, summary.totalCount)
        assertEquals(1, summary.boughtCount)
    }

    @Test
    fun summaries_zeroCountsForEmptyLists() = runTest {
        val list = entity("Groceries", createdAt = 100)
        dao.insert(list)

        val summary = dao.observeListSummaries().first().single()
        assertEquals(0, summary.totalCount)
        assertEquals(0, summary.boughtCount)
    }

    @Test
    fun summaries_updateOnItemInsertToggleAndDelete() = runTest {
        val list = entity("Groceries", createdAt = 100)
        dao.insert(list)
        val a = item(list.id, "A", createdAt = 100)
        val b = item(list.id, "B", createdAt = 200)
        itemDao.insert(a)
        itemDao.insert(b)

        assertEquals(2, dao.observeListSummaries().first().single().totalCount)

        itemDao.toggleBought(a.id)
        assertEquals(1, dao.observeListSummaries().first().single().boughtCount)

        itemDao.deleteById(a.id)
        val summary = dao.observeListSummaries().first().single()
        assertEquals(1, summary.totalCount)
        assertEquals(0, summary.boughtCount)
    }

    @Test
    fun observeById_emitsListForKnownId_andNothingForUnknown() = runTest {
        val list = entity("Groceries", createdAt = 100)
        dao.insert(list)

        val known = dao.observeById(list.id).first()
        assertEquals(list.id, known?.id)
        assertEquals("Groceries", known?.name)

        val unknown = dao.observeById(UUID.randomUUID()).first()
        assertTrue(unknown == null)
    }

    private fun entity(name: String, createdAt: Long) =
        ShoppingListEntity(id = UUID.randomUUID(), name = name, createdAt = createdAt)

    private fun item(listId: UUID, name: String, createdAt: Long) =
        ShoppingItemEntity(id = UUID.randomUUID(), listId = listId, name = name, createdAt = createdAt)
}

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
class ShoppingItemDaoTest {

    private lateinit var database: ShoppingDatabase
    private lateinit var itemDao: ShoppingItemDao
    private lateinit var listDao: ShoppingListDao

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder<ShoppingDatabase>(context).build()
        itemDao = database.shoppingItemDao()
        listDao = database.shoppingListDao()
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
    fun insert_thenObserve_filtersByListAndOrdersByCreationTime() = runTest {
        val listA = createList()
        val listB = createList()
        itemDao.insert(entity(listId = listA, name = "One", createdAt = 100))
        itemDao.insert(entity(listId = listA, name = "Two", createdAt = 200))
        itemDao.insert(entity(listId = listA, name = "Three", createdAt = 300))
        itemDao.insert(entity(listId = listB, name = "Other", createdAt = 50))

        val items = itemDao.observeByList(listA).first()

        assertEquals(listOf("One", "Two", "Three"), items.map { it.name })
        assertTrue(items.all { it.listId == listA })
    }

    @Test
    fun renameById_updatesNameAndKeepsIdentity() = runTest {
        val listId = createList()
        val entity = entity(listId = listId, name = "Milk", createdAt = 100)
        itemDao.insert(entity)

        itemDao.renameById(entity.id, "Soy milk")

        val item = itemDao.observeByList(entity.listId).first().single()
        assertEquals("Soy milk", item.name)
        assertEquals(entity.id, item.id)
        assertEquals(entity.listId, item.listId)
        assertEquals(entity.createdAt, item.createdAt)
    }

    @Test
    fun deleteById_removesItemFromStream() = runTest {
        val listId = createList()
        val item = entity(listId = listId, name = "Milk", createdAt = 100)
        itemDao.insert(item)

        itemDao.deleteById(item.id)

        assertTrue(itemDao.observeByList(item.listId).first().isEmpty())
    }

    @Test
    fun deletingList_cascadesToItsItems() = runTest {
        val listId = createList()
        itemDao.insert(entity(listId = listId, name = "Milk", createdAt = 100))
        itemDao.insert(entity(listId = listId, name = "Bread", createdAt = 200))

        listDao.deleteById(listId)

        assertTrue(itemDao.observeByList(listId).first().isEmpty())
    }

    private fun entity(listId: UUID, name: String, createdAt: Long) =
        ShoppingItemEntity(id = UUID.randomUUID(), listId = listId, name = name, createdAt = createdAt)
}

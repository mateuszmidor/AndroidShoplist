package org.mateuszmidor.shoplist.ui.items

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import java.util.UUID
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mateuszmidor.shoplist.di.AppContainer
import org.mateuszmidor.shoplist.ui.App

@RunWith(AndroidJUnit4::class)
class ItemLookupDiacriticsUiTest {

    @get:Rule
    val composeRule = createComposeRule()

    private val container by lazy {
        AppContainer(InstrumentationRegistry.getInstrumentation().targetContext)
    }

    private lateinit var listId: UUID

    @Before
    fun seed() = runBlocking {
        container.shoppingListRepository.observeLists().first().forEach {
            container.shoppingListRepository.delete(it.id)
        }
        listId = container.shoppingListRepository.create("Lookup test")
        container.shoppingItemRepository.createAll(listId, listOf("losos", "nić dentystyczna"))
        container.shoppingItemRepository.getAllByList(listId).forEach {
            container.shoppingItemRepository.toggleBought(it.id)
        }
    }

    @Test
    fun lookupIgnoresDiacriticsInBothDirections() {
        composeRule.setContent { App(container) }
        composeRule.onNodeWithText("Lookup test").performClick()
        composeRule.onNodeWithText("+").performClick()

        composeRule.onNode(hasSetTextAction()).performTextInput("łosoś")
        assertLastDisplayed("losos")
        composeRule.onNodeWithText("Already on this list").assertIsDisplayed()

        composeRule.onNode(hasSetTextAction()).performTextClearance()
        composeRule.onNode(hasSetTextAction()).performTextInput("nic")
        assertLastDisplayed("nić dentystyczna")
    }

    private fun assertLastDisplayed(text: String) {
        val nodes = composeRule.onAllNodesWithText(text)
        nodes[nodes.fetchSemanticsNodes().lastIndex].assertIsDisplayed()
    }
}

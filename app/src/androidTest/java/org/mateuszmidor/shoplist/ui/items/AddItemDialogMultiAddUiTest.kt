package org.mateuszmidor.shoplist.ui.items

import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performImeAction
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mateuszmidor.shoplist.di.AppContainer
import org.mateuszmidor.shoplist.ui.App

@RunWith(AndroidJUnit4::class)
class AddItemDialogMultiAddUiTest {

    @get:Rule
    val composeRule = createComposeRule()

    private val container by lazy {
        AppContainer(InstrumentationRegistry.getInstrumentation().targetContext)
    }

    @Before
    fun seed() = runBlocking {
        container.shoppingListRepository.observeLists().first().forEach {
            container.shoppingListRepository.delete(it.id)
        }
        container.shoppingListRepository.create("Multi-add test")
        Unit
    }

    @Test
    fun addingItemKeepsDialogOpenClearsFieldAndConfirms() {
        openDialog()

        typeName("Milk")
        composeRule.onNodeWithText("Add").performClick()

        composeRule.onNodeWithText("Done").assertIsDisplayed()
        composeRule.onNodeWithText("Added \"Milk\"").assertIsDisplayed()
        assertEquals("", fieldText())
    }

    @Test
    fun typingAfterAddStartsFromEmptyField() {
        openDialog()

        typeName("Milk")
        composeRule.onNodeWithText("Add").performClick()
        typeName("Bread")
        composeRule.onNodeWithText("Add").performClick()

        composeRule.onNodeWithText("Added \"Bread\"").assertIsDisplayed()
        composeRule.onNodeWithText("Done").performClick()

        composeRule.onNodeWithText("Milk").assertIsDisplayed()
        composeRule.onNodeWithText("Bread").assertIsDisplayed()
        composeRule.onNodeWithText("MilkBread").assertDoesNotExist()
    }

    @Test
    fun doneClosesDialogWithoutAddingTypedName() {
        openDialog()

        typeName("Milk")
        composeRule.onNodeWithText("Add").performClick()
        typeName("Bread")
        composeRule.onNodeWithText("Add").performClick()
        typeName("Ham")
        composeRule.onNodeWithText("Done").performClick()

        composeRule.onNodeWithText("Milk").assertIsDisplayed()
        composeRule.onNodeWithText("Bread").assertIsDisplayed()
        composeRule.onNodeWithText("Ham").assertDoesNotExist()
    }

    @Test
    fun keyboardDoneAddsTypedItem() {
        openDialog()

        typeName("Milk")
        composeRule.onNode(hasSetTextAction()).performImeAction()

        composeRule.onNodeWithText("Added \"Milk\"").assertIsDisplayed()
        composeRule.onNodeWithText("Done").assertIsDisplayed()
        assertEquals("", fieldText())
    }

    @Test
    fun duplicateWarningReplacesConfirmation() {
        openDialog()

        typeName("Milk")
        composeRule.onNodeWithText("Add").performClick()
        composeRule.onNodeWithText("Added \"Milk\"").assertIsDisplayed()

        typeName("milk")
        composeRule.waitUntil(timeoutMillis = 5_000) {
            composeRule.onAllNodesWithText("Already on this list")
                .fetchSemanticsNodes().isNotEmpty()
        }

        composeRule.onNodeWithText("Added \"Milk\"").assertDoesNotExist()
        composeRule.onNodeWithText("Add").assertIsNotEnabled()
    }

    private fun openDialog() {
        composeRule.setContent { App(container) }
        composeRule.onNodeWithText("Multi-add test").performClick()
        composeRule.onNodeWithText("+").performClick()
        composeRule.onNodeWithText("Add item").performClick()
    }

    private fun typeName(name: String) {
        composeRule.onNode(hasSetTextAction()).performTextInput(name)
    }

    private fun fieldText(): String =
        composeRule.onNode(hasSetTextAction())
            .fetchSemanticsNode()
            .config[SemanticsProperties.EditableText]
            .text
}

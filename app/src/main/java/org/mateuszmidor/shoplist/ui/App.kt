package org.mateuszmidor.shoplist.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import kotlin.reflect.typeOf
import org.mateuszmidor.shoplist.di.AppContainer
import org.mateuszmidor.shoplist.navigation.Combined
import org.mateuszmidor.shoplist.navigation.Items
import org.mateuszmidor.shoplist.navigation.Lists
import org.mateuszmidor.shoplist.navigation.ListId
import org.mateuszmidor.shoplist.navigation.ListIdListNavType
import org.mateuszmidor.shoplist.navigation.ListIdNavType
import org.mateuszmidor.shoplist.ui.combined.CombinedScreen
import org.mateuszmidor.shoplist.ui.combined.CombinedViewModel
import org.mateuszmidor.shoplist.ui.items.ItemsScreen
import org.mateuszmidor.shoplist.ui.items.ItemsViewModel
import org.mateuszmidor.shoplist.ui.lists.AndroidListClipboard
import org.mateuszmidor.shoplist.ui.lists.ListsScreen
import org.mateuszmidor.shoplist.ui.lists.ListsViewModel

/**
 * Root composable of the app. Hosts the navigation graph: the lists screen as
 * the start destination and the items screen (reached via a list UUID).
 */
@Composable
fun App(container: AppContainer) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Lists,
    ) {
        composable<Lists> {
            val context = LocalContext.current
            val viewModel: ListsViewModel =
                viewModel {
                    ListsViewModel(
                        listRepository = container.shoppingListRepository,
                        itemRepository = container.shoppingItemRepository,
                        clipboard = AndroidListClipboard(context),
                    )
                }
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()
            ListsScreen(
                uiState = uiState,
                onCreateList = viewModel::createList,
                onRenameList = viewModel::renameList,
                onDeleteList = viewModel::deleteList,
                onExportItems = viewModel::exportListItems,
                onOpenList = { listId -> navController.navigate(Items(listId = ListId(listId))) },
                onEnterSelection = viewModel::enterSelectionMode,
                onToggleSelection = viewModel::toggleSelected,
                onClearSelection = viewModel::clearSelection,
                onCombine = { listIds ->
                    navController.navigate(Combined(listIds = listIds.map { ListId(it) }))
                    viewModel.clearSelection()
                },
            )
        }
        composable<Items>(typeMap = mapOf(typeOf<ListId>() to ListIdNavType)) { backStackEntry ->
            val route = backStackEntry.toRoute<Items>()
            val viewModel: ItemsViewModel =
                viewModel { ItemsViewModel(container.shoppingItemRepository, container.shoppingListRepository, route.listId.value) }
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()
            ItemsScreen(
                uiState = uiState,
                onAddItem = viewModel::addItem,
                onRenameItem = viewModel::renameItem,
                onDeleteItem = viewModel::deleteItem,
                onToggleBought = viewModel::toggleItemBought,
                onImportText = viewModel::importItems,
                onBack = { navController.popBackStack() },
            )
        }
        composable<Combined>(
            typeMap = mapOf(typeOf<List<ListId>>() to ListIdListNavType),
        ) { backStackEntry ->
            val route = backStackEntry.toRoute<Combined>()
            val listIds = route.listIds.map { it.value }
            val viewModel: CombinedViewModel =
                viewModel {
                    CombinedViewModel(
                        container.shoppingItemRepository,
                        container.shoppingListRepository,
                        listIds,
                    )
                }
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()
            CombinedScreen(
                uiState = uiState,
                onToggleBought = viewModel::toggleItemBought,
                onBack = { navController.popBackStack() },
            )
        }
    }
}
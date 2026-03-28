package com.joao.warehouse.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.joao.warehouse.ui.screens.BarcodeScannerScreen
import com.joao.warehouse.ui.screens.CategoryFormScreen
import com.joao.warehouse.ui.screens.CategoryListScreen
import com.joao.warehouse.ui.screens.DashboardScreen
import com.joao.warehouse.ui.screens.MovementHistoryScreen
import com.joao.warehouse.ui.screens.ProductFormScreen
import com.joao.warehouse.ui.screens.ProductListScreen
import com.joao.warehouse.ui.screens.StockMovementScreen
import com.joao.warehouse.ui.viewmodel.CategoryViewModel
import com.joao.warehouse.ui.viewmodel.DashboardViewModel
import com.joao.warehouse.ui.viewmodel.ProductViewModel
import com.joao.warehouse.ui.viewmodel.StockMovementViewModel

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Dashboard.route
    ) {
        composable(Screen.Dashboard.route) {
            val dashboardViewModel: DashboardViewModel = viewModel()
            val productViewModel: ProductViewModel = viewModel()
            val categoryViewModel: CategoryViewModel = viewModel()
            DashboardScreen(
                viewModel = dashboardViewModel,
                onNavigateToProducts = { navController.navigate(Screen.ProductList.route) },
                onNavigateToCategories = { navController.navigate(Screen.CategoryList.route) },
                onNavigateToMovements = { navController.navigate(Screen.MovementHistory.route) },
                onNavigateToStockMovement = { navController.navigate(Screen.StockMovement.createRoute()) }
            )
        }

        composable(Screen.ProductList.route) {
            val productViewModel: ProductViewModel = viewModel()
            ProductListScreen(
                viewModel = productViewModel,
                onNavigateToForm = { productId ->
                    navController.navigate(Screen.ProductForm.createRoute(productId))
                },
                onNavigateToStockMovement = { productId ->
                    navController.navigate(Screen.StockMovement.createRoute(productId))
                },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.ProductForm.route,
            arguments = listOf(
                navArgument("productId") {
                    type = NavType.LongType
                    defaultValue = -1L
                }
            )
        ) { backStackEntry ->
            val productViewModel: ProductViewModel = viewModel()
            val categoryViewModel: CategoryViewModel = viewModel()
            val productId = backStackEntry.arguments?.getLong("productId") ?: -1L
            val scannedBarcode = backStackEntry.savedStateHandle.get<String>("scanned_barcode")
            ProductFormScreen(
                productViewModel = productViewModel,
                categoryViewModel = categoryViewModel,
                productId = if (productId == -1L) null else productId,
                scannedBarcode = scannedBarcode,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToScanner = {
                    navController.navigate(Screen.BarcodeScanner.route)
                }
            )
        }

        composable(Screen.BarcodeScanner.route) {
            BarcodeScannerScreen(
                onBarcodeScanned = { barcodeValue ->
                    navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.set("scanned_barcode", barcodeValue)
                    navController.popBackStack()
                },
                onCancel = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.CategoryList.route) {
            val categoryViewModel: CategoryViewModel = viewModel()
            CategoryListScreen(
                viewModel = categoryViewModel,
                onNavigateToForm = { categoryId ->
                    navController.navigate(Screen.CategoryForm.createRoute(categoryId))
                },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.CategoryForm.route,
            arguments = listOf(
                navArgument("categoryId") {
                    type = NavType.LongType
                    defaultValue = -1L
                }
            )
        ) { backStackEntry ->
            val categoryViewModel: CategoryViewModel = viewModel()
            val categoryId = backStackEntry.arguments?.getLong("categoryId") ?: -1L
            CategoryFormScreen(
                viewModel = categoryViewModel,
                categoryId = if (categoryId == -1L) null else categoryId,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.StockMovement.route,
            arguments = listOf(
                navArgument("productId") {
                    type = NavType.LongType
                    defaultValue = -1L
                }
            )
        ) { backStackEntry ->
            val stockMovementViewModel: StockMovementViewModel = viewModel()
            val productId = backStackEntry.arguments?.getLong("productId") ?: -1L
            StockMovementScreen(
                viewModel = stockMovementViewModel,
                preSelectedProductId = if (productId == -1L) null else productId,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.MovementHistory.route) {
            val stockMovementViewModel: StockMovementViewModel = viewModel()
            val productViewModel: ProductViewModel = viewModel()
            MovementHistoryScreen(
                viewModel = stockMovementViewModel,
                productViewModel = productViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}

package com.joao.warehouse.ui.navigation

sealed class Screen(val route: String) {
    data object Dashboard : Screen("dashboard")
    data object ProductList : Screen("product_list")
    data object ProductForm : Screen("product_form?productId={productId}") {
        fun createRoute(productId: Long? = null): String {
            return if (productId != null) "product_form?productId=$productId" else "product_form"
        }
    }
    data object CategoryList : Screen("category_list")
    data object CategoryForm : Screen("category_form?categoryId={categoryId}") {
        fun createRoute(categoryId: Long? = null): String {
            return if (categoryId != null) "category_form?categoryId=$categoryId" else "category_form"
        }
    }
    data object StockMovement : Screen("stock_movement?productId={productId}") {
        fun createRoute(productId: Long? = null): String {
            return if (productId != null) "stock_movement?productId=$productId" else "stock_movement"
        }
    }
    data object MovementHistory : Screen("movement_history")
    data object BarcodeScanner : Screen("barcode_scanner")
}

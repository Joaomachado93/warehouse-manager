package com.joao.warehouse

import android.app.Application
import com.joao.warehouse.data.database.AppDatabase
import com.joao.warehouse.data.repository.CategoryRepository
import com.joao.warehouse.data.repository.ProductRepository
import com.joao.warehouse.data.repository.StockMovementRepository

class WarehouseApp : Application() {

    val database: AppDatabase by lazy {
        AppDatabase.getDatabase(this)
    }

    val productRepository: ProductRepository by lazy {
        ProductRepository(database.productDao())
    }

    val categoryRepository: CategoryRepository by lazy {
        CategoryRepository(database.categoryDao())
    }

    val stockMovementRepository: StockMovementRepository by lazy {
        StockMovementRepository(database.stockMovementDao())
    }
}

package com.joao.warehouse.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.joao.warehouse.WarehouseApp
import com.joao.warehouse.data.model.Product
import com.joao.warehouse.data.model.StockMovement
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class DashboardViewModel(application: Application) : AndroidViewModel(application) {

    private val app = application as WarehouseApp
    private val productRepository = app.productRepository
    private val stockMovementRepository = app.stockMovementRepository

    val productCount: StateFlow<Int> = productRepository.getProductCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val lowStockCount: StateFlow<Int> = productRepository.getLowStockCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val lowStockProducts: StateFlow<List<Product>> = productRepository.getLowStockProducts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val recentMovements: StateFlow<List<StockMovement>> = stockMovementRepository.getRecentMovements(10)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
}

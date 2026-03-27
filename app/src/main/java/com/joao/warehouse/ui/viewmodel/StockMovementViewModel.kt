package com.joao.warehouse.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.joao.warehouse.WarehouseApp
import com.joao.warehouse.data.model.MovementType
import com.joao.warehouse.data.model.Product
import com.joao.warehouse.data.model.StockMovement
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class StockMovementViewModel(application: Application) : AndroidViewModel(application) {

    private val app = application as WarehouseApp
    private val stockMovementRepository = app.stockMovementRepository
    private val productRepository = app.productRepository

    val allMovements: StateFlow<List<StockMovement>> = stockMovementRepository.getAllMovements()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allProducts: StateFlow<List<Product>> = productRepository.getAllProducts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun registerMovement(productId: Long, type: MovementType, quantity: Int, reason: String) {
        viewModelScope.launch {
            val product = productRepository.getProductById(productId) ?: return@launch

            val newQuantity = when (type) {
                MovementType.IN -> product.quantity + quantity
                MovementType.OUT -> (product.quantity - quantity).coerceAtLeast(0)
            }

            productRepository.update(
                product.copy(
                    quantity = newQuantity,
                    updatedAt = System.currentTimeMillis()
                )
            )

            stockMovementRepository.insert(
                StockMovement(
                    productId = productId,
                    type = type,
                    quantity = quantity,
                    reason = reason
                )
            )
        }
    }

    suspend fun getProductById(id: Long): Product? {
        return productRepository.getProductById(id)
    }
}

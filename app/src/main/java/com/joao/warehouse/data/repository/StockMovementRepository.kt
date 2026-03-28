package com.joao.warehouse.data.repository

import com.joao.warehouse.data.dao.StockMovementDao
import com.joao.warehouse.data.model.StockMovement
import kotlinx.coroutines.flow.Flow

class StockMovementRepository(private val stockMovementDao: StockMovementDao) {

    fun getAllMovements(): Flow<List<StockMovement>> = stockMovementDao.getAllMovements()

    fun getMovementsByProduct(productId: Long): Flow<List<StockMovement>> =
        stockMovementDao.getMovementsByProduct(productId)

    fun getRecentMovements(limit: Int = 10): Flow<List<StockMovement>> =
        stockMovementDao.getRecentMovements(limit)

    suspend fun insert(movement: StockMovement): Long = stockMovementDao.insert(movement)

    fun getMovementCount(): Flow<Int> = stockMovementDao.getMovementCount()
}

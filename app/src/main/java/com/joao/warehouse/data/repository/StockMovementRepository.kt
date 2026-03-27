package com.joao.warehouse.data.repository

import com.joao.warehouse.data.model.StockMovement
import com.joao.warehouse.data.sync.FirestoreSync
import kotlinx.coroutines.flow.Flow

class StockMovementRepository(private val firestoreSync: FirestoreSync) {

    fun getAllMovements(): Flow<List<StockMovement>> = firestoreSync.getAllMovements()

    fun getMovementsByProduct(productId: Long): Flow<List<StockMovement>> =
        firestoreSync.getMovementsByProduct(productId)

    fun getRecentMovements(limit: Int = 10): Flow<List<StockMovement>> =
        firestoreSync.getRecentMovements(limit)

    suspend fun insert(movement: StockMovement): Long = firestoreSync.insertMovement(movement)

    fun getMovementCount(): Flow<Int> = firestoreSync.getMovementCount()
}

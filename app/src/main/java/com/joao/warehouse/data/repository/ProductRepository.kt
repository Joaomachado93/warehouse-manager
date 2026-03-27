package com.joao.warehouse.data.repository

import com.joao.warehouse.data.model.Product
import com.joao.warehouse.data.sync.FirestoreSync
import kotlinx.coroutines.flow.Flow

class ProductRepository(private val firestoreSync: FirestoreSync) {

    fun getAllProducts(): Flow<List<Product>> = firestoreSync.getAllProducts()

    suspend fun getProductById(id: Long): Product? = firestoreSync.getProductById(id)

    fun searchProducts(query: String): Flow<List<Product>> = firestoreSync.searchProducts(query)

    fun getProductsByCategory(categoryId: Long): Flow<List<Product>> =
        firestoreSync.getProductsByCategory(categoryId)

    fun getLowStockProducts(): Flow<List<Product>> = firestoreSync.getLowStockProducts()

    fun getProductCount(): Flow<Int> = firestoreSync.getProductCount()

    fun getLowStockCount(): Flow<Int> = firestoreSync.getLowStockCount()

    suspend fun insert(product: Product): Long = firestoreSync.insertProduct(product)

    suspend fun update(product: Product) = firestoreSync.updateProduct(product)

    suspend fun delete(product: Product) = firestoreSync.deleteProduct(product)
}

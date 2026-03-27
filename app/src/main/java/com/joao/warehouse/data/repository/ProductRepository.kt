package com.joao.warehouse.data.repository

import com.joao.warehouse.data.dao.ProductDao
import com.joao.warehouse.data.model.Product
import kotlinx.coroutines.flow.Flow

class ProductRepository(private val productDao: ProductDao) {

    fun getAllProducts(): Flow<List<Product>> = productDao.getAllProducts()

    suspend fun getProductById(id: Long): Product? = productDao.getProductById(id)

    fun searchProducts(query: String): Flow<List<Product>> = productDao.searchProducts(query)

    fun getProductsByCategory(categoryId: Long): Flow<List<Product>> =
        productDao.getProductsByCategory(categoryId)

    fun getLowStockProducts(): Flow<List<Product>> = productDao.getLowStockProducts()

    fun getProductCount(): Flow<Int> = productDao.getProductCount()

    fun getLowStockCount(): Flow<Int> = productDao.getLowStockCount()

    suspend fun insert(product: Product): Long = productDao.insert(product)

    suspend fun update(product: Product) = productDao.update(product)

    suspend fun delete(product: Product) = productDao.delete(product)
}

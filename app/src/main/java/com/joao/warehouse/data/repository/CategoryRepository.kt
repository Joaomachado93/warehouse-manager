package com.joao.warehouse.data.repository

import com.joao.warehouse.data.dao.CategoryDao
import com.joao.warehouse.data.model.Category
import kotlinx.coroutines.flow.Flow

class CategoryRepository(private val categoryDao: CategoryDao) {

    fun getAllCategories(): Flow<List<Category>> = categoryDao.getAllCategories()

    suspend fun getCategoryById(id: Long): Category? = categoryDao.getCategoryById(id)

    suspend fun insert(category: Category): Long = categoryDao.insert(category)

    suspend fun update(category: Category) = categoryDao.update(category)

    suspend fun delete(category: Category) = categoryDao.delete(category)

    fun getCategoryCount(): Flow<Int> = categoryDao.getCategoryCount()
}

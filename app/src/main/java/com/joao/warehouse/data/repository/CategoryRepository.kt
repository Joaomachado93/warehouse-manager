package com.joao.warehouse.data.repository

import com.joao.warehouse.data.model.Category
import com.joao.warehouse.data.sync.FirestoreSync
import kotlinx.coroutines.flow.Flow

class CategoryRepository(private val firestoreSync: FirestoreSync) {

    fun getAllCategories(): Flow<List<Category>> = firestoreSync.getAllCategories()

    suspend fun getCategoryById(id: Long): Category? = firestoreSync.getCategoryById(id)

    suspend fun insert(category: Category): Long = firestoreSync.insertCategory(category)

    suspend fun update(category: Category) = firestoreSync.updateCategory(category)

    suspend fun delete(category: Category) = firestoreSync.deleteCategory(category)

    fun getCategoryCount(): Flow<Int> = firestoreSync.getCategoryCount()
}

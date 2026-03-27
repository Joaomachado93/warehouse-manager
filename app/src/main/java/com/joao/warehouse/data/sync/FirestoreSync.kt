package com.joao.warehouse.data.sync

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.joao.warehouse.data.model.Category
import com.joao.warehouse.data.model.MovementType
import com.joao.warehouse.data.model.Product
import com.joao.warehouse.data.model.StockMovement
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class FirestoreSync(
    private val firestore: FirebaseFirestore,
    private val warehouseId: String
) {

    // --- Collection references ---

    private val productsCollection
        get() = firestore.collection("warehouses").document(warehouseId).collection("products")

    private val categoriesCollection
        get() = firestore.collection("warehouses").document(warehouseId).collection("categories")

    private val movementsCollection
        get() = firestore.collection("warehouses").document(warehouseId).collection("movements")

    // ========== PRODUCTS ==========

    fun getAllProducts(): Flow<List<Product>> = callbackFlow {
        val registration: ListenerRegistration = productsCollection
            .orderBy("name", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val products = snapshot?.documents?.mapNotNull { doc ->
                    documentToProduct(doc.id, doc.data)
                } ?: emptyList()
                trySend(products)
            }
        awaitClose { registration.remove() }
    }

    fun searchProducts(query: String): Flow<List<Product>> = callbackFlow {
        val registration: ListenerRegistration = productsCollection
            .orderBy("name", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val lowerQuery = query.lowercase()
                val products = snapshot?.documents?.mapNotNull { doc ->
                    documentToProduct(doc.id, doc.data)
                }?.filter { product ->
                    product.name.lowercase().contains(lowerQuery) ||
                            product.sku.lowercase().contains(lowerQuery) ||
                            product.barcode.lowercase().contains(lowerQuery)
                } ?: emptyList()
                trySend(products)
            }
        awaitClose { registration.remove() }
    }

    fun getProductsByCategory(categoryId: Long): Flow<List<Product>> = callbackFlow {
        val registration: ListenerRegistration = productsCollection
            .whereEqualTo("categoryId", categoryId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val products = snapshot?.documents?.mapNotNull { doc ->
                    documentToProduct(doc.id, doc.data)
                }?.sortedBy { it.name } ?: emptyList()
                trySend(products)
            }
        awaitClose { registration.remove() }
    }

    fun getLowStockProducts(): Flow<List<Product>> = callbackFlow {
        val registration: ListenerRegistration = productsCollection
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val products = snapshot?.documents?.mapNotNull { doc ->
                    documentToProduct(doc.id, doc.data)
                }?.filter { it.quantity < it.minStockLevel }
                    ?.sortedBy { it.name } ?: emptyList()
                trySend(products)
            }
        awaitClose { registration.remove() }
    }

    fun getProductCount(): Flow<Int> = callbackFlow {
        val registration: ListenerRegistration = productsCollection
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                trySend(snapshot?.size() ?: 0)
            }
        awaitClose { registration.remove() }
    }

    fun getLowStockCount(): Flow<Int> = callbackFlow {
        val registration: ListenerRegistration = productsCollection
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val count = snapshot?.documents?.mapNotNull { doc ->
                    documentToProduct(doc.id, doc.data)
                }?.count { it.quantity < it.minStockLevel } ?: 0
                trySend(count)
            }
        awaitClose { registration.remove() }
    }

    suspend fun getProductById(id: Long): Product? {
        val snapshot = productsCollection
            .whereEqualTo("id", id)
            .get()
            .await()
        return snapshot.documents.firstOrNull()?.let { doc ->
            documentToProduct(doc.id, doc.data)
        }
    }

    suspend fun insertProduct(product: Product): Long {
        val id = if (product.id == 0L) System.currentTimeMillis() else product.id
        val newProduct = product.copy(id = id)
        productsCollection.document(id.toString())
            .set(productToMap(newProduct))
            .await()
        return id
    }

    suspend fun updateProduct(product: Product) {
        productsCollection.document(product.id.toString())
            .set(productToMap(product))
            .await()
    }

    suspend fun deleteProduct(product: Product) {
        productsCollection.document(product.id.toString())
            .delete()
            .await()
    }

    // ========== CATEGORIES ==========

    fun getAllCategories(): Flow<List<Category>> = callbackFlow {
        val registration: ListenerRegistration = categoriesCollection
            .orderBy("name", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val categories = snapshot?.documents?.mapNotNull { doc ->
                    documentToCategory(doc.id, doc.data)
                } ?: emptyList()
                trySend(categories)
            }
        awaitClose { registration.remove() }
    }

    fun getCategoryCount(): Flow<Int> = callbackFlow {
        val registration: ListenerRegistration = categoriesCollection
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                trySend(snapshot?.size() ?: 0)
            }
        awaitClose { registration.remove() }
    }

    suspend fun getCategoryById(id: Long): Category? {
        val snapshot = categoriesCollection
            .whereEqualTo("id", id)
            .get()
            .await()
        return snapshot.documents.firstOrNull()?.let { doc ->
            documentToCategory(doc.id, doc.data)
        }
    }

    suspend fun insertCategory(category: Category): Long {
        val id = if (category.id == 0L) System.currentTimeMillis() else category.id
        val newCategory = category.copy(id = id)
        categoriesCollection.document(id.toString())
            .set(categoryToMap(newCategory))
            .await()
        return id
    }

    suspend fun updateCategory(category: Category) {
        categoriesCollection.document(category.id.toString())
            .set(categoryToMap(category))
            .await()
    }

    suspend fun deleteCategory(category: Category) {
        categoriesCollection.document(category.id.toString())
            .delete()
            .await()
    }

    // ========== STOCK MOVEMENTS ==========

    fun getAllMovements(): Flow<List<StockMovement>> = callbackFlow {
        val registration: ListenerRegistration = movementsCollection
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val movements = snapshot?.documents?.mapNotNull { doc ->
                    documentToMovement(doc.id, doc.data)
                } ?: emptyList()
                trySend(movements)
            }
        awaitClose { registration.remove() }
    }

    fun getMovementsByProduct(productId: Long): Flow<List<StockMovement>> = callbackFlow {
        val registration: ListenerRegistration = movementsCollection
            .whereEqualTo("productId", productId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val movements = snapshot?.documents?.mapNotNull { doc ->
                    documentToMovement(doc.id, doc.data)
                }?.sortedByDescending { it.timestamp } ?: emptyList()
                trySend(movements)
            }
        awaitClose { registration.remove() }
    }

    fun getRecentMovements(limit: Int): Flow<List<StockMovement>> = callbackFlow {
        val registration: ListenerRegistration = movementsCollection
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .limit(limit.toLong())
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val movements = snapshot?.documents?.mapNotNull { doc ->
                    documentToMovement(doc.id, doc.data)
                } ?: emptyList()
                trySend(movements)
            }
        awaitClose { registration.remove() }
    }

    fun getMovementCount(): Flow<Int> = callbackFlow {
        val registration: ListenerRegistration = movementsCollection
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                trySend(snapshot?.size() ?: 0)
            }
        awaitClose { registration.remove() }
    }

    suspend fun insertMovement(movement: StockMovement): Long {
        val id = if (movement.id == 0L) System.currentTimeMillis() else movement.id
        val newMovement = movement.copy(id = id)
        movementsCollection.document(id.toString())
            .set(movementToMap(newMovement))
            .await()
        return id
    }

    // ========== MAPPERS ==========

    private fun productToMap(product: Product): Map<String, Any?> = mapOf(
        "id" to product.id,
        "name" to product.name,
        "description" to product.description,
        "sku" to product.sku,
        "quantity" to product.quantity,
        "minStockLevel" to product.minStockLevel,
        "categoryId" to product.categoryId,
        "location" to product.location,
        "barcode" to product.barcode,
        "imageUri" to product.imageUri,
        "createdAt" to product.createdAt,
        "updatedAt" to product.updatedAt
    )

    private fun documentToProduct(docId: String, data: Map<String, Any>?): Product? {
        if (data == null) return null
        return try {
            Product(
                id = (data["id"] as? Number)?.toLong() ?: docId.toLongOrNull() ?: return null,
                name = data["name"] as? String ?: "",
                description = data["description"] as? String ?: "",
                sku = data["sku"] as? String ?: "",
                quantity = (data["quantity"] as? Number)?.toInt() ?: 0,
                minStockLevel = (data["minStockLevel"] as? Number)?.toInt() ?: 0,
                categoryId = (data["categoryId"] as? Number)?.toLong(),
                location = data["location"] as? String ?: "",
                barcode = data["barcode"] as? String ?: "",
                imageUri = data["imageUri"] as? String,
                createdAt = (data["createdAt"] as? Number)?.toLong() ?: System.currentTimeMillis(),
                updatedAt = (data["updatedAt"] as? Number)?.toLong() ?: System.currentTimeMillis()
            )
        } catch (e: Exception) {
            null
        }
    }

    private fun categoryToMap(category: Category): Map<String, Any?> = mapOf(
        "id" to category.id,
        "name" to category.name,
        "description" to category.description
    )

    private fun documentToCategory(docId: String, data: Map<String, Any>?): Category? {
        if (data == null) return null
        return try {
            Category(
                id = (data["id"] as? Number)?.toLong() ?: docId.toLongOrNull() ?: return null,
                name = data["name"] as? String ?: "",
                description = data["description"] as? String ?: ""
            )
        } catch (e: Exception) {
            null
        }
    }

    private fun movementToMap(movement: StockMovement): Map<String, Any?> = mapOf(
        "id" to movement.id,
        "productId" to movement.productId,
        "type" to movement.type.name,
        "quantity" to movement.quantity,
        "reason" to movement.reason,
        "timestamp" to movement.timestamp
    )

    private fun documentToMovement(docId: String, data: Map<String, Any>?): StockMovement? {
        if (data == null) return null
        return try {
            StockMovement(
                id = (data["id"] as? Number)?.toLong() ?: docId.toLongOrNull() ?: return null,
                productId = (data["productId"] as? Number)?.toLong() ?: return null,
                type = try {
                    MovementType.valueOf(data["type"] as? String ?: "IN")
                } catch (e: Exception) {
                    MovementType.IN
                },
                quantity = (data["quantity"] as? Number)?.toInt() ?: 0,
                reason = data["reason"] as? String ?: "",
                timestamp = (data["timestamp"] as? Number)?.toLong() ?: System.currentTimeMillis()
            )
        } catch (e: Exception) {
            null
        }
    }
}

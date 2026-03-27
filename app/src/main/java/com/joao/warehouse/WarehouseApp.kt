package com.joao.warehouse

import android.app.Application
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.joao.warehouse.data.repository.CategoryRepository
import com.joao.warehouse.data.repository.ProductRepository
import com.joao.warehouse.data.repository.StockMovementRepository
import com.joao.warehouse.data.sync.FirestoreSync
import com.joao.warehouse.data.sync.WarehousePreferences

class WarehouseApp : Application() {

    lateinit var warehousePreferences: WarehousePreferences
        private set

    private var _firestoreSync: FirestoreSync? = null
    private var _productRepository: ProductRepository? = null
    private var _categoryRepository: CategoryRepository? = null
    private var _stockMovementRepository: StockMovementRepository? = null

    val firestoreSync: FirestoreSync
        get() = _firestoreSync ?: throw IllegalStateException("FirestoreSync not initialized. Set warehouse ID first.")

    val productRepository: ProductRepository
        get() = _productRepository ?: throw IllegalStateException("Repositories not initialized. Set warehouse ID first.")

    val categoryRepository: CategoryRepository
        get() = _categoryRepository ?: throw IllegalStateException("Repositories not initialized. Set warehouse ID first.")

    val stockMovementRepository: StockMovementRepository
        get() = _stockMovementRepository ?: throw IllegalStateException("Repositories not initialized. Set warehouse ID first.")

    val isWarehouseConfigured: Boolean
        get() = _firestoreSync != null

    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
        warehousePreferences = WarehousePreferences(this)

        // Configure Firestore offline persistence
        val firestore = FirebaseFirestore.getInstance()
        val settings = FirebaseFirestoreSettings.Builder()
            .setPersistenceEnabled(true)
            .setCacheSizeBytes(FirebaseFirestoreSettings.CACHE_SIZE_UNLIMITED)
            .build()
        firestore.firestoreSettings = settings

        // If warehouse ID was previously saved, initialize repositories
        val savedWarehouseId = warehousePreferences.getWarehouseId()
        if (savedWarehouseId != null) {
            initializeRepositories(savedWarehouseId)
        }
    }

    fun initializeRepositories(warehouseId: String) {
        val firestore = FirebaseFirestore.getInstance()
        val sync = FirestoreSync(firestore, warehouseId)
        _firestoreSync = sync
        _productRepository = ProductRepository(sync)
        _categoryRepository = CategoryRepository(sync)
        _stockMovementRepository = StockMovementRepository(sync)
        warehousePreferences.setWarehouseId(warehouseId)
    }
}

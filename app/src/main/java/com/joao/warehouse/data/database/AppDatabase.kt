package com.joao.warehouse.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.joao.warehouse.data.dao.CategoryDao
import com.joao.warehouse.data.dao.ProductDao
import com.joao.warehouse.data.dao.StockMovementDao
import com.joao.warehouse.data.model.Category
import com.joao.warehouse.data.model.Product
import com.joao.warehouse.data.model.StockMovement

@Database(
    entities = [Product::class, Category::class, StockMovement::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun productDao(): ProductDao
    abstract fun categoryDao(): CategoryDao
    abstract fun stockMovementDao(): StockMovementDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "warehouse_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}

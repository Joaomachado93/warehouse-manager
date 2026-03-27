package com.joao.warehouse.data.sync

import android.content.Context
import android.content.SharedPreferences

class WarehousePreferences(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("warehouse_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_WAREHOUSE_ID = "warehouse_id"
    }

    fun getWarehouseId(): String? {
        return prefs.getString(KEY_WAREHOUSE_ID, null)
    }

    fun setWarehouseId(warehouseId: String) {
        prefs.edit().putString(KEY_WAREHOUSE_ID, warehouseId).apply()
    }

    fun clearWarehouseId() {
        prefs.edit().remove(KEY_WAREHOUSE_ID).apply()
    }
}

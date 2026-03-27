package com.joao.warehouse

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.joao.warehouse.ui.navigation.NavGraph
import com.joao.warehouse.ui.theme.WarehouseManagerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WarehouseManagerTheme {
                val navController = rememberNavController()
                NavGraph(navController = navController)
            }
        }
    }
}

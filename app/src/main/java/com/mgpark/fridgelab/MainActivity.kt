package com.mgpark.fridgelab

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.mgpark.fridgelab.navigation.FridgeNavHost
import com.mgpark.fridgelab.ui.theme.FridgeLabTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FridgeLabTheme {
                FridgeNavHost()
            }
        }
    }
}

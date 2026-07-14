package com.saico.mimercado

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.saico.mimercado.ui.screens.ProductListScreen
import com.saico.mimercado.ui.theme.MiMercadoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MiMercadoTheme {
                ProductListScreen()
            }
        }
    }
}

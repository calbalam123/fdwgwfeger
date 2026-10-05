package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.local.AppDatabase
import com.example.data.repository.BotRepository
import com.example.ui.BotViewModel
import com.example.ui.BotViewModelFactory
import com.example.ui.MainAppScreen
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val database = AppDatabase.getDatabase(applicationContext)
        val repository = BotRepository(database.botDao())
        val factory = BotViewModelFactory(repository)

        setContent {
            MyApplicationTheme {
                val viewModel: BotViewModel = viewModel(factory = factory)
                MainAppScreen(viewModel = viewModel)
            }
        }
    }
}

package com.example.shoppingcalculator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import com.example.shoppingcalculator.navigation.NavGraph
import com.example.shoppingcalculator.ui.theme.ShoppingCalculatorTheme
import com.example.shoppingcalculator.viewmodel.ShoppingViewModel


class MainActivity : ComponentActivity() {
    private lateinit var appViewModel: ShoppingViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        appViewModel = ViewModelProvider(this)[ShoppingViewModel::class.java]
        setContent {
            ShoppingCalculatorTheme(darkTheme = appViewModel.appSettings.value["Dark Mode"] as Boolean )
            {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    NavGraph(appViewModel, modifier=Modifier.padding(innerPadding))
                }
            }
        }
    }

    override fun onStop() {
        super.onStop()
        if (appViewModel.appSettings.value[ appViewModel.context.getString(R.string.save_file)] as Boolean && appViewModel.currentList.value.items.isNotEmpty()
            ) appViewModel.saveListToFile()
    }
}

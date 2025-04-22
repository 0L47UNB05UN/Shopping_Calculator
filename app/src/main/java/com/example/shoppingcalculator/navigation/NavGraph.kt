package com.example.shoppingcalculator.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.shoppingcalculator.screens.AboutScreen
import com.example.shoppingcalculator.screens.HomeScreen
import com.example.shoppingcalculator.screens.MainScreen
import com.example.shoppingcalculator.screens.SettingsScreen
import com.example.shoppingcalculator.viewmodel.ShoppingViewModel

@Composable
fun NavGraph(viewModel: ShoppingViewModel, modifier: Modifier = Modifier ) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            HomeScreen(
                viewModel = viewModel,
                onNavigate = { screen ->
                    navController.navigate(screen) {
                        popUpTo("home") { inclusive = false }
                    }
                },
                modifier = modifier
            )
        }
        composable("main") {
            MainScreen(
                viewModel = viewModel,
                onNavigate = { screen ->
                    navController.navigate(screen) {
                        popUpTo("home") { inclusive = false }
                    }
                },
                modifier = modifier
            )
        }
        composable("settings"){
            SettingsScreen(
                onBackPressed = {navController.popBackStack()},
                onNavigate = { screen->
                    navController.navigate(screen){
                        popUpTo("home"){inclusive=false}
                    }
                },
                viewModel = viewModel
            )
        }
        composable("about") {
            AboutScreen(
                onBackClicked = { navController.popBackStack() },
                onNavigate =  { screen->
                    navController.navigate(screen){
                        popUpTo("home"){inclusive=false}
                    }
                },
                viewModel = viewModel,
                modifier = modifier
            )
        }
    }
}
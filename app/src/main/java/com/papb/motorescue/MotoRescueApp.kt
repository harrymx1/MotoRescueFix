package com.papb.motorescue

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.papb.motorescue.ui.DriverFormScreen
import com.papb.motorescue.ui.DriverHomeScreen
import com.papb.motorescue.ui.MechanicDetailScreen
import com.papb.motorescue.ui.MechanicHomeScreen
import com.papb.motorescue.ui.MechanicViewModel
import com.papb.motorescue.ui.WelcomeScreen

@Composable
fun MotoRescueApp() {
    val navController = rememberNavController()

    val sharedMechanicViewModel: MechanicViewModel = viewModel()

    NavHost(navController = navController, startDestination = "welcome") {

        // Rute 1: Welcome
        composable("welcome") {
            WelcomeScreen(
                onLoginDriver = { navController.navigate("driver_home") },
                onLoginMechanic = { navController.navigate("mechanic_home") }
            )
        }

        // Rute 2: Driver Home
        composable("driver_home") {
            DriverHomeScreen(navController = navController)
        }

        // Rute 3: Driver Form
        composable("driver_form") {
            DriverFormScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // Rute 4: Mechanic Home (Sisi Montir)
        composable("mechanic_home") {
            MechanicHomeScreen(
                navController = navController,
                viewModel = sharedMechanicViewModel
            )
        }

        // Rute 5: Mechanic Detail (Sisi Montir)
        composable("mechanic_detail/{orderId}") { backStackEntry ->
            val orderId = backStackEntry.arguments?.getString("orderId") ?: ""
            MechanicDetailScreen(
                orderId = orderId,
                onNavigateBack = { navController.popBackStack() },
                viewModel = sharedMechanicViewModel
            )
        }
    }
}
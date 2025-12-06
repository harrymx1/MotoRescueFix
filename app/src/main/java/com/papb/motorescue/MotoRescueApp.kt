package com.papb.motorescue

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.papb.motorescue.ui.DriverFormScreen
import com.papb.motorescue.ui.DriverHomeScreen
import com.papb.motorescue.ui.MechanicHomeScreen
import com.papb.motorescue.ui.WelcomeScreen

@Composable
fun MotoRescueApp() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "welcome") {
        // Rute 1: Halaman Welcome
        composable("welcome") {
            WelcomeScreen(
                onLoginDriver = { navController.navigate("driver_home") },
                onLoginMechanic = { navController.navigate("mechanic_home") }
            )
        }

        // Rute 2: Halaman Driver
        composable("driver_home") {
            DriverHomeScreen(navController = navController)
        }

        // Rute 3: Halaman Montir
        composable("mechanic_home") {
            MechanicHomeScreen()
        }

        // Rute 4: Halaman Form Driver
        composable("driver_form") {
            DriverFormScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
package com.dresta0056.whowantcoffee.nav

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.dresta0056.whowantcoffee.ui.about.AboutScreen
import com.dresta0056.whowantcoffee.ui.cellar.CellarScreen
import com.dresta0056.whowantcoffee.ui.coffeelist.CoffeeListScreen
import com.dresta0056.whowantcoffee.ui.detail.CoffeeDetailScreen
import kotlinx.coroutines.CoroutineScope

@Composable
fun SetupNavGraph(
    navController: NavHostController = rememberNavController(),
    snackbarHostState: SnackbarHostState,
    snackbarScope: CoroutineScope
) {
    NavHost(
        navController = navController,
        startDestination = Screen.CoffeeList.route
    ) {
        composable(route = Screen.CoffeeList.route) {
            CoffeeListScreen(navController)
        }

        composable(route = Screen.CoffeeAdd.route) {
            CoffeeDetailScreen(
                navController = navController,
                snackbarHostState = snackbarHostState,
                snackbarScope = snackbarScope
            )
        }

        composable(
            route = Screen.CoffeeEdit.route,
            arguments = listOf(
                navArgument(KEY_ID_COFFEE) {
                    type = NavType.IntType
                }
            )
        ) { navBackStackEntry ->
            val id = navBackStackEntry.arguments?.getInt(KEY_ID_COFFEE)

            CoffeeDetailScreen(
                navController = navController,
                id = id,
                snackbarHostState = snackbarHostState,
                snackbarScope = snackbarScope
            )
        }

        composable(route = Screen.Cellar.route) {
            CellarScreen(
                navController = navController,
                snackbarHostState = snackbarHostState,
                snackbarScope = snackbarScope
            )
        }

        composable(route = Screen.About.route) {
            AboutScreen(navController)
        }
    }
}
package com.dresta0056.whowantcoffee.ui.detail

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController

@Composable
fun CoffeeDetailScreen(
    navController: NavHostController,
    id: Int? = null
) {
    if (id == null) {
        Text(text = "Add Coffee Screen")
    } else {
        Text(text = "Edit Coffee Screen, id = $id")
    }
}
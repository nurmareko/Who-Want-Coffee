package com.dresta0056.whowantcoffee.ui.cellar

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController

@Composable
fun CellarScreen(navController: NavHostController) {
    Button(
        onClick = {
            navController.popBackStack()
        }
    ) {
        Text(text = "Back")
    }
}
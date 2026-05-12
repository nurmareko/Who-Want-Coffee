package com.dresta0056.whowantcoffee.ui.coffeelist

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.dresta0056.whowantcoffee.nav.Screen

@Composable
fun CoffeeListScreen(navController: NavHostController) {
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(text = "Coffee List Screen")

        Button(
            onClick = {
                navController.navigate(Screen.CoffeeAdd.route)
            }
        ) {
            Text(text = "Go to Add Coffee")
        }

        Button(
            onClick = {
                navController.navigate(Screen.CoffeeEdit.withId(1))
            }
        ) {
            Text(text = "Go to Edit Coffee id 1")
        }

        Button(
            onClick = {
                navController.navigate(Screen.Cellar.route)
            }
        ) {
            Text(text = "Go to Cellar")
        }

        Button(
            onClick = {
                navController.navigate(Screen.About.route)
            }
        ) {
            Text(text = "Go to About")
        }
    }
}
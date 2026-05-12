package com.dresta0056.whowantcoffee.ui.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

@Composable
fun CoffeeDetailScreen(
    navController: NavHostController,
    id: Int? = null
) {
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (id == null) {
            Text(text = "Add Coffee Screen")
        } else {
            Text(text = "Edit Coffee Screen, id = $id")
        }

        Button(
            onClick = {
                navController.popBackStack()
            }
        ) {
            Text(text = "Back")
        }
    }
}
package com.dresta0056.whowantcoffee

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.dresta0056.whowantcoffee.ui.theme.WhoWantCoffeeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WhoWantCoffeeTheme {
                App()
            }
        }
    }
}

@Composable
fun App() {
    Text(text = "Who Want Coffee")
}
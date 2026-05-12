package com.dresta0056.whowantcoffee

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.dresta0056.whowantcoffee.nav.SetupNavGraph
import com.dresta0056.whowantcoffee.ui.theme.WhoWantCoffeeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WhoWantCoffeeTheme {
                SetupNavGraph()
            }
        }
    }
}
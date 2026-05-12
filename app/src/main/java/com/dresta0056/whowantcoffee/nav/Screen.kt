package com.dresta0056.whowantcoffee.nav

const val KEY_ID_COFFEE = "idCoffee"

sealed class Screen(val route: String) {
    data object CoffeeList : Screen("mainScreen")

    data object CoffeeAdd : Screen("detailScreen")

    data object CoffeeEdit : Screen("detailScreen/{$KEY_ID_COFFEE}") {
        fun withId(id: Int) = "detailScreen/$id"
    }

    data object Cellar : Screen("cellarScreen")

    data object About : Screen("aboutScreen")
}
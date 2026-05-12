package com.dresta0056.whowantcoffee

import android.app.Application
import com.dresta0056.whowantcoffee.data.CoffeeDatabase
import com.dresta0056.whowantcoffee.data.CoffeeRepository
import com.dresta0056.whowantcoffee.data.UserPreferencesRepository
import com.dresta0056.whowantcoffee.data.dataStore

class WhoWantCoffeeApplication : Application() {

    lateinit var repository: CoffeeRepository
        private set

    override fun onCreate() {
        super.onCreate()

        val database = CoffeeDatabase.getDatabase(this)
        val preferences = UserPreferencesRepository(dataStore)

        repository = CoffeeRepository(
            dao = database.coffeeDao(),
            preferences = preferences
        )
    }
}
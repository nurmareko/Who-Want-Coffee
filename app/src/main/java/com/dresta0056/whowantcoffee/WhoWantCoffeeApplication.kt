package com.dresta0056.whowantcoffee

import android.app.Application
import com.dresta0056.whowantcoffee.data.Coffee
import com.dresta0056.whowantcoffee.data.CoffeeDatabase
import com.dresta0056.whowantcoffee.data.CoffeeRepository
import com.dresta0056.whowantcoffee.data.UserPreferencesRepository
import com.dresta0056.whowantcoffee.data.dataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

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

        seedDatabase(database)
    }

    private fun seedDatabase(database: CoffeeDatabase) {
        CoroutineScope(Dispatchers.IO).launch {
            val dao = database.coffeeDao()

            if (dao.countAllCoffees() == 0) {
                val now = System.currentTimeMillis()
                val day = 1000L * 60 * 60 * 24

                dao.insert(
                    Coffee(
                        name = "Ethiopia Yirgacheffe",
                        process = "Washed",
                        rating = 5,
                        notes = "Bright floral notes with jasmine, citrus, and bergamot.",
                        dateAdded = now - (2 * day),
                        lastUpdated = now - (2 * day),
                        archivedAt = null
                    )
                )

                dao.insert(
                    Coffee(
                        name = "Colombia Supremo",
                        process = "Honey",
                        rating = 4,
                        notes = "Sweet caramel body with soft acidity.",
                        dateAdded = now - (5 * day),
                        lastUpdated = now - (5 * day),
                        archivedAt = null
                    )
                )

                dao.insert(
                    Coffee(
                        name = "Indonesia Gayo",
                        process = "Natural",
                        rating = 4,
                        notes = "Earthy, heavy body, dark chocolate finish.",
                        dateAdded = now - (10 * day),
                        lastUpdated = now - (10 * day),
                        archivedAt = null
                    )
                )

                dao.insert(
                    Coffee(
                        name = "Brazil Cerrado",
                        process = "Natural",
                        rating = 3,
                        notes = "Nutty and mellow. Good daily cup, not too complex.",
                        dateAdded = now - (16 * day),
                        lastUpdated = now - (16 * day),
                        archivedAt = null
                    )
                )

                dao.insert(
                    Coffee(
                        name = "Guatemala Antigua",
                        process = "Washed",
                        rating = 4,
                        notes = "Chocolate, spice, and orange peel.",
                        dateAdded = now - (25 * day),
                        lastUpdated = now - (25 * day),
                        archivedAt = now - (3 * day)
                    )
                )

                dao.insert(
                    Coffee(
                        name = "Kenya AA",
                        process = "Washed",
                        rating = 5,
                        notes = "Juicy blackcurrant acidity. Very clean cup.",
                        dateAdded = now - (30 * day),
                        lastUpdated = now - (30 * day),
                        archivedAt = now - (7 * day)
                    )
                )
            }
        }
    }
}
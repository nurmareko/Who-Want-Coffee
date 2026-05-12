package com.dresta0056.whowantcoffee.data

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest

class CoffeeRepository(
    private val dao: CoffeeDao,
    private val preferences: UserPreferencesRepository
) {
    val sortOrder: Flow<String> = preferences.sortOrder
    val viewMode: Flow<String> = preferences.viewMode

    @OptIn(ExperimentalCoroutinesApi::class)
    fun activeCoffees(): Flow<List<Coffee>> {
        return sortOrder.flatMapLatest { order ->
            when (order) {
                "rating" -> dao.getActiveByRating()
                else -> dao.getActiveByRecent()
            }
        }
    }

    fun archivedCoffees(): Flow<List<Coffee>> {
        return dao.getArchived()
    }

    fun getById(id: Int): Flow<Coffee?> {
        return dao.getById(id)
    }

    fun countArchived(): Flow<Int> {
        return dao.countArchived()
    }

    suspend fun addCoffee(
        name: String,
        process: String,
        rating: Int,
        notes: String?
    ) {
        val now = System.currentTimeMillis()

        dao.insert(
            Coffee(
                name = name,
                process = process,
                rating = rating,
                notes = notes,
                dateAdded = now,
                lastUpdated = now,
                archivedAt = null
            )
        )
    }

    suspend fun updateCoffee(coffee: Coffee) {
        dao.update(
            coffee.copy(
                lastUpdated = System.currentTimeMillis()
            )
        )
    }

    suspend fun archive(id: Int) {
        dao.archive(
            id = id,
            timestamp = System.currentTimeMillis()
        )
    }

    suspend fun restore(id: Int) {
        dao.restore(id)
    }

    suspend fun hardDelete(coffee: Coffee) {
        dao.hardDelete(coffee)
    }

    suspend fun toggleSort(current: String) {
        val newValue = if (current == "recent") "rating" else "recent"
        preferences.setSortOrder(newValue)
    }

    suspend fun toggleView(current: String) {
        val newValue = if (current == "list") "grid" else "list"
        preferences.setViewMode(newValue)
    }
}
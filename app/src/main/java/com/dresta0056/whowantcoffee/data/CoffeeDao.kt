package com.dresta0056.whowantcoffee.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface CoffeeDao {

    @Query("SELECT * FROM coffees WHERE archivedAt IS NULL ORDER BY dateAdded DESC")
    fun getActiveByRecent(): Flow<List<Coffee>>

    @Query("SELECT * FROM coffees WHERE archivedAt IS NULL ORDER BY rating DESC, dateAdded DESC")
    fun getActiveByRating(): Flow<List<Coffee>>

    @Query("SELECT * FROM coffees WHERE archivedAt IS NOT NULL ORDER BY archivedAt DESC")
    fun getArchived(): Flow<List<Coffee>>

    @Query("SELECT * FROM coffees WHERE id = :id")
    fun getById(id: Int): Flow<Coffee?>

    @Insert
    suspend fun insert(coffee: Coffee): Long

    @Update
    suspend fun update(coffee: Coffee)

    @Query("UPDATE coffees SET archivedAt = :timestamp WHERE id = :id")
    suspend fun archive(id: Int, timestamp: Long)

    @Query("UPDATE coffees SET archivedAt = NULL WHERE id = :id")
    suspend fun restore(id: Int)

    @Delete
    suspend fun hardDelete(coffee: Coffee)

    @Query("SELECT COUNT(*) FROM coffees")
    suspend fun countAllCoffees(): Int

    @Query("SELECT COUNT(*) FROM coffees WHERE archivedAt IS NOT NULL")
    fun countArchived(): Flow<Int>
}
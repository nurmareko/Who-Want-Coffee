package com.dresta0056.whowantcoffee.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "coffees")
data class Coffee(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val name: String,
    val process: String,
    val rating: Int,
    val notes: String? = null,

    val dateAdded: Long,
    val lastUpdated: Long,

    // null = active list
    // not null = archived in Cellar
    val archivedAt: Long? = null
)
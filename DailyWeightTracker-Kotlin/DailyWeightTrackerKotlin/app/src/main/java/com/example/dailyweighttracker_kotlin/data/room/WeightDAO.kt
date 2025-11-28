package com.example.dailyweighttracker_kotlin.data.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow


    /**
     * Data Access Object for the 'weights' table.
     * Defines the methods for interacting with the database.
     */

    // Data Access Object for weight
    @Dao
    interface WeightDao {

        // Insert weight into database
        @Insert(onConflict = OnConflictStrategy.IGNORE)
        suspend fun insert(weight: Weight)

        // Update weight into database
        @Update
        suspend fun update(weight: Weight)

        // Delete weight from database
        @Delete
        suspend fun delete(weight: Weight)

        // Get weight by user id and order by date
        @Query("SELECT * FROM weights WHERE userId = :userId ORDER BY date DESC")
        fun getAllWeights(userId: Int): Flow<List<Weight>>
    }
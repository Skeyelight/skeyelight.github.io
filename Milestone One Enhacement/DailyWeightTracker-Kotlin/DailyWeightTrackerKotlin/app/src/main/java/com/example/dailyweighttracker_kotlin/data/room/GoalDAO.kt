package com.example.dailyweighttracker_kotlin.data.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

// Data Access Object for Goal
@Dao
interface GoalDao {

    // Retrieves the goal for a specific user
    @Query("SELECT * FROM goals WHERE userId = :userId")
    fun getGoalForUserFlow(userId: Int): Flow<Goal?>

    // Inserts new goal into the database
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun setGoal(goal: Goal)
}

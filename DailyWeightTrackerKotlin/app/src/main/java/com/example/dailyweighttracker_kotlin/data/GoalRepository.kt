package com.example.dailyweighttracker_kotlin.data

import com.example.dailyweighttracker_kotlin.data.room.Goal
import com.example.dailyweighttracker_kotlin.data.room.GoalDao
import kotlinx.coroutines.flow.Flow


 // Repository class for managing goal data.
class GoalRepository(private val goalDao: GoalDao) {

     // Retrieves the current goal for a specific user
    fun getGoalForUserFlow(userId: Int): Flow<Goal?> {
        return goalDao.getGoalForUserFlow(userId)
    }

    // Sets a user's goal.
    suspend fun setGoal(goal: Goal) {
        goalDao.setGoal(goal)
    }
}


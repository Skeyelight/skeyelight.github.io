package com.example.dailyweighttracker_kotlin.data

import com.example.dailyweighttracker_kotlin.data.room.Weight
import com.example.dailyweighttracker_kotlin.data.room.WeightDao
import kotlinx.coroutines.flow.Flow


// Manages weights in database
class WeightRepository(private val weightDao: WeightDao) {

    // Returns weights
    fun getAllWeightsStream(userId: Int): Flow<List<Weight>> = weightDao.getAllWeights(userId)

    // Inserts weight into database
    suspend fun insertWeight(weight: Weight) = weightDao.insert(weight)


    // Delete a weight from the database
    suspend fun deleteWeight(weight: Weight) = weightDao.delete(weight)

    // Updates a weight in the database
    suspend fun updateWeight(weight: Weight) = weightDao.update(weight)
}

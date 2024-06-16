package com.example.gebung.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface PredictionDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(prediction: Prediction)

    @Query("SELECT * FROM prediction_table ORDER BY month ASC")
    fun getAllPredictions(): LiveData<List<Prediction>>

    @Query("SELECT COUNT(*) FROM prediction_table WHERE month = :month")
    suspend fun countMonthTotal(month: String): Int
}
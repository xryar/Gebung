package com.example.gebung.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "prediction_table")
data class Prediction(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int = 0,

    @ColumnInfo(name = "month")
    val month: String,

    @ColumnInfo(name = "predicted")
    val predicted: Float?
)
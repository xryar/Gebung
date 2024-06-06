package com.example.gebung.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transaction_table")
data class Transaction(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Int = 0,

    @ColumnInfo(name = "title")
    var title: String,

    @ColumnInfo(name = "category")
    var category: String,

    @ColumnInfo(name = "date")
    var date: String,

    @ColumnInfo(name = "nominal")
    var nominal: Int,

    @ColumnInfo(name = "type")
    var type: String
)

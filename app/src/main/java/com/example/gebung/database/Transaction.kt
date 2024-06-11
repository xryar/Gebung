package com.example.gebung.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transaction_table")
data class Transaction(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "Id")
    var id: Int = 0,

    @ColumnInfo(name = "Description")
    var description: String,

    @ColumnInfo(name = "Category")
    var category: String,

    @ColumnInfo(name = "Date")
    var date: String,

    @ColumnInfo(name = "Amount")
    var amount: Int,

    @ColumnInfo(name = "Type")
    var type: String
)

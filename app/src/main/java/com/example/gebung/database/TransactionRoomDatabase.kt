package com.example.gebung.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Transaction::class], version = 1, exportSchema = false)
abstract class TransactionRoomDatabase : RoomDatabase() {

    abstract fun transactionDao(): TransactionDao

    companion object {
        @Volatile
        private var INSTANCE: TransactionRoomDatabase? = null

        @JvmStatic
        fun getDatabase(context: Context): TransactionRoomDatabase {
            if (INSTANCE == null) {
                synchronized(TransactionRoomDatabase::class.java) {
                    INSTANCE = Room.databaseBuilder(
                        context.applicationContext,
                        TransactionRoomDatabase::class.java, "food_database")
                        .build()
                }
            }
            return INSTANCE as TransactionRoomDatabase
        }
    }

}
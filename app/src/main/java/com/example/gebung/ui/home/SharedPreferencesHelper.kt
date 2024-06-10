package com.example.gebung.ui.home

import android.content.Context

object SharedPreferencesHelper {

    private const val PREFS_NAME = "transaction_prefs"
    private const val KEY_DATES = "transaction_dates"

    fun addTransactionDate(context: Context, date: String){
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val dates = prefs.getStringSet(KEY_DATES, mutableSetOf()) ?: mutableSetOf()
        if (dates.add(date)){
            prefs.edit().putStringSet(KEY_DATES, dates).apply()
        }
    }

    fun getTransactionDates(context: Context): Set<String>{
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getStringSet(KEY_DATES, mutableSetOf()) ?: mutableSetOf()
    }

    fun getTransactionDatesCount(context: Context): Int{
        return getTransactionDates(context).size
    }

    fun resetTransactionDates(context: Context){
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().remove(KEY_DATES).apply()
    }
}
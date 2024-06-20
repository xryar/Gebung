package com.example.gebung.ui.home

import android.content.Context
import android.util.Log
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

object SharedPreferencesHelper {

    private const val PREFS_NAME = "transaction_prefs"
    private const val KEY_DATES = "transaction_dates"

    fun addTransactionDate(context: Context, date: String) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val dates = prefs.getStringSet(KEY_DATES, mutableSetOf()) ?: mutableSetOf()

        if (!dates.contains(date)) {
            dates.add(date)
            prefs.edit().putStringSet(KEY_DATES, dates).apply()
            Log.d("SharedPreferencesHelper", "Added date: $date")
        }
        Log.d("SharedPreferencesHelper", "Current week dates: $dates")
    }

    private fun getTransactionDates(context: Context): Set<String> {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getStringSet(KEY_DATES, mutableSetOf()) ?: mutableSetOf()
    }

    fun resetTransactionDates(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().remove(KEY_DATES).apply() // Change apply() to commit()
        Log.d("SharedPreferencesHelper", "Transaction dates reset")
    }

    fun getTransactionDatesInNext7Days(context: Context): Set<String> {
        val dates = getTransactionDates(context)
        val next7DaysDates = dates.filter { isInNext7Days(it) }.toSet()
        Log.d("SharedPreferencesHelper", "Next 7 days dates: $next7DaysDates")
        if (next7DaysDates.size != dates.size) {
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            prefs.edit().putStringSet(KEY_DATES, next7DaysDates).apply()
        }
        return next7DaysDates
    }

    private fun isInNext7Days(date: String): Boolean {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val inputDate = dateFormat.parse(date) ?: return false

        val calendar = Calendar.getInstance()
        calendar.time = inputDate
        val today = Calendar.getInstance().time

        val diff = (calendar.time.time - today.time) / (1000 * 60 * 60 * 24)
        return diff in 0..6
    }
}
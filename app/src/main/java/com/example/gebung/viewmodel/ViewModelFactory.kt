package com.example.gebung.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.gebung.ui.analysis.AnalysisViewModel

class ViewModelFactory(private val application: Application): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TransactionViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TransactionViewModel(application) as T
        }else if (modelClass.isAssignableFrom(AnalysisViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AnalysisViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
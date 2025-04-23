package com.openclassrooms.rebonnte.ui.aisle.add

import androidx.lifecycle.ViewModel
import com.openclassrooms.rebonnte.repository.StockRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AddAisleViewModel @Inject constructor(val stockRepository: StockRepository): ViewModel() {
    fun addAisle(name: String) {
        stockRepository.addAisle(name)
    }
}
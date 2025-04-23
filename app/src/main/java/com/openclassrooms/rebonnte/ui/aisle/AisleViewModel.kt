package com.openclassrooms.rebonnte.ui.aisle

import androidx.lifecycle.ViewModel
import com.openclassrooms.rebonnte.repository.StockRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AisleViewModel @Inject constructor(stockRepository: StockRepository) :
    ViewModel() {
    var aisles = stockRepository.aisles


    /*fun addRandomAisle() {
        val currentAisles: MutableList<Aisle> = ArrayList(aisles.value)
        currentAisles.add(Aisle("Aisle " + (currentAisles.size + 1)))
        _aisles.value = currentAisles
    }*/
}


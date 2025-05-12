package com.openclassrooms.rebonnte.ui.aisle.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openclassrooms.rebonnte.repository.StockRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddAisleViewModel @Inject constructor(val stockRepository: StockRepository) : ViewModel() {
    fun addAisle(name: String) {
        viewModelScope.launch(Dispatchers.IO) { //Add Dispatchers.Io so it don't run on main thread (by default viewModelScope scope run on main)
            stockRepository.addAisle(name)
        }
    }
}
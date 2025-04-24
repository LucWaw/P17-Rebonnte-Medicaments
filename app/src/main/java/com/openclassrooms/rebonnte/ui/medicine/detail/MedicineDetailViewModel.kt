package com.openclassrooms.rebonnte.ui.medicine.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openclassrooms.rebonnte.domain.History
import com.openclassrooms.rebonnte.repository.OrderFilter
import com.openclassrooms.rebonnte.repository.StockRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject



@HiltViewModel
class MedicineDetailViewModel @Inject constructor(private val stockRepository: StockRepository) :
    ViewModel() {
    val medicines = stockRepository.medicines(OrderFilter.NONE)
    val aisles = stockRepository.aisles


    /*
    La mise a jour du stock permet de mettre a jour le flow et donc l'historique en temps réel
     */
    fun modifyMedicine(medicineId: String, name: String, aisle: String, stock: Int) {
        stockRepository.modifyMedicine(medicineId, name, aisle, stock)
    }

    fun addToHistory(medicineId: String, history: History) {
        viewModelScope.launch {
            stockRepository.addHistory(medicineId, history)
        }
    }
}
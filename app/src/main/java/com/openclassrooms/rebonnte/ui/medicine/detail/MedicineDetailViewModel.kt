package com.openclassrooms.rebonnte.ui.medicine.detail

import androidx.lifecycle.ViewModel
import com.google.android.gms.tasks.Task
import com.openclassrooms.rebonnte.domain.History
import com.openclassrooms.rebonnte.repository.OrderFilter
import com.openclassrooms.rebonnte.repository.StockRepository
import dagger.hilt.android.lifecycle.HiltViewModel
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
        stockRepository.addHistory(medicineId, history)

    }

    fun deleteMedicine(medicineId: String): Task<Task<Void?>?> {
        return stockRepository.deleteMedicine(medicineId)
    }
}
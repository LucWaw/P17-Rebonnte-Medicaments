package com.openclassrooms.rebonnte.ui.medicine.add

import androidx.lifecycle.ViewModel
import com.openclassrooms.rebonnte.domain.Medicine
import com.openclassrooms.rebonnte.repository.StockRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AddMedicineViewModel @Inject constructor(private val stockRepository: StockRepository) :
    ViewModel() {
        val aisles = stockRepository.aisles

    fun addMedicine(medicine : Medicine){
        stockRepository.addMedicine(medicine)
    }

}

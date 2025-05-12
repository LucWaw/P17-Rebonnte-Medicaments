package com.openclassrooms.rebonnte.ui.aisle.detail

import androidx.lifecycle.ViewModel
import com.google.android.gms.tasks.Task
import com.openclassrooms.rebonnte.repository.StockRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Inject


@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class AisleDetailViewModel @Inject constructor(private val stockRepository: StockRepository) :
    ViewModel() {
    val aisles = stockRepository.aisles
    val medicines = stockRepository.medicines


    fun deleteWithoutMedicine(aisleId: String): Task<Void?> {
        return stockRepository.deleteAisleWithoutMedicine(aisleId)
    }

    fun deleteAisleAndAllMedicine(aisleId: String, aisleName: String): Task<Task<Void?>?> {
        return stockRepository.deleteAisleAndAllMedicine(aisleId, aisleName)
    }

    fun deleteByMovingAllMedicine(
        aisleId: String,
        targetAisleName: String,
        aisleName: String
    ): Task<Task<Void?>?> {
        return stockRepository.deleteByMovingAllMedicine(aisleId, targetAisleName, aisleName)
    }

}
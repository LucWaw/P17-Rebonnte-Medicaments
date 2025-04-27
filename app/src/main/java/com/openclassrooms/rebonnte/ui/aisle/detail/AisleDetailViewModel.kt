package com.openclassrooms.rebonnte.ui.aisle.detail

import androidx.lifecycle.ViewModel
import com.openclassrooms.rebonnte.domain.Medicine
import com.openclassrooms.rebonnte.repository.OrderFilter
import com.openclassrooms.rebonnte.repository.StockRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject


@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class AisleDetailViewModel @Inject constructor(private val stockRepository: StockRepository) :
    ViewModel() {
    private val _currentFilter = MutableStateFlow(Pair(OrderFilter.NONE, ""))
    var aisles = stockRepository.aisles


    fun getMedicines(): Flow<List<Medicine>> {
        return _currentFilter.flatMapLatest { (filter, filterString) ->
            stockRepository.medicines(filter, filterString)
        }
    }

    fun deleteWithoutMedicine(aisleName: String) {
        stockRepository.deleteAisleWithoutMedicine(aisleName)
    }

    fun deleteAisleAndAllMedicine(aisleName: String) {
        stockRepository.deleteAisleAndAllMedicine(aisleName)
    }

    fun deleteByMovingAllMedicine(aisleName: String, targetAisleName: String) {
        stockRepository.deleteByMovingAllMedicine(aisleName, targetAisleName)
    }

}
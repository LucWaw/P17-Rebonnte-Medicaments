package com.openclassrooms.rebonnte.ui.medicine.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.google.android.gms.tasks.Task
import com.openclassrooms.rebonnte.domain.History
import com.openclassrooms.rebonnte.repository.OrderFilter
import com.openclassrooms.rebonnte.repository.StockRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class MedicineDetailViewModel @Inject constructor(private val stockRepository: StockRepository) :
    ViewModel() {

    val medicines = stockRepository.medicines(OrderFilter.NONE, "")
    val aisles = stockRepository.aisles()

    private val _history = MutableStateFlow<PagingData<History>>(PagingData.empty())
    val history = _history.asStateFlow()

    fun loadHistory(string: String) {
        Pager(
            config = PagingConfig(pageSize = 20, prefetchDistance = 5),
            pagingSourceFactory = { stockRepository.historyPager(string) }
        ).flow
            .cachedIn(viewModelScope)
            .onEach { _history.value = it }
            .launchIn(viewModelScope)
    }


    /*
    La mise a jour du stock permet de mettre a jour le flow et donc l'historique en temps réel
     */
    fun modifyMedicine(medicineId: String, name: String, aisle: String, stock: Int) {
        viewModelScope.launch {
            stockRepository.modifyMedicine(medicineId, name, aisle, stock)
        }
    }

    fun addToHistory(medicineId: String, history: History) {
        viewModelScope.launch {
            stockRepository.addHistory(medicineId, history)
        }
    }

    fun deleteMedicine(medicineId: String): Task<Task<Void?>?> {
        return stockRepository.deleteMedicine(medicineId)
    }
}
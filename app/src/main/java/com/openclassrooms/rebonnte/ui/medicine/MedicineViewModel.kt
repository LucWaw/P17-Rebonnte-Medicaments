package com.openclassrooms.rebonnte.ui.medicine

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openclassrooms.rebonnte.domain.Medicine
import com.openclassrooms.rebonnte.repository.OrderFilter
import com.openclassrooms.rebonnte.repository.StockRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.collections.List

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class MedicineViewModel @Inject constructor(private val stockRepository: StockRepository) :
    ViewModel() {
    private val _currentFilter = MutableStateFlow(Pair(OrderFilter.NONE, ""))
    private val _medicines = MutableStateFlow<List<Medicine>>(emptyList())
    val medicines: StateFlow<List<Medicine>> = _medicines

    init {
        viewModelScope.launch {
            _currentFilter
                .flatMapLatest { (filter, filterString) ->
                    stockRepository.medicines(filter, filterString)
                }
                .collect { list ->
                    _medicines.value = list
                }
        }
    }


    fun updateFilterAndSort(filter: OrderFilter, filterString: String = "") {
        if (filter == OrderFilter.FILTER_BY_NAME && filterString == ""){
            _currentFilter.value = Pair(OrderFilter.NONE, "")
            return
        }
        _currentFilter.value = Pair(filter, filterString)
    }
}


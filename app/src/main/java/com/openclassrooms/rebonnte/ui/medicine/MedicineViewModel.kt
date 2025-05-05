package com.openclassrooms.rebonnte.ui.medicine

import androidx.lifecycle.ViewModel
import com.openclassrooms.rebonnte.domain.Medicine
import com.openclassrooms.rebonnte.domain.Result
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
class MedicineViewModel @Inject constructor(private val stockRepository: StockRepository) :
    ViewModel() {
    private val _currentFilter = MutableStateFlow(Pair(OrderFilter.NONE, ""))

    fun getMedicines(): Flow<Result<List<Medicine>>> {
        return _currentFilter.flatMapLatest { (filter, filterString) ->
            stockRepository.medicines(filter, filterString)
        }
    }

    fun updateFilterAndSort(filter: OrderFilter, filterString: String = "") {
        if (filter == OrderFilter.FILTER_BY_NAME && filterString == "") {
            _currentFilter.value = Pair(OrderFilter.NONE, "")
            return
        }
        _currentFilter.value = Pair(filter, filterString)
    }

}



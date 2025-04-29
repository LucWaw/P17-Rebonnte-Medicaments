package com.openclassrooms.rebonnte.ui.medicine

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.openclassrooms.rebonnte.repository.OrderFilter
import com.openclassrooms.rebonnte.repository.StockRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class MedicineViewModel @Inject constructor(private val stockRepository: StockRepository) :
    ViewModel() {
    private val _currentFilter = MutableStateFlow(Pair(OrderFilter.NONE, ""))
    val currentFilter = _currentFilter.asStateFlow()

    val medicinePagingFlow = _currentFilter
        .flatMapLatest { (orderBy, filterString) ->
            Pager(
                config = PagingConfig(pageSize = 20, prefetchDistance = 5),
                pagingSourceFactory = {
                    stockRepository.medicinesPager(
                        orderBy,
                        filterString
                    )
                }
            ).flow
        }
        .cachedIn(viewModelScope)


    fun updateFilterAndSort(filter: OrderFilter, filterString: String = "") {
        if (filter == OrderFilter.FILTER_BY_NAME && filterString == "") {
            _currentFilter.value = Pair(OrderFilter.NONE, "")
            return
        }
        _currentFilter.value = Pair(filter, filterString)
    }

}


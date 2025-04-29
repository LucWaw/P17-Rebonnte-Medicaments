package com.openclassrooms.rebonnte.ui.aisle

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.openclassrooms.rebonnte.repository.StockRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AisleViewModel @Inject constructor(stockRepository: StockRepository) : ViewModel() {
    var aisles = Pager(
        config = PagingConfig(pageSize = 20, prefetchDistance = 5), pagingSourceFactory = {
            stockRepository.aislesPager()
        }).flow.cachedIn(viewModelScope)

    /*fun addRandomAisle() {
        val currentAisles: MutableList<Aisle> = ArrayList(aisles.value)
        currentAisles.add(Aisle("Aisle " + (currentAisles.size + 1)))
        _aisles.value = currentAisles
    }*/
}


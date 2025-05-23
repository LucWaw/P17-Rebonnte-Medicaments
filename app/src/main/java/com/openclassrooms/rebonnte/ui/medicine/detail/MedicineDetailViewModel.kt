package com.openclassrooms.rebonnte.ui.medicine.detail

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentReference
import com.openclassrooms.rebonnte.domain.History
import com.openclassrooms.rebonnte.domain.Medicine
import com.openclassrooms.rebonnte.repository.StockRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject


@HiltViewModel
class MedicineDetailViewModel @Inject constructor(private val stockRepository: StockRepository) :
    ViewModel() {

    private val _medicine = MutableStateFlow(Medicine())
    val medicine = _medicine.asStateFlow()

    val aisles = stockRepository.aisles

    private val _history = MutableStateFlow<PagingData<History>>(PagingData.empty())
    val history: StateFlow<PagingData<History>> = _history

    private var currentJob: Job? = null
    private var reloadJob: Job? = null

    /**
     * Loads the history for a given medicine.
     *
     * This function sets up a snapshot listener for the medicine's stock,
     * starts a pager to display the history, and sets up a listener to reload
     * the pager when the stock data changes.
     *
     * @param medicineId The ID of the medicine to load the history for.
     */
    fun loadHistory(medicineId: String) {
        stockRepository.setupSnapshotListener(medicineId)

        startPager(medicineId)

        reloadJob?.cancel()
        reloadJob = stockRepository.shouldReload
            .onEach { startPager(medicineId) }
            .launchIn(viewModelScope)
    }

    private fun startPager(medicineId: String) {
        currentJob?.cancel()
        currentJob = stockRepository.pagerFlow(medicineId)
            .cachedIn(viewModelScope)
            .onEach { _history.value = it }
            .launchIn(viewModelScope)
    }

    override fun onCleared() {
        super.onCleared()
        stockRepository.snapshotListenerRegistration?.remove()
        currentJob?.cancel()
        reloadJob?.cancel()
    }


    /**
     * Loads a specific medicine from the repository based on its ID.
     *
     * This function asynchronously retrieves medicine data from the `stockRepository`.
     * Upon successful retrieval, the `_medicine` StateFlow is updated with the fetched medicine.
     * If an error occurs during loading, a log message is recorded.
     *
     * @param id The unique identifier of the medicine to load.
     * @return A [Task] that will eventually contain the [Medicine] object if found, or null.
     *         The task also handles success and failure listeners internally.
     */
    fun loadMedicine(id: String): Task<Medicine?> {
        return stockRepository.getMedicine(id).addOnSuccessListener { medicine ->
            _medicine.value = medicine
        }.addOnFailureListener {
            Log.d("MedicineDetailViewModel", "Error loading medicine $it")
        }
    }


    /*
    Update of the stock allows to update the flow and therefore the history in real time
     */
    fun modifyMedicine(medicineId: String, name: String, aisle: String, stock: Int): Task<Void?> {
        return stockRepository.modifyMedicine(medicineId, name, aisle, stock)

    }

    fun addToHistory(medicineId: String, history: History): Task<DocumentReference?> {
        return stockRepository.addHistory(medicineId, history)

    }

    fun deleteMedicine(medicineId: String): Task<Task<Void?>?> {
        return stockRepository.deleteMedicine(medicineId)
    }
}
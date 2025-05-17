package com.openclassrooms.rebonnte.ui.medicine.detail

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.openclassrooms.rebonnte.domain.History
import com.openclassrooms.rebonnte.domain.Medicine
import com.openclassrooms.rebonnte.repository.StockRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class MedicineDetailViewModel @Inject constructor(private val stockRepository: StockRepository) :
    ViewModel() {

    private val _medicine = MutableStateFlow(Medicine())
    val medicine = _medicine.asStateFlow()

    val aisles = stockRepository.aisles

    private val _history = MutableStateFlow<PagingData<History>>(PagingData.empty())
    val history: StateFlow<PagingData<History>> = _history

    private var snapshotListenerRegistration: ListenerRegistration? = null
    private var currentJob: Job? = null
    private var isFirstSnapshot = true

    fun loadHistory(medicineId: String) {
        startPager(medicineId)
        setupSnapshotListener(medicineId)
    }

    private fun startPager(medicineId: String) {
        // Annule le job précédent (sinon multiples collecteurs en vie)
        currentJob?.cancel()
        currentJob = Pager(
            config = PagingConfig(pageSize = 20, prefetchDistance = 5),
            pagingSourceFactory = { stockRepository.historyPager(medicineId) }
        ).flow
            .cachedIn(viewModelScope)
            .onEach { _history.value = it }
            .launchIn(viewModelScope)
    }

    private fun setupSnapshotListener(id: String) {
        // éviter d'attacher plusieurs listeners
        snapshotListenerRegistration?.remove()
        isFirstSnapshot = true

        val collectionRef = FirebaseFirestore.getInstance()
            .collection("medicines")
            .document(id)
            .collection("history")

        snapshotListenerRegistration = collectionRef.addSnapshotListener { snapshot, error ->
            if (error != null || snapshot == null) return@addSnapshotListener

            if (isFirstSnapshot) {
                isFirstSnapshot = false
                return@addSnapshotListener
            }

            val isFromServer = !snapshot.metadata.isFromCache
            val hasRealAdditions = snapshot.documentChanges.any {
                it.type == DocumentChange.Type.ADDED
            }

            if (isFromServer && hasRealAdditions) {
                startPager(id) // recharge les données
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        snapshotListenerRegistration?.remove()
        currentJob?.cancel()
    }


    fun loadMedicine(id: String): Task<Medicine?> {
        return stockRepository.getMedicine(id).addOnSuccessListener { medicine ->
            _medicine.value = medicine
        }.addOnFailureListener {
            Log.d("MedicineDetailViewModel", "Error loading medicine $it")
        }
    }


    /*
    La mise a jour du stock permet de mettre a jour le flow et donc l'historique en temps réel
     */
    fun modifyMedicine(medicineId: String, name: String, aisle: String, stock: Int) {
        viewModelScope.launch(Dispatchers.IO) { //Add Dispatchers.Io so it don't run on main thread (by default viewModelScope scope run on main)
            stockRepository.modifyMedicine(medicineId, name, aisle, stock)
        }
    }

    fun addToHistory(medicineId: String, history: History) {
        viewModelScope.launch(Dispatchers.IO) { //Add Dispatchers.Io so it don't run on main thread (by default viewModelScope scope run on main)
            stockRepository.addHistory(medicineId, history)
        }
    }

    fun deleteMedicine(medicineId: String): Task<Task<Void?>?> {
        return stockRepository.deleteMedicine(medicineId)
    }
}
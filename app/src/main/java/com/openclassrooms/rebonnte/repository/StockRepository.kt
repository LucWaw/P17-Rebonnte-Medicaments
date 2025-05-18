package com.openclassrooms.rebonnte.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.openclassrooms.rebonnte.domain.Aisle
import com.openclassrooms.rebonnte.domain.History
import com.openclassrooms.rebonnte.domain.Medicine
import com.openclassrooms.rebonnte.domain.Result
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.retry
import kotlinx.coroutines.flow.stateIn
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StockRepository @Inject constructor(
    private val firebaseApi: FirebaseApi,
    private val internetContext: InternetContext
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private fun aisles(): Flow<Result<List<Aisle>>> = flow {
        emit(Result.Loading)

        if (!internetContext.isInternetAvailable()) {
            emit(Result.Error)
            throw NoInternetException()
        }

        firebaseApi.getAllAisles().collect { list ->
            emit(Result.Success(list))
        }

    }.retry { cause ->
        if (cause is NoInternetException) {
            delay(3000) // attendre un peu avant de réessayer
            true
        } else {
            false
        }
    }.flowOn(Dispatchers.IO)

    val aisles = aisles().stateIn(  //Ne pas appeler aisles dans chasue repository
        scope = scope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = Result.Loading
    )


    fun cancel() {
        scope.cancel()
    }

    class NoInternetException : IOException("No Internet Connection")

    fun medicines(orderFilter: OrderFilter, filter: String = ""): Flow<Result<List<Medicine>>> =
        flow {
            emit(Result.Loading)

            if (!internetContext.isInternetAvailable()) {
                emit(Result.Error)           // Affiche l’erreur
                throw NoInternetException()  // Déclenche retry plus tard
            }

            firebaseApi.getAllMedicines(orderFilter, filter).collect {
                emit(Result.Success(it))
            }

        }.retry { cause ->
            if (cause is NoInternetException) {
                delay(3000) // Réessayer après un court délai
                true        // Relancer le flow
            } else {
                false       // Autres erreurs → pas de retry
            }
        }.flowOn(Dispatchers.IO)


    val medicines = medicines(orderFilter = OrderFilter.NONE, filter = "").stateIn(
        scope = scope,//meme scope
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = Result.Loading
    )

    fun getMedicine(medicineId: String): Task<Medicine> = firebaseApi.getMedicine(medicineId)

    fun historyPager(medicineId: String): HistoryPagingSource =
        HistoryPagingSource(medicineId)

    fun addAisle(aisle: String) {
        firebaseApi.addAisle(aisle)
    }

    fun addMedicine(medicine: MedicineDto) {
        firebaseApi.addMedicine(medicine)
    }

    fun addHistory(medicineId: String, history: History): Task<DocumentReference?> {
        return firebaseApi.addHistory(medicineId, history)
    }

    fun modifyMedicine(medicineId: String, name: String, aisle: String, stock: Int) {
        firebaseApi.modifyMedicine(medicineId, name, aisle, stock)
    }

    fun deleteMedicine(idMedicine: String): Task<Task<Void?>?> {
        return firebaseApi.deleteMedicine(idMedicine)
    }

    fun deleteAisleWithoutMedicine(aisleId: String): Task<Void?> {
        return firebaseApi.deleteAisleWithoutMedicine(aisleId)
    }

    fun deleteAisleAndAllMedicine(aisleId: String, nameAisle: String): Task<Task<Void?>?> {
        return firebaseApi.deleteAisleAndAllMedicine(aisleId, nameAisle)
    }

    fun deleteByMovingAllMedicine(
        aisleId: String,
        targetAisleName: String,
        nameAisle: String
    ): Task<Task<Void?>?> {
        return firebaseApi.deleteByMovingAllMedicine(aisleId, targetAisleName, nameAisle)
    }

    var snapshotListenerRegistration: ListenerRegistration? = null
    private var isFirstSnapshot = true


    fun pagerFlow(medicineId: String): Flow<PagingData<History>> {
        return Pager(
            config = PagingConfig(pageSize = 20, prefetchDistance = 5),
            pagingSourceFactory = { historyPager(medicineId) }
        ).flow
    }

    private val _shouldReload = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val shouldReload: SharedFlow<Unit> = _shouldReload


    /**
     * Sets up a snapshot listener for the history collection of a specific medicine.
     *
     * This function removes any existing snapshot listener and initializes a new one
     * for the history of the medicine identified by `id`.
     *
     * The listener observes changes in the "history" subcollection of the specified medicine.
     * It is designed to emit a signal to `_shouldReload` when a new document is added
     * to the history and the change originates from the server (not the local cache).
     * The Goal of this is to trigger a reload of the history data in the UI when
     * new data is added to the history collection from the server (any device).
     *
     * The first snapshot received is ignored to prevent unnecessary reloads when the listener
     * is initially attached.
     *
     * @param id The ID of the medicine for which to set up the history snapshot listener.
     */
    fun setupSnapshotListener(id: String) {
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
                _shouldReload.tryEmit(Unit)
            }
        }
    }
}
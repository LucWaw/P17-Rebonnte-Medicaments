package com.openclassrooms.rebonnte.repository

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentReference
import com.openclassrooms.rebonnte.domain.Aisle
import com.openclassrooms.rebonnte.domain.History
import com.openclassrooms.rebonnte.domain.Medicine
import com.openclassrooms.rebonnte.domain.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.retry
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StockRepository @Inject constructor(
    private val firebaseApi: FirebaseApi,
    private val internetContext: InternetContext
) {

    fun aisles(): Flow<Result<List<Aisle>>> = flow {
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
}
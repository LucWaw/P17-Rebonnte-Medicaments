package com.openclassrooms.rebonnte.repository

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentReference
import com.openclassrooms.rebonnte.domain.Aisle
import com.openclassrooms.rebonnte.domain.History
import com.openclassrooms.rebonnte.domain.Medicine
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StockRepository @Inject constructor(private val firebaseApi: FirebaseApi) {

    val aisles: Flow<List<Aisle>> = firebaseApi.getAllAisles()

    fun medicinesPager(orderFilter: OrderFilter, filter: String = ""): MedicinePagingSource =
        MedicinePagingSource(
            orderFilter,
            filter
        )

    fun medicines(orderFilter: OrderFilter, filter : String = ""): Flow<List<Medicine>> {
        return firebaseApi.getAllMedicines(orderFilter, filter)
    }



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
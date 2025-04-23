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

    fun medicines(orderFilter: OrderFilter) :  Flow<List<Medicine>> = firebaseApi.getAllMedicines(orderFilter)

    fun addAisle(aisle: String) {
        firebaseApi.addAisle(aisle)
    }

    fun addMedicine(medicine: Medicine) {
        firebaseApi.addMedicine(medicine)
    }

    fun addHistory(medicineId : String, history: History): Task<DocumentReference?> {
        return firebaseApi.addHistory(medicineId, history)
    }

    fun modifyMedicine(medicineId: String, stock: Int) {
        firebaseApi.modifyMedicine(medicineId, stock)
    }
}
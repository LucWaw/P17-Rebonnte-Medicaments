package com.openclassrooms.rebonnte.repository

import com.openclassrooms.rebonnte.domain.Aisle
import com.openclassrooms.rebonnte.domain.Medicine
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StockRepository @Inject constructor(private val firebaseApi: FirebaseApi) {

    val aisles: Flow<List<Aisle>> = firebaseApi.getAllAisles()

    fun medicines(orderFilter: OrderFilter) :  Flow<List<Medicine>> = firebaseApi.getAllMedicines(orderFilter)
}
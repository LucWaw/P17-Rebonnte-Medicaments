package com.openclassrooms.rebonnte.repository

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query.Direction
import com.openclassrooms.rebonnte.domain.History
import com.openclassrooms.rebonnte.domain.Medicine
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.tasks.await

class MedicinePagingSource(
    private val orderBy: OrderFilter,
    private val filter: String = ""
) : PagingSource<DocumentSnapshot, Medicine>() {

    private val MEDICINE_COLLECTION_NAME = "medicines"


    override suspend fun load(
        params: LoadParams<DocumentSnapshot>
    ): LoadResult<DocumentSnapshot, Medicine> {
        return try {
            val collection = getMedecineCollection()
            // 1. Construire la query de base avec tri/filtre
            val baseQuery = when (orderBy) {
                OrderFilter.ORDER_BY_NAME ->
                    collection.orderBy("name")
                OrderFilter.ORDER_BY_STOCK ->
                    collection.orderBy("stock")
                OrderFilter.FILTER_BY_NAME ->
                    collection
                        .whereGreaterThanOrEqualTo("name", filter)
                        .whereLessThanOrEqualTo("name", filter + '\uf8ff')
                else ->
                    collection
            }

            // 2. Appliquer la pagination
            val query = params.key
                ?.let { baseQuery.startAfter(it) }
                ?: baseQuery
            val limitedQuery = query.limit(params.loadSize.toLong())

            // 3. Exécuter la requête et récupérer les documents
            val snapshot = limitedQuery.get().await()
            val docs = snapshot.documents

            // 4. Mapper en Medicine + chargement des histories
            val medicines = coroutineScope {
                docs.map { doc ->
                    async {
                        val med = doc.toObject(Medicine::class.java)!!.copy(id = doc.id)
                        val histories = getHistoriesForMedicine(doc.id)
                        med.copy(histories = histories)
                    }
                }.awaitAll()
            }

            // 5. Préparer la clé pour la page suivante
            val lastDoc = docs.lastOrNull()
            LoadResult.Page(
                data = medicines,
                prevKey = null,
                nextKey = lastDoc
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    private fun getMedecineCollection(): CollectionReference {
        return FirebaseFirestore.getInstance().collection(MEDICINE_COLLECTION_NAME)
    }

    suspend fun getHistoriesForMedicine(medicineId: String): List<History> {
        val history = getMedecineCollection()
            .document(medicineId)
            .collection("history").orderBy(
                "date",
                Direction.DESCENDING
            )
            .get()
            .await().documents.map { element : DocumentSnapshot ->
                History(
                    id = element.id,
                    medicineName = element.getString("medicineName") ?: "",
                    userId = element.getString("userId") ?: "",
                    date = element.getLong("date") ?: 0L,
                    details = element.getString("details") ?: ""
                )
            }
        return history
    }

    override fun getRefreshKey(
        state: PagingState<DocumentSnapshot, Medicine>
    ): DocumentSnapshot? {
        // Relancer à la page contenant l’anchorPosition
        return state.anchorPosition?.let { pos ->
            state.closestPageToPosition(pos)?.prevKey
                ?: state.closestPageToPosition(pos)?.nextKey
        }
    }
}

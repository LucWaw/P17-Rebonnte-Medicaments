package com.openclassrooms.rebonnte.repository

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query.Direction
import com.openclassrooms.rebonnte.domain.History
import kotlinx.coroutines.tasks.await

class HistoryPagingSource(
    private val medicineId: String,
) : PagingSource<DocumentSnapshot, History>() {

    private val MEDICINE_COLLECTION_NAME = "medicines"

    private fun getMedecineCollection(): CollectionReference {
        return FirebaseFirestore.getInstance().collection(MEDICINE_COLLECTION_NAME)
    }

    override suspend fun load(params: LoadParams<DocumentSnapshot>): LoadResult<DocumentSnapshot, History> {
        return try {
            val collection = getMedecineCollection()
            // 1. Construire la query de base avec tri/filtre
            val baseQuery = collection.document(medicineId).collection("history").orderBy(
                "date",
                Direction.DESCENDING
            )

            // 2. Appliquer la pagination
            val query = params.key
                ?.let { baseQuery.startAfter(it) }
                ?: baseQuery
            val limitedQuery = query.limit(params.loadSize.toLong())

            // 3. Exécuter la requête et récupérer les documents
            val snapshot = limitedQuery.get().await()
            val docs = snapshot.documents

            // 4. Mapper en History
            val histories = docs.map { doc ->
                doc.toObject(History::class.java)!!.copy(id = doc.id)
            }

            // 5. Déterminer la clé pour la page suivante
            val lastDoc = docs.lastOrNull()

            LoadResult.Page(
                data = histories,
                prevKey = null,
                nextKey = lastDoc
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(
        state: PagingState<DocumentSnapshot, History>
    ): DocumentSnapshot? {
        // Relancer à la page contenant l’anchorPosition
        return state.anchorPosition?.let { pos ->
            state.closestPageToPosition(pos)?.prevKey
                ?: state.closestPageToPosition(pos)?.nextKey
        }
    }
}

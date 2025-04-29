package com.openclassrooms.rebonnte.repository
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.openclassrooms.rebonnte.domain.Aisle
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.tasks.await

class AislePagingSource(
) : PagingSource<DocumentSnapshot, Aisle>() {

    private val AISLE_COLLECTION_NAME = "aisles"

    private fun getAisleCollection(): CollectionReference {
        return FirebaseFirestore.getInstance().collection(AISLE_COLLECTION_NAME)
    }


    override suspend fun load(
        params: LoadParams<DocumentSnapshot>
    ): LoadResult<DocumentSnapshot, Aisle> {
        return try {
            // 1. Construire la query de base (tri par nom)
            val baseQuery = getAisleCollection()
                .orderBy("name")

            // 2. Appliquer la pagination via cursors
            val pagedQuery = params.key
                ?.let { baseQuery.startAfter(it) }
                ?: baseQuery
            val limitedQuery = pagedQuery.limit(params.loadSize.toLong())

            // 3. Exécuter la requête
            val snapshot = limitedQuery.get().await()
            val docs = snapshot.documents

            // 4. Mapper en Aisle et appliquer le tri “Main aisle” en premier
            val aisles = docs.map { doc ->
                doc.toObject(Aisle::class.java)!!.copy(id = doc.id)
            }.sortedBy { if (it.name == "Main aisle") 0 else 1 }

            // 5. Déterminer la clé pour la page suivante
            val lastDoc = docs.lastOrNull()

            LoadResult.Page(
                data = aisles,
                prevKey = null,
                nextKey = lastDoc
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(
        state: PagingState<DocumentSnapshot, Aisle>
    ): DocumentSnapshot? {
        // Repartir de la page contenant la position d’ancrage
        return state.anchorPosition
            ?.let { pos ->
                state.closestPageToPosition(pos)?.prevKey
                    ?: state.closestPageToPosition(pos)?.nextKey
            }
    }
}

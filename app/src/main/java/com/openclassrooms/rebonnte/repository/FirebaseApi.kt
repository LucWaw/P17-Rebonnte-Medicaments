package com.openclassrooms.rebonnte.repository

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.snapshots
import com.openclassrooms.rebonnte.domain.Aisle
import com.openclassrooms.rebonnte.domain.History
import com.openclassrooms.rebonnte.domain.Medicine
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class FirebaseApi {

    private val AISLE_COLLECTION_NAME = "aisles"

    private val MEDICINE_COLLECTION_NAME = "medicines"


    private fun getAisleCollection(): CollectionReference {
        return FirebaseFirestore.getInstance().collection(AISLE_COLLECTION_NAME)
    }

    private fun getMedecineCollection(): CollectionReference {
        return FirebaseFirestore.getInstance().collection(MEDICINE_COLLECTION_NAME)
    }

    fun getAllAisles(): Flow<List<Aisle>> {
        return getAisleCollection()
            .orderBy("name")
            .snapshots()
            .map { querySnapshot ->
                querySnapshot.toObjects(Aisle::class.java)
            }
    }

    /**
     * Récupère la liste des médicaments en temps réel, avec possibilité de trier ou filtrer selon différents critères.
     * Chaque médicament est enrichi avec son historique (sous-collection "History").
     *
     * @param orderBy Le critère de tri ou de filtre à appliquer (par nom, par stock, etc.).
     * @return Un Flow émettant une liste de médicaments mise à jour en temps réel.
     */
    fun getAllMedicines(orderBy: OrderFilter, filter : String = ""): Flow<List<Medicine>> = callbackFlow {
        // Récupération de la référence à la collection "Medicines" dans Firestore
        val collection = getMedecineCollection()

        // Application du filtre ou de l'ordre selon la valeur de "orderBy"
        val collectionRef = when (orderBy) {
            OrderFilter.ORDER_BY_NAME -> collection.orderBy("name")
            OrderFilter.ORDER_BY_STOCK -> collection.orderBy("stock")
            OrderFilter.NONE -> collection
            OrderFilter.FILTER_BY_NAME -> collection.whereGreaterThanOrEqualTo("name", filter)
        }

        // Mise en place du listener Firestore pour recevoir les mises à jour en temps réel
        val listener = collectionRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                // En cas d'erreur, on ferme le flux
                close(error)
                return@addSnapshotListener
            }

            // Liste des documents récupérés ou liste vide par défaut
            val documents = snapshot?.documents ?: emptyList()

            // Lancement d'une coroutine pour charger les historiques de chaque médicament en parallèle
            CoroutineScope(Dispatchers.IO).launch {
                val medicineList = documents.map { document ->
                    // Conversion du document Firestore en objet Medicine, avec récupération de l'ID
                    val medicine =
                        document.toObject(Medicine::class.java)!!.copy(name = document.id)

                    // Récupération de la sous-collection "History" pour ce médicament
                    val histories = getHistoriesForMedicine(document.id)

                    // Création de l'objet complet avec historique
                    medicine.copy(histories = histories)
                }

                // Envoi de la liste dans le flux
                trySend(medicineList)
            }
        }

        // Nettoyage : suppression du listener quand le Flow est annulé
        awaitClose { listener.remove() }
    }


    suspend fun getHistoriesForMedicine(medicineId: String): List<History> {
        return getMedecineCollection()
            .document(medicineId)
            .collection("history")
            .get()
            .await()
            .toObjects(History::class.java)
    }


}
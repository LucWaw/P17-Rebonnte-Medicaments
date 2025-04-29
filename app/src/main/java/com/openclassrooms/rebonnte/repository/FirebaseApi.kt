package com.openclassrooms.rebonnte.repository

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query.Direction
import com.google.firebase.firestore.snapshots
import com.openclassrooms.rebonnte.domain.Aisle
import com.openclassrooms.rebonnte.domain.History
import com.openclassrooms.rebonnte.domain.Medicine
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

    /**
     * Récupère la liste des allées en temps réel, triée par nom. avec Main aisle en premier.
     *
     * @return Un Flow émettant une liste d'allées mise à jour en temps réel.
     */
    fun getAllAisles(): Flow<List<Aisle>> {
        return getAisleCollection()
            .orderBy("name")
            .snapshots()
            .map { snapshot ->
                // Conversion des documents Firestore en objets Aisle
                val aisles = snapshot.documents.map { document ->
                    document.toObject(Aisle::class.java)!!.copy(id = document.id)
                }
                // Tri des allées pour que "Main aisle" soit en premier
                aisles.sortedBy { if (it.name == "Main aisle") 0 else 1 }
            }
    }

    /**
     * Récupère la liste des médicaments en temps réel, avec possibilité de trier ou filtrer selon différents critères.
     * Chaque médicament est enrichi avec son historique (sous-collection "History").
     *
     * @param orderBy Le critère de tri ou de filtre à appliquer (par nom, par stock, etc.).
     * @return Un Flow émettant une liste de médicaments mise à jour en temps réel.
     */
    fun getAllMedicines(orderBy: OrderFilter, filter: String = ""): Flow<List<Medicine>> =
        callbackFlow {
            // Récupération de la référence à la collection "Medicines" dans Firestore
            val collection = getMedecineCollection()

            // Application du filtre ou de l'ordre selon la valeur de "orderBy"
            val collectionRef = when (orderBy) {
                OrderFilter.ORDER_BY_NAME -> collection.orderBy("name")
                OrderFilter.ORDER_BY_STOCK -> collection.orderBy("stock")
                OrderFilter.NONE -> collection
                OrderFilter.FILTER_BY_NAME -> collection
                    .whereGreaterThanOrEqualTo("name", filter)
                    .whereLessThanOrEqualTo("name", filter + '\uf8ff')

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
                this.launch {
                    val medicineList = documents.map { document ->
                        // Conversion du document Firestore en objet Medicine, avec récupération de l'ID
                        val medicine =
                            document.toObject(Medicine::class.java)!!.copy(id = document.id)

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

    fun addAisle(nameAisle: String) {
        val aisle = Aisle(name = nameAisle, id = "")


        getAisleCollection().add(aisle)
    }

    fun addMedicine(medicine: MedicineDto) {
        getMedecineCollection().add(medicine)
    }

    fun addHistory(medicineId: String, history: History): Task<DocumentReference?> {
        return getMedecineCollection()
            .document(medicineId)
            .collection("history")
            .add(history)
    }

    fun modifyMedicine(medicineId: String, name: String, aisle: String, stock: Int) {
        getMedecineCollection()
            .document(medicineId)
            .update("stock", stock, "name", name, "nameAisle", aisle)
    }

    fun deleteMedicine(idMedicine: String): Task<Task<Void?>?> {
        // Récupère la référence du document du médicament
        val medicineRef = getMedecineCollection().document(idMedicine)

        // Récupère la sous-collection "history" du médicament
        val historyRef = medicineRef.collection("history")

        // Supprime tous les documents de la sous-collection "history"
        return historyRef.get().continueWith { task ->
            if (task.isSuccessful) {
                // Récupère les documents de l'historique
                val historyDocs = task.result?.documents ?: emptyList()

                // Supprime chaque document de l'historique
                historyDocs.forEach { doc ->
                    doc.reference.delete() // Supprimer chaque document de l'historique
                }
            }
        }.continueWith { task ->
            // Après avoir supprimé les documents de l'historique, on supprime le document du médicament
            medicineRef.delete()
        }
    }


    /**
     * Supprime une allée sans médicaments associés.
     *
     * @param aisleId Le nom de l'allée à supprimer.
     * @return Une tâche indiquant le résultat de l'opération.
     */
    fun deleteAisleWithoutMedicine(aisleId: String): Task<Void?> {
        return getAisleCollection().document(aisleId).delete()
    }

    /**
     * Supprime une allée et tous les médicaments associés.
     *
     * @param aisleId Le nom de l'allée à supprimer.
     * @return Une tâche indiquant le résultat de l'opération.
     */
    fun deleteAisleAndAllMedicine(aisleId: String, nameAisle: String): Task<Task<Void?>?> {
        // Récupère la référence de l'allée à supprimer
        val aisleRef = getAisleCollection().document(aisleId)

        // Récupère la sous-collection "medicines" de l'allée
        val medicinesRef = getMedecineCollection()

        // Supprime tous les documents de la collection "medicines" ayant l'allée correspondante
        return medicinesRef.get().continueWith { task ->
            if (task.isSuccessful) {
                // Récupère les documents de la collection "medicines"
                val medicineDocs = task.result?.documents ?: emptyList()
                // Supprime chaque document de la collection "medicines" ayant l'allée correspondante
                medicineDocs.forEach { doc ->
                    if (doc.getString("nameAisle") == nameAisle) {
                        deleteMedicine(doc.id)
                    }
                }
            }
        }.continueWith { task ->
            // Après avoir supprimé les documents des médicaments, on supprime le document de l'allée
            aisleRef.delete()
        }
    }

    /**
     * Déplace tous les médicaments d'une allée vers une autre puis supprime l'allée source.
     *
     * @param aisleId L'ID de l'allée source.
     * @param targetAisleName L'ID de l'allée cible.
     * @return Une tâche indiquant le résultat de l'opération.
     */
    fun deleteByMovingAllMedicine(aisleId: String, targetAisleName: String, nameAisle: String): Task<Task<Void?>?> {
        // Récupère la référence de l'allée à supprimer
        val aisleRef = getAisleCollection().document(aisleId)

        // Récupère la sous-collection "medicines" de l'allée
        val medicinesRef = getMedecineCollection()

        // Supprime tous les documents de la collection "medicines" ayant l'allée correspondante
        return medicinesRef.get().continueWith { task ->
            if (task.isSuccessful) {
                // Récupère les documents de la collection "medicines"
                val medicineDocs = task.result?.documents ?: emptyList()

                // Met à jour chaque document de la collection "medicines" pour changer l'allée
                medicineDocs.forEach { doc ->

                    if (doc.getString("nameAisle") == nameAisle) {
                        doc.reference.update("nameAisle", targetAisleName) // Mettre à jour l'allée
                    }
                }
            }
        }.continueWith { task ->
            // Après avoir mis à jour les documents des médicaments, on supprime le document de l'allée
            aisleRef.delete()
        }
    }


}
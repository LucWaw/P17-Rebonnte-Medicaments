package com.openclassrooms.rebonnte.ui.medicine

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openclassrooms.rebonnte.domain.History
import com.openclassrooms.rebonnte.domain.Medicine
import com.openclassrooms.rebonnte.repository.OrderFilter
import com.openclassrooms.rebonnte.repository.StockRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MedicineViewModel @Inject constructor(private val stockRepository: StockRepository) :
    ViewModel() {
    var medicines = stockRepository.medicines(OrderFilter.NONE)
    private val _selectedMedicine = MutableStateFlow<Medicine?>(null)
    val selectedMedicine: StateFlow<Medicine?> = _selectedMedicine

    fun loadMedicine(name: String) {
        viewModelScope.launch {
            stockRepository.medicines(OrderFilter.NONE)
                .map { list -> list.find { it.name == name } }
                .collect { _selectedMedicine.value = it }
        }
    }

    fun modifyMedicine(medicineId: String, stock: Int) {
        stockRepository.modifyMedicine(medicineId, stock)
    }

    fun addToHistory(medicineId: String,medicineName : String, history: History) {
        viewModelScope.launch {
            stockRepository.addHistory(medicineId, history).addOnSuccessListener {
                loadMedicine(medicineName) // recharge le medoc avec les nouveaux historiques
            }
        }
    }

    /*fun addToHistory(medicine: Medicine, history: History) {
        val currentMedicines = ArrayList(medicines.value)
        val index = currentMedicines.indexOf(medicine)
        if (index != -1) {
            val updatedMedicine = currentMedicines[index].copy(histories = currentMedicines[index].histories + history)
            currentMedicines[index] = updatedMedicine
            _medicines.value = currentMedicines
        }
    }*/


    /*fun addRandomMedicine(aisles: List<Aisle>) {
        val currentMedicines = ArrayList(medicines.value)
        currentMedicines.add(
            Medicine(
                "Medicine " + (currentMedicines.size + 1),
                Random().nextInt(100),
                aisles[Random().nextInt(aisles.size)].name,
                emptyList()
            )
        )
        _medicines.value = currentMedicines
    }*/

    /*fun filterByName(name: String) {
        val currentMedicines: List<Medicine> = medicines.value
        val filteredMedicines: MutableList<Medicine> = ArrayList()
        for (medicine in currentMedicines) {
            if (medicine.name.lowercase(Locale.getDefault())
                    .contains(name.lowercase(Locale.getDefault()))
            ) {
                filteredMedicines.add(medicine)
            }
        }
        _medicines.value = filteredMedicines
    }*/

    /*fun sortByNone() {
        _medicines.value = medicines.value.toMutableList() // Pas de tri
    }*/

    /*fun sortByName() {
        val currentMedicines = ArrayList(medicines.value)
        currentMedicines.sortWith(Comparator.comparing(Medicine::name))
        _medicines.value = currentMedicines
    }

    fun sortByStock() {
        val currentMedicines = ArrayList(medicines.value)
        currentMedicines.sortWith(Comparator.comparingInt(Medicine::stock))
        _medicines.value = currentMedicines
    }*/
}


package com.openclassrooms.rebonnte.ui.medicine.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openclassrooms.rebonnte.repository.MedicineDto
import com.openclassrooms.rebonnte.repository.StockRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


/**
 * UI state for the Add Medicine screen.
 * Holds the current values entered by the user.
 */
data class AddMedicineUiState(
    val name: String = "",
    val stockInput: String = "", // Keep stock as String for input field
    val selectedAisleName: String = ""
)

@HiltViewModel
class AddMedicineViewModel @Inject constructor(
    private val stockRepository: StockRepository
) : ViewModel() {

    /**
     * StateFlow providing the list of available aisles.
     */
    val aisles = stockRepository.aisles

    /**
     * Internal mutable state flow representing the current UI state of the form.
     */
    private val _uiState = MutableStateFlow(AddMedicineUiState())

    /**
     * Public immutable state flow representing the current UI state of the form.
     */
    val uiState: StateFlow<AddMedicineUiState> = _uiState.asStateFlow()

    /**
     * StateFlow derived from the uiState that emits an AddMedicineFormError
     * if any mandatory field is invalid, or null otherwise.
     */
    val error: StateFlow<AddMedicineFormError?> = uiState.map {
        verifyMedicineInput(it)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null // Initially no error
    )

    /**
     * Handles form events triggered by user input.
     *
     * @param formEvent The event to process.
     */
    fun onAction(formEvent: AddMedicineFormEvent) {
        when (formEvent) {
            is AddMedicineFormEvent.NameChanged -> {
                _uiState.update { it.copy(name = formEvent.name) }
            }

            is AddMedicineFormEvent.StockChanged -> {
                _uiState.update { it.copy(stockInput = formEvent.stock) }
            }

            is AddMedicineFormEvent.AisleChanged -> {
                _uiState.update { it.copy(selectedAisleName = formEvent.aisleName) }
            }
        }
    }

    /**
     * Attempts to save the medicine based on the current UI state.
     * Does nothing if the input is invalid.
     *
     * @return true if the medicine was successfully added (validation passed), false otherwise.
     */
    fun saveMedicine(): Boolean {
        val currentState = _uiState.value
        val currentError = verifyMedicineInput(currentState)

        if (currentError != null) {
            // Should not happen if button is disabled by error state, but good practice
            return false
        }

        // Validation passed, convert stock and create Medicine object
        val stockValue = currentState.stockInput.toIntOrNull()
        if (stockValue == null || stockValue <= 0) {
            // This specific case should be caught by verifyMedicineInput, but double-check
            // Or handle potential exceptions if verify didn't check numeric
            return false
        }

        val medicine = MedicineDto(
            name = currentState.name.trim(),
            nameAisle = currentState.selectedAisleName,
            stock = stockValue
        )

        viewModelScope.launch(Dispatchers.IO) { //Add Dispatchers.Io so it don't run on main thread (by default viewModelScope scope run on main)
            stockRepository.addMedicine(medicine)
        }
        // Clear form state after successful save
        _uiState.value = AddMedicineUiState()

        return true // Indicate success
    }

    /**
     * Verifies the mandatory fields of the current UI state.
     *
     * @param currentState The current AddMedicineUiState to verify.
     * @return An [AddMedicineFormError] if validation fails, null otherwise.
     */
    private fun verifyMedicineInput(currentState: AddMedicineUiState): AddMedicineFormError? {
        return when {
            currentState.name.isBlank() -> AddMedicineFormError.NameError
            currentState.selectedAisleName.isBlank() -> AddMedicineFormError.AisleError
            currentState.stockInput.isBlank() -> AddMedicineFormError.StockError // Check empty first
            currentState.stockInput.toIntOrNull() == null -> AddMedicineFormError.StockError // Check if numeric
            currentState.stockInput.toInt() <= 0 -> AddMedicineFormError.StockError // Check if positive
            else -> null // All checks passed
        }
    }
}
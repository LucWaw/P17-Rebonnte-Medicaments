package com.openclassrooms.rebonnte.ui.medicine.add

import androidx.annotation.StringRes
import com.openclassrooms.rebonnte.R

/**
 * A sealed class representing different events that can occur on the add medicine form.
 */
sealed class AddMedicineFormEvent {

    /**
     * Event triggered when the medicine name changes.
     * @param name The new name string.
     */
    data class NameChanged(val name: String) : AddMedicineFormEvent()

    /**
     * Event triggered when the medicine stock input changes.
     * @param stock The new stock string input.
     */
    data class StockChanged(val stock: String) : AddMedicineFormEvent()

    /**
     * Event triggered when the aisle selection changes.
     * @param aisleName The selected aisle name.
     */
    data class AisleChanged(val aisleName: String) : AddMedicineFormEvent()
}

/**
 * A sealed class representing different validation errors for the add medicine form.
 * Each error holds a reference to a string resource for the error message.
 */
sealed class AddMedicineFormError(@StringRes val messageRes: Int) {

    /**
     * Error indicating the medicine name is missing.
     */
    data object NameError : AddMedicineFormError(
        R.string.error_medicine_name
    )

    /**
     * Error indicating the aisle has not been selected.
     */
    data object AisleError : AddMedicineFormError(
        R.string.error_medicine_aisle
    )

    /**
     * Error indicating the stock is missing or not a valid positive number.
     */
    data object StockError : AddMedicineFormError(
        R.string.error_medicine_stock
    )
}
package com.openclassrooms.rebonnte.repository

import androidx.compose.runtime.Immutable

@Immutable
data class MedicineDto(
    val name: String = "",
    val stock: Int = 0,
    val nameAisle: String = ""
)
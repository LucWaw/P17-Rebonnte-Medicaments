package com.openclassrooms.rebonnte.domain

import androidx.compose.runtime.Immutable

@Immutable //Not in same module that compose usage so need to use Immutable for performance
data class Medicine(
    val id : String = "",
    val name: String = "",
    val stock: Int = 0,
    val nameAisle: String = "",
    val histories: List<History> = emptyList()
)
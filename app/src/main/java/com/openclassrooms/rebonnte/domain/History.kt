package com.openclassrooms.rebonnte.domain

import androidx.compose.runtime.Immutable

@Immutable //Not in same module that compose usage so need to use Immutable for performance
data class History(
    val id: String = "",
    val medicineName: String = "",
    val userEmail: String = "",
    val userName: String = "",
    val date: Long = 0,
    val details: String = ""
)
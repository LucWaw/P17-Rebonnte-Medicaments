package com.openclassrooms.rebonnte.domain

import androidx.compose.runtime.Immutable

@Immutable //Not in same module that compose usage so need to use Immutable for performance
data class Aisle(val id : String = "", val name: String = "")
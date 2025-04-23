package com.openclassrooms.rebonnte.domain

data class Medicine(
    val id : String = "",
    var name: String = "",
    var stock: Int = 0,
    var nameAisle: String = "",
    var histories: List<History> = emptyList()
)
package com.openclassrooms.rebonnte.domain

data class Medicine(
    var name: String = "",
    var stock: Int = 0,
    var nameAisle: String = "",
    var histories: List<History> = emptyList()
)
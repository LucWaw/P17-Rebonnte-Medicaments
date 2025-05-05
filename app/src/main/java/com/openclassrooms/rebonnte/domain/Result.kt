package com.openclassrooms.rebonnte.domain

sealed interface Result<out D> {
    data class Success<out D>(val data: D) : Result<D>
    data object Error : Result<Nothing>
    data object Loading : Result<Nothing>
}
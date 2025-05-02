package com.openclassrooms.rebonnte.ui.account

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(directSignInEnabled: Boolean) : ViewModel() {
    val isDirectSignInEnabled = directSignInEnabled
}

package com.openclassrooms.rebonnte.ui.profile

import android.content.Context
import androidx.lifecycle.ViewModel
import com.google.android.gms.tasks.Task
import com.openclassrooms.rebonnte.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(private val userRepository: UserRepository) : ViewModel() {

    fun signOutCurrentUser(context: Context) : Task<Void> {
        return userRepository.signOut(context)
    }

    fun deleteCurrentUser(context: Context) : Task<Void> {
        return userRepository.deleteUser(context)
    }
}
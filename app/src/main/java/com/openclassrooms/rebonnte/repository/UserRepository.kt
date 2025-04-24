package com.openclassrooms.rebonnte.repository

import android.content.Context
import com.firebase.ui.auth.AuthUI
import com.google.android.gms.tasks.Task
import javax.inject.Singleton

@Singleton
class UserRepository {

    fun signOut(context: Context): Task<Void> {
        return AuthUI.getInstance().signOut(context)
    }

    fun deleteUser(context: Context): Task<Void> {
        return AuthUI.getInstance().delete(context)
    }
}
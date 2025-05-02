package com.openclassrooms.rebonnte.ui.account

import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.hilt.navigation.compose.hiltViewModel
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.ErrorCodes
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.firebase.auth.FirebaseAuth
import com.openclassrooms.rebonnte.R

@Composable
fun SignInScreen(
    modifier: Modifier = Modifier,
    navigateToMedicineScreen: () -> Unit,
    hiltViewModel: SignInViewModel = hiltViewModel(),
    ) {
    var retry by remember { mutableStateOf(false) }
    if (retry == false) {
        Box(
            modifier = modifier.fillMaxSize().testTag("InProgress"), contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    }


    var isConnected by remember { mutableStateOf(FirebaseAuth.getInstance().currentUser!=null) }
    val context = LocalContext.current



    val launcher =
        rememberLauncherForActivityResult(FirebaseAuthUIActivityResultContract()) { result ->
            if (result.resultCode == RESULT_OK) {
                isConnected = true
            }
            handleResponseAfterSignIn(
                result,
                onError = { retry = true },
                onSignInSuccess = navigateToMedicineScreen,
                context = context
            )

        }

    LaunchedEffect(Unit) {

        if (!isConnected) {
            if (hiltViewModel.isDirectSignInEnabled) {
                FirebaseAuth.getInstance().signInWithEmailAndPassword("fakehhkgugugugufugubkdt@mail.com", "Gjgjgjvkfyfuvk").addOnSuccessListener {
                    isConnected = true
                }.addOnFailureListener {
                    retry = true
                }
            } else {
                signIn(launcher)
            }
        }
    }

    if (isConnected) {
        navigateToMedicineScreen()
    } else {

        if (retry) {
            ErrorState {
                retry = false
                signIn(launcher)
            }

        }

    }
}


private fun signIn(launcher: ManagedActivityResultLauncher<Intent, FirebaseAuthUIAuthenticationResult>) {
    val providers = listOf(
        AuthUI.IdpConfig.EmailBuilder().build(),
    )

    val signInIntent =
        AuthUI.getInstance().createSignInIntentBuilder().setTheme(R.style.theme_SignIn)
            .setAvailableProviders(providers).setLogo(R.mipmap.ic_launcher).build()

    launcher.launch(signInIntent)

}

fun handleResponseAfterSignIn(
    result: FirebaseAuthUIAuthenticationResult?,
    onError: () -> Unit,
    context: Context,
    onSignInSuccess: () -> Unit
) {
    if (result?.resultCode == RESULT_OK) {
        // Handle sign-in success
        onSignInSuccess()
        Toast.makeText(context, context.getString(R.string.SuccessfulConnexion), Toast.LENGTH_LONG)
            .show()
    } else {
        // ERRORS
        if (result == null) {
            Toast.makeText(context, context.getString(R.string.unknow_error), Toast.LENGTH_LONG)
                .show()
            onError()
        } else if (result.resultCode == ErrorCodes.NO_NETWORK) {
            Toast.makeText(context, context.getString(R.string.network_error), Toast.LENGTH_LONG)
                .show()
            onError()
        } else if (result.resultCode == ErrorCodes.UNKNOWN_ERROR) {
            Toast.makeText(context, context.getString(R.string.unknow_error), Toast.LENGTH_LONG)
                .show()
            onError()
        }
    }
}

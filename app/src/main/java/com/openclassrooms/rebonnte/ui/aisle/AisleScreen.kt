package com.openclassrooms.rebonnte.ui.aisle

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.firebase.ui.auth.AuthUI
import com.openclassrooms.rebonnte.domain.Aisle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AisleScreen(
    viewModel: AisleViewModel = hiltViewModel(),
    addAisle: () -> Unit,
    navigateToLogin: () -> Unit,
    goToDetail: (String) -> Unit
) {
    val aisles by viewModel.aisles.collectAsState(initial = emptyList())

    Scaffold(
        topBar =
            {
                Column(verticalArrangement = Arrangement.spacedBy((-1).dp)) {
                    TopAppBar(
                        title = { Text(text = "Aisle") },
                    )
                }
            },
        floatingActionButton = {
            val context = LocalContext.current
            Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
                FloatingActionButton(onClick = {
                    AuthUI.getInstance().signOut(context).addOnSuccessListener {
                        navigateToLogin()
                    }
                }) {
                    Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Add")
                }
                FloatingActionButton(onClick = {
                    addAisle()
                }) {
                    Icon(Icons.Default.Add, contentDescription = "Add")
                }
            }

        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            items(aisles) { aisle ->
                AisleItem(aisle = aisle, onClick = {
                    goToDetail(aisle.name)
                })
            }
        }
    }

}

@Composable
fun AisleItem(aisle: Aisle, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = aisle.name, style = MaterialTheme.typography.bodyMedium)
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = "Arrow"
        )
    }
}


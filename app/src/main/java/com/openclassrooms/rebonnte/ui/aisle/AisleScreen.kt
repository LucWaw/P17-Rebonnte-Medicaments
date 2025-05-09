package com.openclassrooms.rebonnte.ui.aisle

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.openclassrooms.rebonnte.domain.Aisle
import com.openclassrooms.rebonnte.domain.Result
import com.openclassrooms.rebonnte.ui.component.ErrorState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AisleScreen(
    viewModel: AisleViewModel = hiltViewModel(),
    addAisle: () -> Unit,
    isAccessibilityEnabled: Boolean = false,
    goToDetail: (String) -> Unit
) {
    val aisles by viewModel.aisles.collectAsStateWithLifecycle(Result.Loading)

    Scaffold(
        topBar =
            {
                Column(verticalArrangement = Arrangement.spacedBy((-1).dp)) {
                    TopAppBar(
                        title = { Text(text = "Aisles") },
                        actions = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .padding(end = 8.dp)
                                    .background(MaterialTheme.colorScheme.surface)
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                if (isAccessibilityEnabled) {
                                    IconButton(
                                        onClick = {
                                            addAisle()
                                        }) {
                                        Icon(Icons.Default.Add, contentDescription = "Add")
                                    }
                                }
                            }
                        }
                    )
                }
            },
        floatingActionButton = {
            Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
                FloatingActionButton(
                    modifier = Modifier.testTag("addAisleFabButton"),
                    onClick = {
                    addAisle()
                }) {
                    Icon(Icons.Default.Add, contentDescription = "Add")
                }
            }

        }
    ) { paddingValues ->

        if (aisles is Result.Error) {
            ErrorState(retryButton = false)
        } else if (aisles is Result.Loading) {
            Box(
                modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (aisles is Result.Success) {
            val aislesList = (aisles as Result.Success).data

            LazyColumn(
                modifier = Modifier
                    .testTag("LazyAisle")
                    .padding(paddingValues)
                    .fillMaxSize()
            ) {
                items(aislesList, key = { aisle -> aisle.id }) { aisle ->
                    AisleItem(aisle) {
                        goToDetail(aisle.id)
                    }
                }
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


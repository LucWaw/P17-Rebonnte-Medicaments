package com.openclassrooms.rebonnte.ui.aisle

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.openclassrooms.rebonnte.domain.Aisle
import com.openclassrooms.rebonnte.ui.component.ItemPlaceholder

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AisleScreen(
    viewModel: AisleViewModel = hiltViewModel(),
    addAisle: () -> Unit,
    goToDetail: (String) -> Unit
) {
    val aisles = viewModel.aisles.collectAsLazyPagingItems()

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
            Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
                FloatingActionButton(onClick = {
                    addAisle()
                    aisles.refresh()
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
            items(
                aisles.itemCount,
                key = aisles.itemKey { it.id }
            ) { index ->
                val aisle = aisles[index]
                if (aisle != null) {
                    AisleItem (aisle){
                        goToDetail(aisle.id)
                    }
                } else {
                    ItemPlaceholder()
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


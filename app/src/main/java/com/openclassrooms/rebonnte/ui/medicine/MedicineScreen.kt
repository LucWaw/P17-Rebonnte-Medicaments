package com.openclassrooms.rebonnte.ui.medicine

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.openclassrooms.rebonnte.R
import com.openclassrooms.rebonnte.domain.Medicine
import com.openclassrooms.rebonnte.domain.Result
import com.openclassrooms.rebonnte.repository.OrderFilter
import com.openclassrooms.rebonnte.ui.component.ErrorState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicineScreen(
    viewModel: MedicineViewModel = hiltViewModel(),
    goToDetail: (String) -> Unit,
    isAccessibilityEnabled: Boolean = false,
    addMedicine: () -> Unit
) {
    val medicines by viewModel.getMedicines().collectAsStateWithLifecycle(Result.Loading)
    val listState = rememberLazyListState()


    Scaffold(
        topBar =
            {
                var isSearchActive by rememberSaveable { mutableStateOf(false) }
                var searchQuery by remember { mutableStateOf("") }

                Column(verticalArrangement = Arrangement.spacedBy((-1).dp)) {
                    TopAppBar(
                        title = { Text(text = "Medicines") },
                        actions = {
                            var expanded by remember { mutableStateOf(false) }
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
                                            addMedicine()
                                        }) {
                                        Icon(Icons.Default.Add, contentDescription = "Add")
                                    }
                                }


                                Box {
                                    IconButton(onClick = { expanded = true }) {
                                        Icon(
                                            Icons.Default.MoreVert,
                                            contentDescription = stringResource(
                                                R.string.sort_options
                                            )
                                        )
                                    }
                                    DropdownMenu(
                                        expanded = expanded,
                                        onDismissRequest = { expanded = false },
                                        offset = DpOffset(x = 0.dp, y = 0.dp)
                                    ) {
                                        DropdownMenuItem(
                                            onClick = {
                                                viewModel.updateFilterAndSort(OrderFilter.NONE)
                                                expanded = false
                                            },
                                            text = { Text("Sort by None") }
                                        )
                                        DropdownMenuItem(
                                            onClick = {
                                                viewModel.updateFilterAndSort(OrderFilter.ORDER_BY_NAME)
                                                expanded = false
                                            },
                                            text = { Text("Sort by Name") }
                                        )
                                        DropdownMenuItem(
                                            onClick = {
                                                viewModel.updateFilterAndSort(OrderFilter.ORDER_BY_STOCK)
                                                expanded = false
                                            },
                                            text = { Text("Sort by Stock") }
                                        )
                                    }
                                }

                            }
                        }
                    )
                    EmbeddedSearchBar(
                        query = searchQuery,
                        onQueryChange = {
                            searchQuery = it
                            viewModel.updateFilterAndSort(
                                OrderFilter.FILTER_BY_NAME,
                                searchQuery
                            )
                        },
                        isSearchActive = isSearchActive,
                        onActiveChanged = { isSearchActive = it }
                    )

                }
            },
        floatingActionButton = {
            FloatingActionButton(
                modifier = Modifier.testTag("addMedicineFabButton"),
                onClick = {
                    addMedicine()
                }) {
                Icon(Icons.Default.Add, contentDescription = "Add")
            }
        }

    ) { paddingValues ->

        if (medicines is Result.Error) {
            ErrorState(retryButton = false)
        } else if (medicines is Result.Loading) {
            Box(
                modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (medicines is Result.Success) {
            val medicinesList = (medicines as Result.Success).data

            LaunchedEffect(medicinesList) {
                if (!medicinesList.isEmpty()) {
                    listState.animateScrollToItem(index = 0)
                }
            }

            LazyColumn(
                state = listState,
                modifier = Modifier
                    .testTag("LazyMedicine")
                    .padding(paddingValues)
                    .fillMaxSize()
            ) {
                items(medicinesList, key = { medicine -> medicine.id }) { medicine ->
                    MedicineItem(medicine) {
                        goToDetail(medicine.id)
                    }
                }
            }
        }
    }

}

@Composable
fun MedicineItem(medicine: Medicine, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(text = medicine.name, style = MaterialTheme.typography.bodyLarge)
            Text(text = "Stock: ${medicine.stock}", style = MaterialTheme.typography.bodyMedium)
        }
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = "Arrow"
        )
    }
}


@Composable
fun EmbeddedSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    isSearchActive: Boolean,
    onActiveChanged: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    var searchQuery by rememberSaveable { mutableStateOf(query) }
    val activeChanged: (Boolean) -> Unit = { active ->
        searchQuery = ""
        onQueryChange("")
        onActiveChanged(active)
    }

    val shape: Shape = RoundedCornerShape(16.dp)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp)
            .padding(horizontal = 16.dp)
            .clip(shape)
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (isSearchActive) {
            IconButton(onClick = { activeChanged(false) }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        } else {
            Icon(
                imageVector = Icons.Rounded.Search,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        BasicTextField(
            value = searchQuery,
            onValueChange = { query ->
                searchQuery = query
                onQueryChange(query)
            },
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 8.dp),
            singleLine = true,
            decorationBox = { innerTextField ->
                if (searchQuery.isEmpty()) {
                    Text(
                        text = "Search",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
                innerTextField()
            }
        )

        if (isSearchActive && searchQuery.isNotEmpty()) {
            IconButton(onClick = {
                searchQuery = ""
                onQueryChange("")
            }) {
                Icon(
                    imageVector = Icons.Rounded.Close,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
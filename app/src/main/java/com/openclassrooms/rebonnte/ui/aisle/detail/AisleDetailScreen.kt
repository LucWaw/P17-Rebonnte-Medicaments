package com.openclassrooms.rebonnte.ui.aisle.detail

import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.gms.tasks.Task
import com.openclassrooms.rebonnte.R
import com.openclassrooms.rebonnte.domain.Medicine
import com.openclassrooms.rebonnte.ui.component.SimpleDialogContent


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AisleDetailScreen(
    id: String,
    navigateToMedicineDetail: (String) -> Unit,
    onBackClick: () -> Unit,
    viewModel: AisleDetailViewModel = hiltViewModel()
) {
    val medicines by viewModel.medicines.collectAsStateWithLifecycle(emptyList())
    val aisles by viewModel.aisles.collectAsStateWithLifecycle(emptyList())
    val aisle = aisles.find { it.id == id }
    val filteredMedicines = medicines.filter { it.nameAisle == aisle?.name }

    var showDeleteDialog by remember { mutableStateOf(false) }

    if (showDeleteDialog) {
        DeleteAisleDialogCustom(
            onDismissRequest = { showDeleteDialog = false },
            onConfirmSimpleDeleteNoMedicine = {
                showDeleteDialog = false
                viewModel.deleteWithoutMedicine(id)
            },
            onConfirmDeleteAisleAndAllMedicine = {
                showDeleteDialog = false
                viewModel.deleteAisleAndAllMedicine(id, aisle?.name ?: "")
            },
            onConfirmDeleteByMovingAllMedicine = { nameTargetAisle ->
                showDeleteDialog = false
                viewModel.deleteByMovingAllMedicine(id, nameTargetAisle, aisle?.name ?: "")
            },
            medicines = filteredMedicines,
            onBackClick = onBackClick,
            aisleOptionsAll = aisles.map { it.name },
            actualAisle = aisle?.name ?: "",
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(aisle?.name ?: "") },
                colors = TopAppBarDefaults.topAppBarColors(),
                navigationIcon =
                    {
                        IconButton(onClick = {
                            onBackClick()
                        }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                                contentDescription = stringResource(R.string.go_back)
                            )
                        }
                    },
                actions =
                    {
                        if (aisle?.name != "Main aisle") {
                            IconButton(modifier = Modifier.testTag("deleteAisle"), onClick = {
                                showDeleteDialog = true
                            }) {
                                Icon(
                                    imageVector = Icons.Filled.Delete,
                                    contentDescription = stringResource(R.string.delete_aisle)
                                )
                            }
                        }

                    }
            )
        }

    ) { paddingValues ->
        LazyColumn(
            contentPadding = paddingValues,
            modifier = Modifier.fillMaxSize()
        ) {
            items(filteredMedicines, key = { medicine -> medicine.id } ) { medicine ->
                MedicineItem(medicine = medicine, onClick = { id ->
                    navigateToMedicineDetail(id)
                })
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeleteAisleDialogCustom(
    modifier: Modifier = Modifier,
    medicines: List<Medicine>,
    onDismissRequest: () -> Unit,
    onBackClick: () -> Unit,
    onConfirmSimpleDeleteNoMedicine: () -> Task<Void?>,
    onConfirmDeleteAisleAndAllMedicine: () -> Task<Task<Void?>?>,
    onConfirmDeleteByMovingAllMedicine: (nameTargetAisle: String) -> Task<Task<Void?>?>,
    aisleOptionsAll: List<String>,
    actualAisle: String,
) {
    val aisleOptions = aisleOptionsAll.filter { it != actualAisle }
    Dialog(
        onDismissRequest = onDismissRequest
    ) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            tonalElevation = 4.dp,
            color = MaterialTheme.colorScheme.surface,
            modifier = modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            if (medicines.isEmpty()) {
                SimpleDialogContent(
                    onDismissRequest = onDismissRequest,
                    onConfirmDelete = {
                        onConfirmSimpleDeleteNoMedicine()
                        onBackClick()
                    },
                    title = stringResource(R.string.delete_aisle_without_medicine)
                )
            } else {
                val deleteALl = stringResource(R.string.delete_all_medicine)
                val movingAll = stringResource(R.string.moving_all_medicine)

                data class DeleteRadioOptions(
                    val name: String,
                    val method: () -> Task<Task<Void?>?>
                )

                var selectedAisle by remember { mutableStateOf(aisleOptions[0]) }

                val deleteRadioOptions = listOf(
                    DeleteRadioOptions(deleteALl) {
                        onConfirmDeleteAisleAndAllMedicine()
                    },
                    DeleteRadioOptions(movingAll) {
                        onConfirmDeleteByMovingAllMedicine(selectedAisle)
                    }
                )
                val (selectedOption, onOptionSelected) = remember {
                    mutableStateOf(
                        deleteRadioOptions[0].name
                    )
                }
                Column {
                    // Note that Modifier.selectableGroup() is essential to ensure correct accessibility behavior
                    Column(Modifier.selectableGroup()) {
                        deleteRadioOptions.forEach { options ->
                            Row(
                                Modifier
                                    .fillMaxWidth()
                                    .height(100.dp)
                                    .selectable(
                                        selected = (options.name == selectedOption),
                                        onClick = { onOptionSelected(options.name) },
                                        role = Role.RadioButton
                                    )
                                    .padding(horizontal = 16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = (options.name == selectedOption),
                                    onClick = null // null recommended for accessibility with screen readers
                                )
                                if (options.name == movingAll) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .fillMaxHeight()
                                            .padding(start = 16.dp),
                                        verticalArrangement = Arrangement.spacedBy(10.dp),
                                    ) {
                                        Text(
                                            text = options.name,
                                            style = MaterialTheme.typography.bodyLarge
                                        )
                                        var isAisleDropdownExpanded by remember {
                                            mutableStateOf(
                                                false
                                            )
                                        }

                                        ExposedDropdownMenuBox(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .fillMaxHeight(),
                                            expanded = isAisleDropdownExpanded,
                                            onExpandedChange = {
                                                isAisleDropdownExpanded = !isAisleDropdownExpanded
                                            },
                                        ) {
                                            OutlinedTextField(
                                                modifier = Modifier
                                                    .menuAnchor()
                                                    .fillMaxWidth(),
                                                readOnly = true,
                                                value = selectedAisle.ifEmpty { stringResource(R.string.select_aisle_placeholder) },
                                                onValueChange = {},
                                                label = { Text(stringResource(R.string.aisle_label)) },
                                                trailingIcon = {
                                                    ExposedDropdownMenuDefaults.TrailingIcon(
                                                        expanded = isAisleDropdownExpanded
                                                    )
                                                },
                                                colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
                                            )

                                            ExposedDropdownMenu(
                                                modifier = Modifier.fillMaxWidth(),
                                                expanded = isAisleDropdownExpanded,
                                                onDismissRequest = {
                                                    isAisleDropdownExpanded = false
                                                },
                                            ) {
                                                aisleOptions.forEach { selectionOption ->
                                                    DropdownMenuItem(
                                                        modifier = Modifier.fillMaxWidth(),
                                                        text = { Text(selectionOption) },
                                                        onClick = {
                                                            selectedAisle = selectionOption
                                                            isAisleDropdownExpanded = false
                                                        },
                                                        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                                                    )
                                                }

                                            }
                                        }


                                    }
                                } else {
                                    Text(
                                        text = options.name,
                                        style = MaterialTheme.typography.bodyLarge,
                                        modifier = Modifier.padding(start = 16.dp)
                                    )
                                }

                            }
                        }
                    }
                    val context = LocalContext.current
                    val string = getStringDelete(context, selectedOption, deleteALl, movingAll, selectedAisle)

                    SimpleDialogContent(
                        onDismissRequest = onDismissRequest,
                        onConfirmDelete = {
                            val selectedOption =
                                deleteRadioOptions.find { it.name == selectedOption }
                            selectedOption?.method?.invoke()
                            onBackClick()
                        },
                        title = stringResource(
                            R.string.do_you_really_want_to_do_delete_the_aisle, string
                        )
                    )
                }
            }
        }
    }

}

fun getStringDelete(context: Context, selectedOption: String, deleteALl: String, movingAll: String, selectedAisle : String): String {
    return when (selectedOption) {
        deleteALl -> context.getString(R.string.by_deleting_all_medicine)
        movingAll -> context.getString(R.string.by_moving_all_medicine, selectedAisle)
        else -> ""
    }
}


@Composable
fun MedicineItem(medicine: Medicine, onClick: (String) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick(medicine.id) }
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(text = medicine.name, fontWeight = FontWeight.Bold)
            Text(text = "Stock: ${medicine.stock}", color = Color.Gray)
        }
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = "Arrow"
        )
    }
}

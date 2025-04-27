package com.openclassrooms.rebonnte.ui.aisle.detail

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.openclassrooms.rebonnte.R
import com.openclassrooms.rebonnte.domain.Medicine


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AisleDetailScreen(
    id: String,
    navigateToMedicineDetail: (String) -> Unit,
    onBackClick: () -> Unit,
    viewModel: AisleDetailViewModel = hiltViewModel()
) {
    val medicines by viewModel.getMedicines().collectAsStateWithLifecycle(emptyList())
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
                viewModel.deleteAisleAndAllMedicine(id)
            },
            onConfirmDeleteByMovingAllMedicine = { nameTargetAisle ->
                showDeleteDialog = false
                viewModel.deleteByMovingAllMedicine(id, nameTargetAisle)
            },
            medicines = filteredMedicines,
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
                        IconButton(modifier = Modifier.testTag("deleteAisle"), onClick = {
                            showDeleteDialog = true
                        }) {
                            Icon(
                                imageVector = Icons.Filled.Delete,
                                contentDescription = stringResource(R.string.delete_aisle)
                            )
                        }
                    }
            )
        }

    ) { paddingValues ->
        LazyColumn(
            contentPadding = paddingValues,
            modifier = Modifier.fillMaxSize()
        ) {
            items(filteredMedicines) { medicine ->
                MedicineItem(medicine = medicine, onClick = { id ->
                    navigateToMedicineDetail(id)
                })
            }
        }
    }
}

@Composable
fun DeleteAisleDialogCustom(
    modifier: Modifier = Modifier,
    medicines : List<Medicine>,
    onDismissRequest: () -> Unit,
    onConfirmSimpleDeleteNoMedicine: () -> Unit,
    onConfirmDeleteAisleAndAllMedicine: () -> Unit,
    onConfirmDeleteByMovingAllMedicine: (nameTargetAisle: String) -> Unit
) {

    Dialog(
        onDismissRequest = onDismissRequest
    ) {
        if (medicines.isEmpty()){

        } else{
            val radioOptions = listOf("MovingAllMedicine", "DeleteAllMedicine")
            val (selectedOption, onOptionSelected) = remember { mutableStateOf(radioOptions[0]) }
            // Note that Modifier.selectableGroup() is essential to ensure correct accessibility behavior
            Column(Modifier.selectableGroup()) {
                radioOptions.forEach { text ->
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .selectable(
                                selected = (text == selectedOption),
                                onClick = { onOptionSelected(text) },
                                role = Role.RadioButton
                            )
                            .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = (text == selectedOption),
                            onClick = null // null recommended for accessibility with screen readers
                        )
                        Text(
                            text = text,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(start = 16.dp)
                        )
                    }
                }
            }
        }
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

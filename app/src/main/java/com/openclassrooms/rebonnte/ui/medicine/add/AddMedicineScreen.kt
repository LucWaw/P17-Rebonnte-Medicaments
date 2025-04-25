package com.openclassrooms.rebonnte.ui.medicine.add

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.openclassrooms.rebonnte.R
import com.openclassrooms.rebonnte.ui.theme.RebonnteTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMedicineScreen(
    modifier: Modifier = Modifier,
    viewModel: AddMedicineViewModel = hiltViewModel(),
    onValidate: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()
    val error by viewModel.error.collectAsState()
    val aisles by viewModel.aisles.collectAsState(initial = emptyList())

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.add_medicine_title)) },
                colors = TopAppBarDefaults.topAppBarColors()
            )
        }
    ) { paddingValues ->
        AddMedicineForm(
            modifier = Modifier.padding(paddingValues),
            uiState = uiState,
            error = error,
            aisleOptions = aisles.map { it.name },
            onAction = viewModel::onAction,
            onSaveClick = {
                if (viewModel.saveMedicine()) {
                    onValidate()
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddMedicineForm(
    modifier: Modifier = Modifier,
    uiState: AddMedicineUiState,
    error: AddMedicineFormError?,
    aisleOptions: List<String>,
    onAction: (AddMedicineFormEvent) -> Unit,
    onSaveClick: () -> Unit
) {
    var isAisleDropdownExpanded by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .verticalScroll(scrollState)
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            value = uiState.name,
            onValueChange = { onAction(AddMedicineFormEvent.NameChanged(it)) },
            label = { Text(stringResource(R.string.medicine_name_label)) },
            modifier = Modifier.fillMaxWidth()
                .testTag("MedicineNameField"),
            isError = error is AddMedicineFormError.NameError,
            singleLine = true,
            supportingText = {
                if (error is AddMedicineFormError.NameError) {
                    Text(
                        text = stringResource(id = error.messageRes),
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
        )

        ExposedDropdownMenuBox(
            modifier = Modifier.fillMaxWidth(),
            expanded = isAisleDropdownExpanded,
            onExpandedChange = { isAisleDropdownExpanded = !isAisleDropdownExpanded },
        ) {
            OutlinedTextField(
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth(),
                readOnly = true,
                value = uiState.selectedAisleName.ifEmpty { stringResource(R.string.select_aisle_placeholder) },
                onValueChange = {},
                label = { Text(stringResource(R.string.aisle_label)) },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isAisleDropdownExpanded) },
                colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                isError = error is AddMedicineFormError.AisleError,
                supportingText = {
                    if (error is AddMedicineFormError.AisleError) {
                        Text(
                            text = stringResource(id = error.messageRes),
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.padding(start = 16.dp)
                        )
                    }
                }
            )

            ExposedDropdownMenu(
                modifier = Modifier.fillMaxWidth(),
                expanded = isAisleDropdownExpanded,
                onDismissRequest = { isAisleDropdownExpanded = false },
            ) {
                aisleOptions.forEach { selectionOption ->
                    DropdownMenuItem(
                        modifier = Modifier.fillMaxWidth(),
                        text = { Text(selectionOption) },
                        onClick = {
                            onAction(AddMedicineFormEvent.AisleChanged(selectionOption))
                            isAisleDropdownExpanded = false
                        },
                        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                    )
                }

            }
        }


        OutlinedTextField(
            value = uiState.stockInput,
            onValueChange = { input ->
                onAction(AddMedicineFormEvent.StockChanged(input))
            },
            label = { Text(stringResource(R.string.stock_label)) },
            modifier = Modifier.fillMaxWidth(),
            isError = error is AddMedicineFormError.StockError,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            supportingText = {
                if (error is AddMedicineFormError.StockError) {
                    Text(
                        text = stringResource(id = error.messageRes),
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
        )

        Spacer(Modifier.weight(1f))

        Button(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .padding(bottom = 16.dp),
            onClick = onSaveClick,
            enabled = error == null
        ) {
            Text(stringResource(R.string.validate))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewAddMedicineScreenValid() {
    RebonnteTheme {
        AddMedicineForm(
            uiState = AddMedicineUiState(
                name = "Paracetamol",
                stockInput = "10",
                selectedAisleName = "Painkillers"
            ),
            error = null,
            aisleOptions = listOf("Painkillers", "Vitamins", "First Aid"),
            onAction = {},
            onSaveClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewAddMedicineScreenWithError() {
    RebonnteTheme {
        AddMedicineForm(
            uiState = AddMedicineUiState(name = "", stockInput = "-5", selectedAisleName = ""),
            error = AddMedicineFormError.NameError, // Show one error for preview
            aisleOptions = listOf("Painkillers", "Vitamins", "First Aid"),
            onAction = {},
            onSaveClick = {}
        )
    }
}
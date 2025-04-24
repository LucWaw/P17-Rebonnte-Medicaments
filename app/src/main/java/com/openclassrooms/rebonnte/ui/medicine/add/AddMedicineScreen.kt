package com.openclassrooms.rebonnte.ui.medicine.add

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.openclassrooms.rebonnte.R
import com.openclassrooms.rebonnte.domain.Medicine

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMedicineScreen(
    modifier: Modifier = Modifier,
    viewModel: AddMedicineViewModel = hiltViewModel(),
    onValidate: () -> Unit
) {
    val aisles by viewModel.aisles.collectAsState(initial = emptyList())

    var nameLocal by remember { mutableStateOf("") }

    var stockLocal by remember { mutableStateOf("") }



    Scaffold { paddingValues ->
        Column(
            modifier = modifier
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TextField(
                value = nameLocal,
                onValueChange = { nameLocal = it },
                label = { Text("Name") },
                modifier = Modifier.fillMaxWidth()
            )
            val options = aisles.map { it.name }
            var expanded by remember { mutableStateOf(false) }
            var selectedOptionText by remember { mutableStateOf("") }
            ExposedDropdownMenuBox(
                modifier = Modifier.fillMaxWidth(),
                expanded = expanded,
                onExpandedChange = { expanded = !expanded },
            ) {
                TextField(
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth(),
                    readOnly = true,
                    value = selectedOptionText,
                    onValueChange = {},
                    label = { Text("Aisle") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    colors = ExposedDropdownMenuDefaults.textFieldColors(),
                )
                ExposedDropdownMenu(
                    modifier = Modifier.fillMaxWidth(),
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                ) {
                    options.forEach { selectionOption ->
                        DropdownMenuItem(
                            modifier = Modifier.fillMaxWidth(),
                            text = { Text(selectionOption) },
                            onClick = {
                                selectedOptionText = selectionOption
                                expanded = false
                            },
                            contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                        )
                    }
                }
            }

            TextField(
                value = stockLocal,
                onValueChange = { stockLocal = it },
                label = { Text("Stock") },
                modifier = Modifier.fillMaxWidth()
            )
            Button(
                modifier = Modifier
                    .width(250.dp)
                    .height(50.dp)
                    .align(Alignment.End),
                onClick = {
                    val medicine = Medicine(
                        name = nameLocal,
                        nameAisle = selectedOptionText,
                        stock = stockLocal.toIntOrNull() ?: 0,
                    )
                    onValidate(); viewModel.addMedicine(medicine)
                }
            ) { Text(stringResource(R.string.validate)) }
        }
    }
}
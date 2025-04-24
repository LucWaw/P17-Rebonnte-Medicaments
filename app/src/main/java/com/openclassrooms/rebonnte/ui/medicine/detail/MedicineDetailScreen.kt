package com.openclassrooms.rebonnte.ui.medicine.detail

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.openclassrooms.rebonnte.R
import com.openclassrooms.rebonnte.domain.History
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale


@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MedicineDetailScreen(id: String, viewModel: MedicineDetailViewModel = hiltViewModel()) {
    val medicines by viewModel.medicines.collectAsState(initial = emptyList())
    val aisles by viewModel.aisles.collectAsState(initial = emptyList())
    val medicine = medicines.find { it.id == id } ?: return
    var nameLocal by remember { mutableStateOf(medicine.name) }

    var stockLocal by remember { mutableIntStateOf(medicine.stock) }


    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            TextField(
                value = nameLocal,
                onValueChange = { nameLocal = it },
                label = { Text("Name") },
                modifier = Modifier.fillMaxWidth()
            )
            val options = aisles.map { it.name }
            var expanded by remember { mutableStateOf(false) }
            var selectedOptionText by remember { mutableStateOf(medicine.nameAisle) }
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
                    value = selectedOptionText, //CONTIENT MAIN AISLE
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
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(onClick = {
                    if (stockLocal > 0) stockLocal--
                }) {
                    Icon(
                        imageVector = Icons.Filled.KeyboardArrowDown,
                        contentDescription = "Minus One"
                    )
                }
                TextField(
                    value = stockLocal.toString(),
                    onValueChange = {},
                    label = { Text("Stock") },
                    enabled = false,
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = {
                    stockLocal++
                }) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowUp,
                        contentDescription = "Plus One"
                    )
                }
            }

            Button(
                modifier = Modifier
                    .width(250.dp)
                    .height(50.dp)
                    .align(Alignment.End),
                onClick = {
                    viewModel.modifyMedicine(
                        medicine.id,
                        nameLocal,
                        selectedOptionText,
                        medicine.stock + (stockLocal - medicine.stock)
                    )
                    viewModel.addToHistory(
                        medicine.id,
                        History(
                            medicineName = medicine.name,
                            userId = "userId",
                            date = System.currentTimeMillis(),
                            details = "Modification de la quantité de ${medicine.stock} à $stockLocal"
                        )
                    )
                }
            ) { Text(stringResource(R.string.valid_modifications)) }

            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "History", style = MaterialTheme.typography.titleLarge)
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(medicine.histories) { history ->
                    HistoryItem(history = history)
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HistoryItem(history: History) {
    val formatter = DateTimeFormatter.ofPattern("d MMMM yyyy", Locale.FRENCH)
    val instant = Instant.ofEpochMilli(history.date)
    val date = instant.atZone(ZoneId.systemDefault()).toLocalDate()


    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = history.medicineName, fontWeight = FontWeight.Bold)
            Text(text = "User: ${history.userId}")
            Text(text = "Date: ${date.format(formatter)}")
            Text(text = "Details: ${history.details}")
        }
    }
}
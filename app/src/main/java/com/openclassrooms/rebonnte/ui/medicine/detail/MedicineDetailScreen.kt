package com.openclassrooms.rebonnte.ui.medicine.detail

import android.content.Context
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Delete
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.google.firebase.auth.FirebaseAuth
import com.openclassrooms.rebonnte.R
import com.openclassrooms.rebonnte.domain.History
import com.openclassrooms.rebonnte.domain.Result
import com.openclassrooms.rebonnte.formatDateFromMillis
import com.openclassrooms.rebonnte.ui.component.ItemPlaceholder
import com.openclassrooms.rebonnte.ui.component.SimpleDialogContent


@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MedicineDetailScreen(
    id: String,
    onBackClick: () -> Unit,
    viewModel: MedicineDetailViewModel = hiltViewModel()
) {
    val aisles by viewModel.aisles.collectAsStateWithLifecycle(Result.Loading)

    val medicine by viewModel.medicine.collectAsStateWithLifecycle()

    var stockLocal by remember { mutableIntStateOf(medicine.stock) }
    var nameLocal by remember { mutableStateOf(medicine.name) }
    var selectedOptionText by remember { mutableStateOf(medicine.nameAisle) }
    var trigerloadMedicineLaunchedEffect by remember { mutableStateOf(false) }
    var trigerloadHistoryLaunchedEffect by remember { mutableStateOf(false) }
    LaunchedEffect(id + trigerloadMedicineLaunchedEffect) {
        viewModel.loadMedicine(id).addOnSuccessListener {
            stockLocal = it?.stock ?: 0
            nameLocal = it?.name ?: ""
            selectedOptionText = it?.nameAisle ?: ""
        }

    }


    val historyItems = viewModel.history.collectAsLazyPagingItems()

    LaunchedEffect(id + trigerloadHistoryLaunchedEffect) {
        viewModel.loadHistory(id)
    }
    val lazyCollumnState = rememberLazyListState()

    LaunchedEffect(historyItems.itemSnapshotList.items.firstOrNull()) {
        // Déclenché lorsque l’historique est rechargé avec un nouvel item
        lazyCollumnState.animateScrollToItem(0)
    }

    var context = LocalContext.current

    var showDeleteDialog by remember { mutableStateOf(false) }

    if (showDeleteDialog) {
        DeleteMedicineDialogCustom(
            onDismissRequest = { showDeleteDialog = false },
            onConfirmDelete = {
                showDeleteDialog = false
                viewModel.deleteMedicine(id)
                    .addOnSuccessListener { innerTask ->
                        innerTask?.addOnSuccessListener {

                            onBackClick()
                            Toast.makeText(
                                context,
                                context.getString(R.string.deleted_with_success),
                                Toast.LENGTH_SHORT
                            ).show()
                        }?.addOnFailureListener {
                            Toast.makeText(
                                context,
                                context.getString(R.string.delete_error_database),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                    .addOnFailureListener {
                        Toast.makeText(
                            context,
                            context.getString(R.string.delete_error_history),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
            }
        )
    }

    val scrollState = rememberScrollState()

    Scaffold(
        modifier = Modifier.scrollable(scrollState, Orientation.Vertical),
        topBar =
            {
                TopAppBar(
                    title = { Text(medicine.name) },
                    navigationIcon = {
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
                            IconButton(
                                modifier = Modifier.testTag("deleteMedicine"),
                                onClick = {
                                    showDeleteDialog = true
                                }) {
                                Icon(
                                    imageVector = Icons.Filled.Delete,
                                    contentDescription = stringResource(R.string.delete_medicine)
                                )
                            }
                        }
                )
            }
    ) { paddingValues ->
        LazyColumn(state = lazyCollumnState, modifier = Modifier.fillMaxSize()) {
            item {
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
                    val options =
                        (aisles as? Result.Success)?.data?.map { it.name } ?: emptyList()
                    var expanded by remember { mutableStateOf(false) }
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
                            modifier = Modifier.weight(1f),
                            colors = TextFieldDefaults.colors(
                                disabledTextColor = MaterialTheme.colorScheme.onBackground.copy(
                                    alpha = 0.7f
                                ),
                                disabledLabelColor = MaterialTheme.colorScheme.onBackground.copy(
                                    alpha = 0.7f
                                ),
                            )
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
                            val modifiedDetails = whatIsModified(
                                nameLocal,
                                selectedOptionText,
                                stockLocal,
                                medicine.name,
                                medicine.nameAisle,
                                medicine.stock,
                                context
                            )


                            if (modifiedDetails == "Nothing was modified") {
                                Toast.makeText(
                                    context,
                                    context.getString(R.string.you_haven_t_modified_anything),
                                    Toast.LENGTH_SHORT
                                ).show()
                                return@Button
                            }

                            viewModel.modifyMedicine(
                                medicine.id,
                                nameLocal,
                                selectedOptionText,
                                medicine.stock + (stockLocal - medicine.stock)
                            ).addOnSuccessListener {
                                trigerloadMedicineLaunchedEffect = !trigerloadMedicineLaunchedEffect
                            }
                            viewModel.addToHistory(
                                medicine.id,
                                History(
                                    medicineName = medicine.name,
                                    userEmail = FirebaseAuth.getInstance().currentUser?.email
                                        ?: "",
                                    userName = FirebaseAuth.getInstance().currentUser?.displayName
                                        ?: "",
                                    date = System.currentTimeMillis(),
                                    details = modifiedDetails
                                )
                            ).addOnSuccessListener {
                                trigerloadHistoryLaunchedEffect = !trigerloadHistoryLaunchedEffect
                            }


                        }
                    ) { Text(stringResource(R.string.valid_modifications)) }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "History", style = MaterialTheme.typography.titleLarge)
                }
            }
            items(
                historyItems.itemCount,
                key = historyItems.itemKey { it.id }
            ) { index ->
                val history = historyItems[index]
                if (history != null) {
                    HistoryItem(history)
                } else {
                    ItemPlaceholder()
                }
            }

        }
    }


}

/**
 * Compares the local values of a medicine's name, aisle, and stock with the original values and identifies what has been modified.
 *
 * This function takes the original (database or previous) values of a medicine's attributes (name, aisle, stock)
 * and compares them to the locally stored values, representing user edits.
 * It then returns a user-friendly string describing what, if anything, has been changed.
 *
 * @param nameLocal The locally stored name of the medicine.
 * @param selectedOptionText The locally stored aisle where the medicine is located.
 * @param stockLocal The locally stored stock count of the medicine.
 * @param medicineName The original (database or previous) name of the medicine.
 * @param medicineAisle The original (database or previous) aisle of the medicine.
 * @param medicineStock The original (database or previous) stock count of the medicine.
 * @param context The application context, used for string resource access.
 * @return A string describing the modifications. If nothing was modified, it returns "Nothing was modified".
 *         Otherwise, it returns a string like "Modification of: Name: [old name] to [new name], Aisle: [old aisle] to [new aisle], Stock: [old stock] to [new stock]"
 *         The format of each change is defined by the string resources:
 *            - R.string.name_from_to: "Name: %1$s to %2$s"
 *            - R.string.aisle_from_to: "Aisle: %1$s to %2$s"
 *            - R.string.stock_from_to: "Stock: %1$d to %2$d"
 *            - R.string.modification_of: "Modification of: "
 *
 * @see R.string.name_from_to
 * @see R.string.aisle_from_to
 * @see R.string.stock_from_to
 * @see R.string.modification_of
 */
private fun whatIsModified(
    nameLocal: String,
    selectedOptionText: String,
    stockLocal: Int,
    medicineName: String,
    medicineAisle: String,
    medicineStock: Int,
    context: Context
): String {
    val modifications = mutableListOf<String>()

    if (nameLocal != medicineName) {
        modifications.add(context.getString(R.string.name_from_to, medicineName, nameLocal))
    }
    if (selectedOptionText != medicineAisle) {
        modifications.add(
            context.getString(
                R.string.aisle_from_to,
                medicineAisle,
                selectedOptionText
            )
        )
    }
    if (stockLocal != medicineStock) {
        modifications.add(context.getString(R.string.stock_from_to, medicineStock, stockLocal))
    }

    return if (modifications.isNotEmpty()) {
        context.getString(R.string.modification_of) + " " + modifications.joinToString(", ")
    } else {
        "Nothing was modified"
    }
}


@Composable
fun DeleteMedicineDialogCustom(
    onDismissRequest: () -> Unit,
    onConfirmDelete: () -> Unit
) {
    Dialog(onDismissRequest = onDismissRequest) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            tonalElevation = 4.dp,
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            SimpleDialogContent(
                onDismissRequest,
                onConfirmDelete,
                stringResource(R.string.popup_message_confirmation_delete_medicine)
            )
        }
    }
}


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HistoryItem(history: History) {
    val formattedDate = formatDateFromMillis(history.date)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp, horizontal = 8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = history.medicineName, fontWeight = FontWeight.Bold)
            Text(text = "Email: ${history.userEmail}")
            Text(text = "Name: ${history.userName}")
            Text(text = "Date: $formattedDate")
            Text(text = "Details: ${history.details}")
        }
    }
}

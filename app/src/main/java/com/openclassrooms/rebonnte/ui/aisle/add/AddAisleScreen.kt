package com.openclassrooms.rebonnte.ui.aisle.add

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
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


@Composable
fun AddAisleScreen(
    modifier: Modifier = Modifier,
    viewModel: AddAisleViewModel = hiltViewModel(),
    onValidate: () -> Unit
) {
    Column(modifier = modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(40.dp,
        Alignment.CenterVertically), horizontalAlignment = Alignment.CenterHorizontally){
        var aisleLocal by remember { mutableStateOf("") }

        TextField(
            value = aisleLocal,
            onValueChange = {aisleLocal = it},
            label = { Text(text = "Aisle name") },
            placeholder = { Text(text = "Enter aisle name") },
            maxLines = 1,
            singleLine = true
        )

        Button(
            onClick = {onValidate(); viewModel.addAisle(aisleLocal) }
        ) { Text(stringResource(R.string.validate)) }
    }
}
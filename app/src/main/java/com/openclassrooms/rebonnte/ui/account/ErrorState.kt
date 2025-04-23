package com.openclassrooms.rebonnte.ui.account

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.openclassrooms.rebonnte.R
import com.openclassrooms.rebonnte.ui.theme.RebonnteTheme

@Composable
fun ErrorState(
    modifier: Modifier = Modifier,
    onTryAgain: () -> Unit
){
    Column(modifier.fillMaxSize().padding(80.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(20.dp, Alignment.CenterVertically)) {
        Box(
            modifier = modifier
                .clip(
                    CircleShape
                )
                .size(64.dp)
                .background(MaterialTheme.colorScheme.onSurfaceVariant),
            contentAlignment = Alignment.Center,

            ) {
            Icon(
                painter = painterResource(id = R.drawable.icon_error),
                contentDescription = stringResource(R.string.error),
                tint = Color.White
            )
        }
        //Centered text
        Text(stringResource(R.string.cannot_access_if_not_logged_in),
            textAlign = TextAlign.Center)
        Button(onClick = {
            onTryAgain()
        }) {
            Text(stringResource(R.string.retry_login))
        }
    }
}


@Preview
@Composable
fun ErrorStatePreview() {
    RebonnteTheme {
        ErrorState(onTryAgain = {})
    }
}
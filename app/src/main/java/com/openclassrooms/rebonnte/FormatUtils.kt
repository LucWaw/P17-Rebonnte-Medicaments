package com.openclassrooms.rebonnte

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
fun formatDateFromMillis(millis: Long, locale: Locale = Locale.FRENCH): String {
    val formatter = DateTimeFormatter.ofPattern("d MMMM yyyy", locale)
    val instant = Instant.ofEpochMilli(millis)
    val date = instant.atZone(ZoneId.systemDefault()).toLocalDate()
    return date.format(formatter)
}
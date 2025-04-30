package com.openclassrooms.rebonnte

import junit.framework.TestCase.assertEquals
import org.junit.Test
import java.util.Locale

class FormatUtilsTest {

    @Test
    fun testFormatDateFromMillis_frenchLocale() {
        val testMillis = 1672531200000 // 1 janvier 2023
        val expectedDate = "1 janvier 2023"
        val actualDate = formatDateFromMillis(testMillis, Locale.FRENCH)
        assertEquals(expectedDate, actualDate)
    }

    @Test
    fun testFormatDateFromMillis_englishLocale() {
        val testMillis = 1672531200000 // 1 January 2023
        val expectedDate = "1 January 2023"
        val actualDate = formatDateFromMillis(testMillis, Locale.ENGLISH)
        assertEquals(expectedDate, actualDate)
    }

    @Test
    fun testFormatDateWithDoubleDigitDayAndMonth() {
        val testMillis = 1699488000000 // 9 novembre 2023
        val expectedDate = "9 novembre 2023"
        val actualDate = formatDateFromMillis(testMillis, Locale.FRENCH)
        assertEquals(expectedDate, actualDate)
    }

    @Test
    fun testFormatDateBeforeEpoch() {
        val testMillis = -315619200000 // 1 janvier 1960
        val expectedDate = "1 janvier 1960"
        val actualDate = formatDateFromMillis(testMillis, Locale.FRENCH)
        assertEquals(expectedDate, actualDate)
    }

    @Test
    fun testFormatDateWithGermanLocale() {
        val testMillis = 1672531200000 // 1 janvier 2023
        val expectedDate = "1 Januar 2023"
        val actualDate = formatDateFromMillis(testMillis, Locale.GERMAN)
        assertEquals(expectedDate, actualDate)
    }

    @Test
    fun testFormatDateWithZeroMillis() {
        val testMillis = 0L // 1 janvier 1970
        val expectedDate = "1 janvier 1970"
        val actualDate = formatDateFromMillis(testMillis, Locale.FRENCH)
        assertEquals(expectedDate, actualDate)
    }
}

package com.openclassrooms.vitesse.utils

import android.icu.text.NumberFormat
import com.openclassrooms.vitesse.ui.utils.Format.convertSalaryToPounds
import com.openclassrooms.vitesse.ui.utils.Format.formatBirthdateForDisplay
import com.openclassrooms.vitesse.ui.utils.Format.formatBirthdateForLocale
import com.openclassrooms.vitesse.ui.utils.Format.formatBirthdateWithAge
import com.openclassrooms.vitesse.ui.utils.Validation
import io.mockk.mockk
import org.junit.Assert.assertFalse
import org.junit.Test
import org.junit.Assert.assertTrue
import org.junit.Before
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale
import kotlin.math.exp
import kotlin.test.assertEquals

class UtilsTest {

    @Test
    fun `get birthdate if valid`() {

        val result = Validation.isBirthdateValid("31/01/2000")

        assertTrue(result)
    }

    @Test
    fun `throws error if birthdate is not valid`() {
        val result = Validation.isBirthdateValid("32/01/2024")

        assertFalse(result)
    }

    @Test
    fun `converts euros to pounds with a gbp rate given`() {
        val euro = 1500
        val gbpRate = 0.89

        val actual = convertSalaryToPounds(euro, gbpRate)
        val expected = euro * gbpRate

        assertEquals(expected, actual)
    }

    @Test
    fun `gets the age of a candidate according to the date of birth given`() {

        val expectedDate = "02/05/2000"
        val expectedAge = Period.between(
            LocalDate.parse("2000-05-02"),
            LocalDate.now()
        ).years
        val actual = formatBirthdateWithAge(expectedDate)
        val expected = expectedDate to expectedAge
        assertEquals(expected, actual)
    }

    @Test
    fun `formats birthdate according to locale`() {
        val birthdate = "02/07/2025"

        val expectedDate = LocalDate.parse(birthdate, DateTimeFormatter.ofPattern("dd/MM/yyyy"))

        val actual = formatBirthdateForLocale(birthdate)
        val expected = formatBirthdateForDisplay(expectedDate)

        assertEquals(expected, actual)
    }

    @Test
    fun `formats birthdate in French locale as ddMMYYYY`() {
        val birthdate = "31/07/2025"
        val date = LocalDate.of(2025, 7, 31)

        val locale = Locale.FRENCH

        val actual = formatBirthdateForDisplay(date, locale)

        assertEquals(birthdate, actual)
    }

    @Test
    fun `formats birthdate in English locale as MMddYYYY`() {
        val birthdate = "07/31/2025"
        val date = LocalDate.of(2025, 7, 31)

        val locale = Locale.ENGLISH

        val actual = formatBirthdateForDisplay(date, locale)

        assertEquals(birthdate, actual)
    }
}
package com.openclassrooms.vitesse.ui.utils

import android.content.Context
import com.openclassrooms.vitesse.R
import android.icu.text.NumberFormat
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.math.roundToInt

object Format {

    fun formatAmount(amount: Double, locale: Locale): String {
        val formatter = NumberFormat.getCurrencyInstance(locale)
        return formatter.format(amount)
    }

    fun formatExpectedSalaryInPounds(context: Context, salary: Int, gbpRate: Double): String {
        val convertedSalary = (salary * gbpRate).let {
            (it * 100).roundToInt() / 100.0
        }

        val gbpSuffix = context.getString(R.string.expected_salary_pounds)
        val formatted = formatAmount(convertedSalary, Locale.UK)

        return "$gbpSuffix $formatted"
    }

    fun formatBirthdateWithAge(context: Context, birthdate: String): String {
        val inputFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        val date = LocalDate.parse(birthdate, inputFormatter)

        val locale = Locale.getDefault()
        val outputPattern = if (locale.language == "en") "MM/dd/yyyy" else "dd/MM/yyyy"
        val outputFormatter = DateTimeFormatter.ofPattern(outputPattern, locale)

        val formattedDate = date.format(outputFormatter)
        val today = LocalDate.now()
        val age = Period.between(date, today).years
        val ageSuffix = context.getString(R.string.age)

        return "$formattedDate ($age $ageSuffix)"
    }

    fun formatBirthdateForLocale(birthdate: String): String {
        val inputFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        val date = LocalDate.parse(birthdate, inputFormatter)

        return formatBirthdateForDisplay(date)
    }

    fun formatBirthdateForDisplay(date: LocalDate, locale: Locale = Locale.getDefault()): String {
        val pattern = if (locale.language == "fr") "dd/MM/yyyy" else "MM/dd/yyyy"
        val formatter = DateTimeFormatter.ofPattern(pattern, locale)
        return date.format(formatter)
    }

    fun formatBirthdateForDatabase(date: LocalDate): String {
        val dbFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        return date.format(dbFormatter)
    }

}

package com.openclassrooms.vitesse.ui.utils

import android.content.Context
import android.os.Build
import android.util.Patterns
import androidx.annotation.RequiresApi
import com.google.android.material.textfield.TextInputLayout
import com.openclassrooms.vitesse.R
import java.text.NumberFormat
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.Locale
import kotlin.math.roundToInt

object Utils {
    fun formatAmountOld(amount: Double, locale: Locale): String {
        val formatter = NumberFormat.getCurrencyInstance(locale)
        return formatter.format(amount)
    }

    fun formatAmountNew(amount: Double, locale: Locale): String {
        val formatter = android.icu.text.NumberFormat.getCurrencyInstance(locale)
        return formatter.format(amount)
    }

    fun formatExpectedSalaryInPounds(context: Context, salary: Double, gbpRate: Double): String {
        val convertedSalary = (salary * gbpRate).let {
            (it * 100).roundToInt() / 100.0
        }

        val gbpSuffix = context.getString(R.string.expected_salary_pounds)
        val formatted = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            formatAmountNew(convertedSalary, Locale.UK)
        } else {
            formatAmountOld(convertedSalary, Locale.UK)
        }

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

        val locale = Locale.getDefault()
        val outputPattern = if (locale.language == "en") "MM/dd/yyyy" else "dd/MM/yyyy"
        val outputFormatter = DateTimeFormatter.ofPattern(outputPattern, locale)

        return date.format(outputFormatter)
    }

    fun validateField(context: Context, value: String, inputLayout: TextInputLayout): Boolean {
        return if (value.isBlank()) {
            inputLayout.error = context.getString(R.string.mandatory_field)
            false
        } else {
            inputLayout.error = null
            true
        }
    }

    fun isEmailValid(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun isPhoneNumberValid(phone: String): Boolean {
        return Patterns.PHONE.matcher(phone).matches()
    }

    fun isBirthdateValid(birthdate: String): Boolean {
        return try {
            val locale = Locale.getDefault()
            val pattern = if (locale.language == "en") "MM/dd/yyyy" else "dd/MM/yyyy"
            val formatter = DateTimeFormatter.ofPattern(pattern, locale)
            LocalDate.parse(birthdate, formatter)
            true
        } catch (e: DateTimeParseException) {
            false
        }
    }

}

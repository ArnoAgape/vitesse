package com.openclassrooms.vitesse.ui.utils

import android.content.Context
import android.util.Patterns
import com.google.android.material.textfield.TextInputLayout
import com.openclassrooms.vitesse.R
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.Locale

object Validation {

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
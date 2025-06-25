package com.openclassrooms.vitesse.ui.utils

import android.content.Context
import android.util.Patterns
import com.google.android.material.textfield.TextInputLayout
import com.openclassrooms.vitesse.R
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

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
        val patterns = listOf("dd/MM/yyyy", "MM/dd/yyyy")

        return patterns.any { pattern ->
            try {
                val formatter = DateTimeFormatter.ofPattern(pattern)
                LocalDate.parse(birthdate, formatter)
                true
            } catch (e: DateTimeParseException) {
                false
            }
        }
    }
}
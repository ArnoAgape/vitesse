package com.openclassrooms.vitesse.ui.utils

import android.util.Patterns
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

object Validation {

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
            } catch (_: DateTimeParseException) {
                false
            }
        }
    }
}
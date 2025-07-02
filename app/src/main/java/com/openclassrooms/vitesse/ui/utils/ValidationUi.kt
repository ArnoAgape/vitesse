package com.openclassrooms.vitesse.ui.utils

import android.content.Context
import com.google.android.material.textfield.TextInputLayout
import com.openclassrooms.vitesse.R

object ValidationUi {

    fun validateField(context: Context, value: String, inputLayout: TextInputLayout): Boolean {
        return if (value.isBlank()) {
            inputLayout.error = context.getString(R.string.mandatory_field)
            false
        } else {
            inputLayout.error = null
            true
        }
    }
}
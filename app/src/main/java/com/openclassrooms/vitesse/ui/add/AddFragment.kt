package com.openclassrooms.vitesse.ui.add

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.fragment.app.Fragment
import com.google.android.material.textfield.TextInputLayout
import com.openclassrooms.vitesse.R
import com.openclassrooms.vitesse.databinding.AddScreenBinding
import com.openclassrooms.vitesse.domain.model.Candidate
import com.openclassrooms.vitesse.ui.home.HomeFragment
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.Calendar
import kotlin.getValue

@AndroidEntryPoint
class AddFragment : Fragment() {

    private lateinit var binding: AddScreenBinding
    private val viewModel: AddViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = AddScreenBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupSave()
        setupToolbar()
    }

    private fun setupSave() {

        binding.birthdateEdit.setOnClickListener {
            showDatePicker()
        }

        binding.saveButton.setOnClickListener {

            val newFirstName = binding.firstNameEdit.text.toString()
            val newLastName = binding.lastNameEdit.text.toString()
            val newPhone = binding.phoneEdit.text.toString()
            val newEmail = binding.emailEdit.text.toString()
            val newBirthdate = binding.birthdateEdit.text.toString()
            val newSalaryText = binding.salaryEdit.text.toString()
            val newSalary = newSalaryText.toDoubleOrNull() ?: 0.0
            val newNotes = binding.notesEdit.text.toString()

            val isFirstNameValid = validateField(newFirstName, binding.firstName)
            val isLastNameValid = validateField(newLastName, binding.lastName)
            val isPhoneValid = validateField(newPhone, binding.phone)
            val isEmailValid = if (newEmail.isBlank()) {
                binding.email.error = getString(R.string.mandatory_field)
                false
            } else if (!isEmailValid(newEmail)) {
                binding.email.error = getString(R.string.invalid_format)
                false
            } else {
                binding.email.error = null
                true
            }
            val isBirthdateValid = if (newBirthdate.isBlank()) {
                binding.birthdate.error = getString(R.string.mandatory_field)
                false
            } else if (!isDateValid(newBirthdate)) {
                binding.birthdate.error = getString(R.string.invalid_format)
                false
            } else {
                binding.birthdate.error = null
                true
            }

            if (!isFirstNameValid || !isLastNameValid || !isPhoneValid || !isEmailValid || !isBirthdateValid)
                return@setOnClickListener

            val newCandidate = Candidate(
                System.currentTimeMillis(),
                newFirstName,
                newLastName,
                newPhone,
                newEmail,
                newBirthdate,
                newSalary,
                newNotes
            )
            viewModel.addCandidate(newCandidate)
            parentFragmentManager
                .beginTransaction()
                .replace(R.id.container, HomeFragment())
                .addToBackStack(null)
                .commit()
            Toast.makeText(context, R.string.added, Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    private fun validateField(value: String, inputLayout: TextInputLayout): Boolean {
        return if (value.isBlank()) {
            inputLayout.error = getString(R.string.mandatory_field)
            false
        } else {
            inputLayout.error = null
            true
        }
    }

    private fun isEmailValid(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun isDateValid(date: String): Boolean {
        return try {
            val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
            val birthDate = LocalDate.parse(date, formatter)
            birthDate.isBefore(LocalDate.now())
        } catch (e: DateTimeParseException) {
            false
        }
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePicker = DatePickerDialog(requireContext(), { _, selectedYear, selectedMonth, selectedDay ->
            val formattedDate = String.format("%02d/%02d/%04d", selectedDay, selectedMonth + 1, selectedYear)
            binding.birthdateEdit.setText(formattedDate)
        }, year, month, day)

        datePicker.show()
    }


}


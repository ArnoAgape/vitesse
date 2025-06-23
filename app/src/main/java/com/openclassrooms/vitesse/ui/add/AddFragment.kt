package com.openclassrooms.vitesse.ui.add

import android.app.DatePickerDialog
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia
import androidx.annotation.RequiresApi
import androidx.fragment.app.viewModels
import androidx.fragment.app.Fragment
import com.google.android.material.textfield.TextInputLayout
import com.openclassrooms.vitesse.R
import com.openclassrooms.vitesse.databinding.AddScreenBinding
import com.openclassrooms.vitesse.domain.model.Candidate
import com.openclassrooms.vitesse.ui.home.HomeFragment
import com.openclassrooms.vitesse.ui.utils.Utils
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale
import kotlin.getValue

@AndroidEntryPoint
class AddFragment : Fragment() {

    private lateinit var binding: AddScreenBinding
    private val viewModel: AddViewModel by viewModels()

    private var selectedImageUri: Uri? = null
    val pickMedia = registerForActivityResult(PickVisualMedia()) { uri ->
        if (uri != null) {
            selectedImageUri = uri
            binding.profilePicture.setImageURI(uri)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = AddScreenBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupSave()
        setupToolbar()

        binding.profilePicture.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(PickVisualMedia.ImageOnly))
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setupSave() {

        binding.birthdateEdit.setOnClickListener {
            showDatePicker()
        }

        binding.saveButton.setOnClickListener {

            val newFirstName = binding.firstNameEdit.text.toString()
            val newLastName = binding.lastNameEdit.text.toString()
            val newPhone = binding.phoneEdit.text.toString()
            val newEmail = binding.emailEdit.text.toString()
            val newBirthdate = viewModel.getBirthdateForDb()
            val newSalaryText = binding.salaryEdit.text.toString()
            val newSalary = newSalaryText.toDoubleOrNull() ?: 0.0
            val newNotes = binding.notesEdit.text.toString()
            val newProfilePicture = selectedImageUri.toString()

            val isFirstNameValid = validateField(newFirstName, binding.firstName)

            val isLastNameValid = validateField(newLastName, binding.lastName)

            val isPhoneValid = if (newPhone.isBlank()) {
                binding.phone.error = getString(R.string.mandatory_field)
                false
            } else if (!isPhoneNumberValid(newPhone)) {
                binding.phone.error = getString(R.string.invalid_format)
                false
            } else {
                binding.phone.error = null
                true
            }

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
            } else if (!Utils.isBirthdateValid(newBirthdate)) {
                binding.birthdate.error = getString(R.string.invalid_format)
                false
            } else {
                binding.birthdate.error = null
                true
            }

            if (!isFirstNameValid || !isLastNameValid || !isPhoneValid || !isEmailValid || !isBirthdateValid)
                return@setOnClickListener

            val newCandidate = Candidate(
                id = null,
                firstname = newFirstName,
                lastname = newLastName,
                phone = newPhone,
                email = newEmail,
                birthdate = newBirthdate,
                salary = newSalary,
                notes = newNotes,
                profilePicture = newProfilePicture
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
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun isPhoneNumberValid(phone: String): Boolean {
        return Patterns.PHONE.matcher(phone).matches()
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePicker =
            DatePickerDialog(requireContext(), { _, selectedYear, selectedMonth, selectedDay ->
                val selectedDate = LocalDate.of(selectedYear, selectedMonth + 1, selectedDay)
                val locale = Locale.getDefault()

                val pattern = if (locale.language == "fr") "dd/MM/yyyy" else "MM/dd/yyyy"
                val formatter = DateTimeFormatter.ofPattern(pattern, locale)

                val formattedDate = selectedDate.format(formatter)
                binding.birthdateEdit.setText(formattedDate)

                val dbFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
                val dbFormattedDate = selectedDate.format(dbFormatter)

                viewModel.setBirthdateForDb(dbFormattedDate)

            }, year, month, day)

        datePicker.show()
    }

}


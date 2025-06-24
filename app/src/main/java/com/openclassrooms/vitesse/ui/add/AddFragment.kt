package com.openclassrooms.vitesse.ui.add

import android.app.DatePickerDialog
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia
import androidx.fragment.app.viewModels
import androidx.fragment.app.Fragment
import com.openclassrooms.vitesse.R
import com.openclassrooms.vitesse.databinding.AddScreenBinding
import com.openclassrooms.vitesse.domain.model.Candidate
import com.openclassrooms.vitesse.ui.home.HomeFragment
import com.openclassrooms.vitesse.ui.utils.Utils
import com.openclassrooms.vitesse.ui.utils.Validation
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDate
import java.util.Calendar
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupSave()
        setupToolbar()

        binding.profilePicture.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(PickVisualMedia.ImageOnly))
        }
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
            val newBirthdate = viewModel.getBirthdateForDb()
            val newSalaryText = binding.salaryEdit.text.toString()
            val newSalary = newSalaryText.toDoubleOrNull() ?: 0.0
            val newNotes = binding.notesEdit.text.toString()
            val newProfilePicture = selectedImageUri.toString()

            val isFirstNameValid = Validation.validateField(requireContext(), newFirstName, binding.firstName)

            val isLastNameValid = Validation.validateField(requireContext(), newLastName, binding.lastName)

            val isPhoneValid = if (newPhone.isBlank()) {
                binding.phone.error = getString(R.string.mandatory_field)
                false
            } else if (!Validation.isPhoneNumberValid(newPhone)) {
                binding.phone.error = getString(R.string.invalid_format)
                false
            } else {
                binding.phone.error = null
                true
            }

            val isEmailValid = if (newEmail.isBlank()) {
                binding.email.error = getString(R.string.mandatory_field)
                false
            } else if (!Validation.isEmailValid(newEmail)) {
                binding.email.error = getString(R.string.invalid_format)
                false
            } else {
                binding.email.error = null
                true
            }

            val isBirthdateValid = if (newBirthdate.isBlank()) {
                binding.birthdate.error = getString(R.string.mandatory_field)
                false
            } else if (!Validation.isBirthdateValid(newBirthdate)) {
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

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePicker =
            DatePickerDialog(requireContext(), { _, selectedYear, selectedMonth, selectedDay ->
                val selectedDate = LocalDate.of(selectedYear, selectedMonth + 1, selectedDay)

                val formattedDate = Utils.formatBirthdateForDisplay(selectedDate)
                binding.birthdateEdit.setText(formattedDate)

                val dbFormattedDate = Utils.formatBirthdateForDatabase(selectedDate)
                viewModel.setBirthdateForDb(dbFormattedDate)

            }, year, month, day)

        datePicker.show()
    }

}


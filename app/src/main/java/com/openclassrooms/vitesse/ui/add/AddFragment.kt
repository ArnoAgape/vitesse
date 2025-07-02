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
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.openclassrooms.vitesse.R
import com.openclassrooms.vitesse.databinding.AddScreenBinding
import com.openclassrooms.vitesse.domain.model.Candidate
import com.openclassrooms.vitesse.ui.home.HomeFragment
import com.openclassrooms.vitesse.ui.utils.Format
import com.openclassrooms.vitesse.ui.utils.Validation
import com.openclassrooms.vitesse.ui.utils.ValidationUi
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.Calendar
import kotlin.getValue

/**
 * Fragment responsible for adding a new candidate.
 */
@AndroidEntryPoint
class AddFragment : Fragment() {

    private lateinit var binding: AddScreenBinding
    private val viewModel: AddViewModel by viewModels()

    private var selectedImageUri: Uri? = null

    // Media picker to select profile picture from gallery
    private val pickMedia = registerForActivityResult(PickVisualMedia()) { uri ->
        uri?.let {
            selectedImageUri = it
            binding.profilePicture.setImageURI(it)
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
        setupToolbar()
        setupSaveButton()
        setupProfilePicturePicker()
        observeErrors()
    }

    // region Setup

    /**
     * Handles the errors.
     */
    private fun observeErrors() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.errorFlow.collect { errorMessage ->
                    errorMessage?.let {
                        Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    /**
     * Handles the toolbar navigation click.
     */
    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    /**
     * Sets up the profile picture click to open media picker.
     */
    private fun setupProfilePicturePicker() {
        binding.profilePicture.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(PickVisualMedia.ImageOnly))
        }
    }

    /**
     * Handles the save button click, input validation and candidate creation.
     */
    private fun setupSaveButton() {
        binding.birthdateEdit.setOnClickListener {
            showDatePicker()
        }

        binding.saveButton.setOnClickListener {
            val newFirstName = binding.firstNameEdit.text.toString().trim()
            val newLastName = binding.lastNameEdit.text.toString().trim()
            val newPhone = binding.phoneEdit.text.toString().trim()
            val newEmail = binding.emailEdit.text.toString().trim()
            val newBirthdate = viewModel.getBirthdateForDb()
            val newSalary = binding.salaryEdit.text.toString().trim().toIntOrNull() ?: 0
            val newNotes = binding.notesEdit.text.toString().trim()
            val newProfilePicture = selectedImageUri.toString()

            val isFirstNameValid =
                ValidationUi.validateField(requireContext(), newFirstName, binding.firstName)
            val isLastNameValid =
                ValidationUi.validateField(requireContext(), newLastName, binding.lastName)

            val isPhoneValid = when {
                newPhone.isBlank() -> {
                    binding.phone.error = getString(R.string.mandatory_field)
                    false
                }

                !Validation.isPhoneNumberValid(newPhone) -> {
                    binding.phone.error = getString(R.string.invalid_format)
                    false
                }

                else -> {
                    binding.phone.error = null
                    true
                }
            }

            val isEmailValid = when {
                newEmail.isBlank() -> {
                    binding.email.error = getString(R.string.mandatory_field)
                    false
                }

                !Validation.isEmailValid(newEmail) -> {
                    binding.email.error = getString(R.string.invalid_format)
                    false
                }

                else -> {
                    binding.email.error = null
                    true
                }
            }

            val isBirthdateValid = when {
                newBirthdate.isBlank() -> {
                    binding.birthdate.error = getString(R.string.mandatory_field)
                    false
                }

                !Validation.isBirthdateValid(newBirthdate) -> {
                    binding.birthdate.error = getString(R.string.invalid_format)
                    false
                }

                else -> {
                    binding.birthdate.error = null
                    true
                }
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

            parentFragmentManager.beginTransaction()
                .replace(R.id.container, HomeFragment())
                .addToBackStack(null)
                .commit()

            Toast.makeText(context, R.string.added, Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Shows a date picker dialog and formats selected date for UI and database.
     */
    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePicker =
            DatePickerDialog(requireContext(), { _, selectedYear, selectedMonth, selectedDay ->
                val selectedDate = LocalDate.of(selectedYear, selectedMonth + 1, selectedDay)
                val displayDate = Format.formatBirthdateForDisplay(selectedDate)
                val dbDate = Format.formatBirthdateForDatabase(selectedDate)

                binding.birthdateEdit.setText(displayDate)
                viewModel.setBirthdateForDb(dbDate)
            }, year, month, day)

        datePicker.datePicker.maxDate = System.currentTimeMillis()
        datePicker.show()
    }

    // endregion
}
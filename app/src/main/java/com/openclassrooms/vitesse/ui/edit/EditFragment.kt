package com.openclassrooms.vitesse.ui.edit

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
import com.bumptech.glide.Glide
import com.openclassrooms.vitesse.R
import com.openclassrooms.vitesse.databinding.EditScreenBinding
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
 * Fragment responsible for editing an existing candidate.
 * Allows the user to update all candidate details including the profile picture.
 */
@AndroidEntryPoint
class EditFragment : Fragment() {

    private lateinit var binding: EditScreenBinding
    private val viewModel: EditViewModel by viewModels()

    private var selectedImageUri: Uri? = null

    /**
     * Registers the image picker to select a new profile picture.
     */
    val pickMedia = registerForActivityResult(PickVisualMedia()) { uri ->
        uri?.let {
            selectedImageUri = it
            binding.profilePictureEdit.setImageURI(it)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = EditScreenBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val candidateId = requireArguments().getLong(ARG_CANDIDATE_ID)
        viewModel.getCandidateById(candidateId)

        setupListeners()
        setupToolbar()
        observeCandidate()
        observeErrors()
    }

    /**
     * Sets up listeners for views, including image and birthdate pickers.
     */
    private fun setupListeners() {
        binding.profilePictureEdit.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(PickVisualMedia.ImageOnly))
        }

        binding.birthdateEdit.setOnClickListener {
            showDatePicker()
        }
    }

    /**
     * Observes potential errors and displays them as toasts.
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
     * Observes the candidate data and updates the UI accordingly.
     */
    private fun observeCandidate() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.candidateFlow.collect { candidateResult ->
                        candidateResult?.let {
                            binding.firstNameEdit.setText(it.firstname)
                            binding.lastNameEdit.setText(it.lastname)
                            binding.phoneEdit.setText(it.phone)
                            binding.emailEdit.setText(it.email)
                            binding.birthdateEdit.setText(Format.formatBirthdateForLocale(it.birthdate))
                            binding.salaryEdit.setText(it.salary.toString())
                            binding.notesEdit.setText(it.notes)
                            Glide.with(binding.root.context)
                                .load(it.profilePicture)
                                .placeholder(R.drawable.ic_profile_pic)
                                .error(R.drawable.ic_profile_pic)
                                .into(binding.profilePictureEdit)
                        }
                        setupSave()
                    }
                }
                launch {
                    viewModel.errorFlow.collect { errorMessage ->
                        errorMessage?.let {
                            Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
                        }
                    }
                }

            }
        }
    }

    /**
     * Sets up the Save button logic, including form validation and data submission.
     */
    private fun setupSave() {
        binding.saveButton.setOnClickListener {
            val currentCandidate = viewModel.candidateFlow.value ?: return@setOnClickListener

            val newFirstName = binding.firstNameEdit.text.toString().trim()
            val newLastName = binding.lastNameEdit.text.toString().trim()
            val newPhone = binding.phoneEdit.text.toString().trim()
            val newEmail = binding.emailEdit.text.toString().trim()
            val newBirthdate = viewModel.getBirthdateForDb().takeIf {
                it.isNotBlank() } ?: currentCandidate.birthdate
            val newSalary = binding.salaryEdit.text.toString().trim().toIntOrNull() ?: 0
            val newNotes = binding.notesEdit.text.toString().trim()
            val newProfilePicture = selectedImageUri?.toString() ?: currentCandidate.profilePicture

            val isFirstNameValid =
                ValidationUi.validateField(requireContext(), newFirstName, binding.firstName)
            val isLastNameValid =
                ValidationUi.validateField(requireContext(), newLastName, binding.lastName)
            val isPhoneValid = validatePhone(newPhone)
            val isEmailValid = validateEmail(newEmail)
            val isBirthdateValid = validateBirthdate(newBirthdate)

            if (!isFirstNameValid || !isLastNameValid || !isPhoneValid || !isEmailValid || !isBirthdateValid)
                return@setOnClickListener

            val updatedCandidate = currentCandidate.copy(
                firstname = newFirstName,
                lastname = newLastName,
                phone = newPhone,
                email = newEmail,
                birthdate = newBirthdate,
                salary = newSalary,
                notes = newNotes,
                profilePicture = newProfilePicture
            )

            viewModel.updateCandidate(updatedCandidate)

            parentFragmentManager.beginTransaction()
                .replace(R.id.container, HomeFragment())
                .addToBackStack(null)
                .commit()

            Toast.makeText(context, R.string.edited, Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Validates the phone number input.
     */
    private fun validatePhone(phone: String): Boolean {
        return when {
            phone.isBlank() -> {
                binding.phone.error = getString(R.string.mandatory_field)
                false
            }

            !Validation.isPhoneNumberValid(phone) -> {
                binding.phone.error = getString(R.string.invalid_format)
                false
            }

            else -> {
                binding.phone.error = null
                true
            }
        }
    }

    /**
     * Validates the email input.
     */
    private fun validateEmail(email: String): Boolean {
        return when {
            email.isBlank() -> {
                binding.email.error = getString(R.string.mandatory_field)
                false
            }

            !Validation.isEmailValid(email) -> {
                binding.email.error = getString(R.string.invalid_format)
                false
            }

            else -> {
                binding.email.error = null
                true
            }
        }
    }

    /**
     * Validates the birthdate input.
     */
    private fun validateBirthdate(birthdate: String): Boolean {
        return when {
            birthdate.isBlank() -> {
                binding.birthdate.error = getString(R.string.mandatory_field)
                false
            }

            !Validation.isBirthdateValid(birthdate) -> {
                binding.birthdate.error = getString(R.string.invalid_format)
                false
            }

            else -> {
                binding.birthdate.error = null
                true
            }
        }
    }

    /**
     * Displays a DatePickerDialog to select a birthdate.
     */
    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePicker = DatePickerDialog(requireContext(), { _, y, m, d ->
            val selectedDate = LocalDate.of(y, m + 1, d)
            binding.birthdateEdit.setText(Format.formatBirthdateForDisplay(selectedDate))
            viewModel.setBirthdateForDb(Format.formatBirthdateForDatabase(selectedDate))
        }, year, month, day)

        datePicker.datePicker.maxDate = System.currentTimeMillis()
        datePicker.show()
    }

    /**
     * Sets up the toolbar back button.
     */
    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    companion object {
        private const val ARG_CANDIDATE_ID = "candidate_id"

        /**
         * Creates a new instance of [EditFragment] with the provided candidate ID.
         */
        fun newInstance(id: Long): EditFragment {
            return EditFragment().apply {
                arguments = Bundle().apply {
                    putLong(ARG_CANDIDATE_ID, id)
                }
            }
        }
    }
}
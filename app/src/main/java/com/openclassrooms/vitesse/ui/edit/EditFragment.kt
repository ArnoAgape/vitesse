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
import com.openclassrooms.vitesse.domain.model.Candidate
import com.openclassrooms.vitesse.ui.home.HomeFragment
import com.openclassrooms.vitesse.ui.utils.Format
import com.openclassrooms.vitesse.ui.utils.Validation
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.Calendar
import kotlin.getValue

@AndroidEntryPoint
class EditFragment : Fragment() {

    private var candidateId: Long = -1L
    private lateinit var candidate: Candidate
    private lateinit var binding: EditScreenBinding
    private val viewModel: EditViewModel by viewModels()

    private var selectedImageUri: Uri? = null
    val pickMedia = registerForActivityResult(PickVisualMedia()) { uri ->
        if (uri != null) {
            selectedImageUri = uri
            binding.profilePictureEdit.setImageURI(uri)
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
        candidateId = requireArguments().getLong("candidate_id")

        val candidateId = requireArguments().getLong("candidate_id")
        viewModel.getCandidateById(candidateId)

        binding.profilePictureEdit.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(PickVisualMedia.ImageOnly))
        }

        setupSave()
        setupToolbar()
        observeCandidate()
    }

    private fun observeCandidate() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.candidateFlow.collect { candidateResult ->
                        candidateResult?.let {
                            candidate = it
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
            val newProfilePicture = selectedImageUri?.toString() ?: candidate.profilePicture

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

            val updatedCandidate = Candidate(
                candidateId,
                newFirstName,
                newLastName,
                newPhone,
                newEmail,
                newBirthdate,
                newSalary,
                newNotes,
                newProfilePicture
            )
            viewModel.updateCandidate(updatedCandidate)
            parentFragmentManager
                .beginTransaction()
                .replace(R.id.container, HomeFragment())
                .addToBackStack(null)
                .commit()
            Toast.makeText(context, R.string.edited, Toast.LENGTH_SHORT).show()
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

                val formattedDate = Format.formatBirthdateForDisplay(selectedDate)
                binding.birthdateEdit.setText(formattedDate)

                val dbFormattedDate = Format.formatBirthdateForDatabase(selectedDate)
                viewModel.setBirthdateForDb(dbFormattedDate)

            }, year, month, day)

        datePicker.show()
    }

    companion object {
        private const val ARG_CANDIDATE_ID = "candidate_id"

        fun newInstance(id: Long): EditFragment {
            val fragment = EditFragment()
            val args = Bundle()
            args.putLong(ARG_CANDIDATE_ID, id)
            fragment.arguments = args
            return fragment
        }
    }

}


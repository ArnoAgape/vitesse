package com.openclassrooms.vitesse.ui.add

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.fragment.app.Fragment
import com.openclassrooms.vitesse.R
import com.openclassrooms.vitesse.databinding.AddScreenBinding
import com.openclassrooms.vitesse.domain.model.Candidate
import com.openclassrooms.vitesse.ui.home.HomeFragment
import dagger.hilt.android.AndroidEntryPoint
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
        binding.saveButton.setOnClickListener {
            val newFirstname = binding.firstNameEdit.text.toString()
            val newLastname = binding.lastNameEdit.text.toString()
            val newPhone = binding.phoneEdit.text.toString()
            val newEmail = binding.emailEdit.text.toString()
            val newBirthdate = binding.birthdateEdit.text.toString()
            val newSalaryText = binding.salaryEdit.text.toString()
            val newSalary = newSalaryText.toDouble()
            val newNotes = binding.notesEdit.text.toString()
            val newCandidate = Candidate(
                System.currentTimeMillis(),
                newFirstname,
                newLastname,
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

}


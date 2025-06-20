package com.openclassrooms.vitesse.ui.detail

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.viewModels
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import com.openclassrooms.vitesse.R
import com.openclassrooms.vitesse.databinding.DetailScreenBinding
import com.openclassrooms.vitesse.domain.model.Candidate
import dagger.hilt.android.AndroidEntryPoint
import kotlin.getValue
import androidx.core.net.toUri
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bumptech.glide.Glide
import com.openclassrooms.vitesse.ui.edit.EditFragment
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter

@AndroidEntryPoint
class DetailFragment : Fragment() {

    private var candidateId: Long = -1L
    private lateinit var candidate: Candidate
    private lateinit var binding: DetailScreenBinding
    private val viewModel: DetailViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DetailScreenBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        candidateId = arguments?.getLong(ARG_CANDIDATE_ID) ?: -1L

        viewModel.getCandidateById(candidateId)

        (requireActivity() as MenuHost).addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.edit_candidate, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.edit -> {
                        setupEditButton()
                        true
                    }

                    R.id.delete -> {
                        showDeleteConfirmationDialog()
                        true
                    }

                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)

        observeCandidate()
    }

    private fun observeCandidate() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.candidateFlow.collect { candidateResult ->
                        candidateResult?.let {
                            candidate = it
                            binding.phoneContainer.setOnClickListener { dialPhoneNumber(candidate.phone) }
                            binding.message.setOnClickListener { sendSms(candidate.phone) }
                            binding.email.setOnClickListener { sendEmail(candidate.email) }
                            binding.birthdateEdit.text = formatBirthdateWithAge(candidate.birthdate)
                            binding.salaryEdit.text = String.format("%s €", candidate.salary.toString())
                            binding.notesEdit.text = candidate.notes
                            binding.salaryConverted.text
                            Glide.with(binding.root.context)
                                .load(candidate.profilePicture)
                                .placeholder(R.drawable.ic_profile_pic)
                                .error(R.drawable.ic_profile_pic)
                                .into(binding.profilePicture)
                            setupToolbar(candidate)
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

    private fun showDeleteConfirmationDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.deletion)
            .setMessage(R.string.sure_delete)
            .setPositiveButton(R.string.confirm) { dialog, _ ->
                viewModel.deleteCandidate(candidate)
                parentFragmentManager.popBackStack()

                Toast.makeText(context, R.string.deleted, Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }
            .setNegativeButton(R.string.cancel) { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }

    private fun setupEditButton() {
        parentFragmentManager
            .beginTransaction()
            .replace(R.id.container, EditFragment())
            .addToBackStack(null)
            .commit()
    }

    private fun setupToolbar(candidate: Candidate) {
        val toolbar = binding.toolbar
        (requireActivity() as AppCompatActivity).setSupportActionBar(toolbar)

        val fullName = "${candidate.firstname} ${candidate.lastname}"
        (requireActivity() as AppCompatActivity).supportActionBar?.title = fullName
        toolbar.setNavigationOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    fun formatBirthdateWithAge(birthdate: String): String {
        val inputFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        val date = LocalDate.parse(birthdate, inputFormatter)

        val today = LocalDate.now()
        val age = Period.between(date, today).years
        val old = getString(R.string.age)

        return "$birthdate (${age} $old)"
    }

    private fun dialPhoneNumber(phoneNumber: String) {
        val intent = Intent(Intent.ACTION_DIAL).apply {
            data = "tel:$phoneNumber".toUri()
        }
        if (intent.resolveActivity(requireActivity().packageManager) != null) {
            startActivity(intent)
        } else {
            Toast.makeText(requireContext(), R.string.no_phone, Toast.LENGTH_SHORT).show()
        }
    }

    private fun sendSms(phoneNumber: String, message: String = "") {
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = "smsto:$phoneNumber".toUri()
            putExtra("sms_body", message)
        }
        if (intent.resolveActivity(requireActivity().packageManager) != null) {
            startActivity(intent)
        } else {
            Toast.makeText(requireContext(), R.string.no_phone, Toast.LENGTH_SHORT).show()
        }
    }

    private fun sendEmail(emailAddress: String, subject: String = "", body: String = "") {
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = "mailto:$emailAddress".toUri()
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, body)
        }
        if (intent.resolveActivity(requireActivity().packageManager) != null) {
            startActivity(intent)
        } else {
            Toast.makeText(requireContext(), R.string.no_email, Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        private const val ARG_CANDIDATE_ID = "candidate_id"

        fun newInstance(id: Long): DetailFragment {
            val fragment = DetailFragment()
            val args = Bundle()
            args.putLong(ARG_CANDIDATE_ID, id)
            fragment.arguments = args
            return fragment
        }
    }

}



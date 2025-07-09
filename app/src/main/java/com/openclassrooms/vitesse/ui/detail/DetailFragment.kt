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
import com.openclassrooms.vitesse.ui.utils.Format
import kotlinx.coroutines.launch
import java.util.Locale

/**
 * Detail screen fragment displaying the details of a candidate.
 */
@AndroidEntryPoint
class DetailFragment : Fragment() {

    private var favoriteMenuItem: MenuItem? = null
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

        val id = arguments?.getLong(ARG_CANDIDATE_ID) ?: return

        viewModel.getEuroConverted()
        viewModel.getCandidateById(id)

        setupMenu()
        observeViewModel()
    }

    /**
     * Sets up the top app bar menu with favorite, edit, and delete actions.
     */
    private fun setupMenu() {
        (requireActivity() as MenuHost).addMenuProvider(
            buildMenuProvider(),
            viewLifecycleOwner,
            Lifecycle.State.RESUMED
        )
    }

    /**
     * Builds the menu provider handling menu creation and item selection.
     */
    private fun buildMenuProvider() = object : MenuProvider {
        override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
            menuInflater.inflate(R.menu.edit_candidate, menu)
            favoriteMenuItem = menu.findItem(R.id.favorite)
            updateStarIcon()
        }

        override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
            return when (menuItem.itemId) {
                R.id.favorite -> {
                    viewModel.toggleFavorite()
                    true
                }

                R.id.edit -> {
                    navigateToEditScreen()
                    true
                }

                R.id.delete -> {
                    showDeleteConfirmationDialog()
                    true
                }

                else -> false
            }
        }
    }

    /**
     * Observes UI flows from the ViewModel.
     */
    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.uiState.collect { state ->
                        val candidate = state.candidate
                        val gbp = state.result

                        if (candidate != null) {
                            renderDetails(candidate)
                            updateStarIcon()

                            if (gbp != null) {
                                val salaryInPounds = Format.convertSalaryToPounds(candidate.salary, gbp)
                                val formatted = Format.formatAmount(salaryInPounds, Locale.UK)
                                binding.salaryConverted.text =
                                    getString(R.string.expected_salary_pounds, formatted)
                            }
                        }
                    }
                }
                launch { collectErrors() }
            }
        }
    }


    /**
     * Collects error messages from the ViewModel and displays appropriate messages to the user.
     */
    private suspend fun collectErrors() {
        viewModel.errorFlow.collect { errorMessage ->
            errorMessage?.let {
                val isNetworkError = it.contains("Unable to resolve host", ignoreCase = true)
                        || it.contains("timeout", ignoreCase = true)
                        || it.contains("Failed to connect", ignoreCase = true)
                        || it.contains("No address associated with hostname", ignoreCase = true)
                val displayedMessage = if (isNetworkError) {
                    getString(R.string.no_network)
                } else {
                    it
                }
                Toast.makeText(requireContext(), displayedMessage, Toast.LENGTH_LONG).show()
            }
        }
    }

    /**
     * Populates the screen with candidate data.
     */
    private fun renderDetails(candidate: Candidate) = with(binding) {
        phoneContainer.setOnClickListener { dialPhoneNumber(candidate.phone) }
        message.setOnClickListener { sendSms(candidate.phone) }
        email.setOnClickListener { sendEmail(candidate.email) }

        val (formatted, age) = Format.formatBirthdateWithAge(candidate.birthdate)
        birthdateEdit.text = getString(R.string.age, formatted, age)
        salaryEdit.text = String.format("%s €", candidate.salary.toString())
        notesEdit.text = candidate.notes

        Glide.with(root.context)
            .load(candidate.profilePicture)
            .placeholder(R.drawable.ic_profile_pic)
            .error(R.drawable.ic_profile_pic)
            .into(profilePicture)

        val toolbar = binding.toolbar
        (requireActivity() as AppCompatActivity).setSupportActionBar(toolbar)

        (requireActivity() as AppCompatActivity).supportActionBar?.title =
            "${candidate.firstname} ${candidate.lastname}"

        toolbar.setNavigationOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    /**
     * Updates the star icon in the top app bar based on the current candidate's favorite status.
     */
    private fun updateStarIcon() {
        val isFavorite = viewModel.uiState.value.candidate?.isFavorite ?: return
        favoriteMenuItem?.setIcon(
            if (isFavorite) R.drawable.ic_star_filled else R.drawable.ic_star_border
        )
    }

    /**
     * Navigates to the EditFragment with current candidate ID.
     */
    private fun navigateToEditScreen() {
        val id = arguments?.getLong(ARG_CANDIDATE_ID) ?: return
        val fragment = EditFragment.newInstance(id)
        parentFragmentManager.beginTransaction()
            .replace(R.id.container, fragment)
            .addToBackStack(null)
            .commit()
    }

    /**
     * Displays confirmation dialog before deleting a candidate.
     */
    private fun showDeleteConfirmationDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.deletion)
            .setMessage(R.string.sure_delete)
            .setPositiveButton(R.string.confirm) { dialog, _ ->
                viewModel.deleteCandidate()
                parentFragmentManager.popBackStack()
                Toast.makeText(context, R.string.deleted, Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }
            .setNegativeButton(R.string.cancel) { dialog, _ -> dialog.dismiss() }
            .create()
            .show()
    }

    /**
     * Launches the dialer app with the candidate's phone number.
     *
     * @param phoneNumber The phone number to dial.
     */
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

    /**
     * Sends an SMS to the given phone number.
     *
     * @param phoneNumber The recipient's phone number.
     * @param message Optional message body to pre-fill.
     */
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

    /**
     * Sends an email to the given address.
     *
     * @param emailAddress The recipient's email address.
     * @param subject Optional email subject.
     * @param body Optional email body text.
     */
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

        /**
         * Factory method to create a new instance of DetailFragment.
         * @param id ID of the candidate to display.
         */
        fun newInstance(id: Long): DetailFragment {
            return DetailFragment().apply {
                arguments = Bundle().apply {
                    putLong(ARG_CANDIDATE_ID, id)
                }
            }
        }
    }
}
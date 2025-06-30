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

/**
 * Detail screen fragment displaying the details of a candidate.
 */
@AndroidEntryPoint
class DetailFragment : Fragment() {

    private var isFavorite: Boolean = false
    private var candidateId: Long = -1L
    private lateinit var candidate: Candidate
    private lateinit var binding: DetailScreenBinding

    private val viewModel: DetailViewModel by viewModels()

    override fun onResume() {
        super.onResume()
        candidateId.let { viewModel.getCandidateById(it) }
    }

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

        viewModel.getEuroConverted()
        viewModel.getCandidateById(candidateId)

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
            updateStarIcon(menu.findItem(R.id.favorite))
        }

        override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
            return when (menuItem.itemId) {
                R.id.favorite -> {
                    isFavorite = !isFavorite
                    updateStarIcon(menuItem)
                    viewModel.toggleFavorite(candidateId, isFavorite)
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
                launch { collectCandidate() }
                launch { collectErrors() }
                launch { collectCurrencyRate() }
            }
        }
    }

    /**
     * Collects candidate details from the ViewModel and binds them to the UI.
     */
    private suspend fun collectCandidate() {
        viewModel.candidateFlow.collect { candidateResult ->
            candidateResult?.let {
                candidate = it
                bindCandidateDetails(candidate)
                setupToolbar(candidate)
                isFavorite = candidate.isFavorite
                candidateId = candidate.id!!
                requireActivity().invalidateOptionsMenu()
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
     * Collects currency conversion rate and updates the salary in pounds.
     */
    private suspend fun collectCurrencyRate() {
        viewModel.gbpFlow.collect { gbpRate ->
            gbpRate?.let {
                binding.salaryConverted.text = Format.formatExpectedSalaryInPounds(
                    requireContext(),
                    candidate.salary,
                    gbpRate
                )
            }
        }
    }

    /**
     * Populates the screen with candidate data.
     */
    private fun bindCandidateDetails(candidate: Candidate) = with(binding) {
        phoneContainer.setOnClickListener { dialPhoneNumber(candidate.phone) }
        message.setOnClickListener { sendSms(candidate.phone) }
        email.setOnClickListener { sendEmail(candidate.email) }

        birthdateEdit.text = Format.formatBirthdateWithAge(requireContext(), candidate.birthdate)
        salaryEdit.text = String.format("%s €", candidate.salary.toString())
        notesEdit.text = candidate.notes

        Glide.with(root.context)
            .load(candidate.profilePicture)
            .placeholder(R.drawable.ic_profile_pic)
            .error(R.drawable.ic_profile_pic)
            .into(profilePicture)
    }

    /**
     * Sets up the toolbar with the candidate's name and back navigation.
     *
     * @param candidate The candidate displayed on this screen.
     */
    private fun setupToolbar(candidate: Candidate) {
        val toolbar = binding.toolbar
        (requireActivity() as AppCompatActivity).setSupportActionBar(toolbar)

        (requireActivity() as AppCompatActivity).supportActionBar?.title =
            "${candidate.firstname} ${candidate.lastname}"

        toolbar.setNavigationOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    /**
     * Navigates to the EditFragment with current candidate ID.
     */
    private fun navigateToEditScreen() {
        val fragment = EditFragment.newInstance(candidateId)
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
                viewModel.deleteCandidate(candidate)
                parentFragmentManager.popBackStack()
                Toast.makeText(context, R.string.deleted, Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }
            .setNegativeButton(R.string.cancel) { dialog, _ -> dialog.dismiss() }
            .create()
            .show()
    }

    /**
     * Updates the favorite icon in the top app bar based on current favorite status.
     *
     * @param item The menu item representing the favorite icon.
     */
    private fun updateStarIcon(item: MenuItem) {
        val iconRes = if (isFavorite) R.drawable.ic_star_filled else R.drawable.ic_star_border
        item.setIcon(iconRes)
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
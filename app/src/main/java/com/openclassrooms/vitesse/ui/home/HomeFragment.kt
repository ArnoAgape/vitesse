package com.openclassrooms.vitesse.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.viewModels
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.openclassrooms.vitesse.R
import com.openclassrooms.vitesse.databinding.HomeScreenBinding
import com.openclassrooms.vitesse.domain.model.Candidate
import com.openclassrooms.vitesse.ui.add.AddFragment
import com.openclassrooms.vitesse.ui.detail.DetailFragment
import com.openclassrooms.vitesse.states.State
import com.openclassrooms.vitesse.ui.home.CandidateAdapter.OnItemClickListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import com.google.android.material.tabs.TabLayout
import kotlin.getValue

/**
 * Home screen fragment displaying a list of candidates with search, tabs and loading states.
 */
@AndroidEntryPoint
class HomeFragment : Fragment(), OnItemClickListener {

    private lateinit var binding: HomeScreenBinding
    private val viewModel: HomeViewModel by viewModels()
    private val candidateAdapter = CandidateAdapter(this)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = HomeScreenBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupFab()
        setupTabs()
        setupSearchBar()
        observeViewModel()
    }

    /**
     * Sets up the RecyclerView with adapter and layout manager.
     */
    private fun setupRecyclerView() {
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = candidateAdapter
        }
    }

    /**
     * Configures the floating action button to open the AddFragment.
     */
    private fun setupFab() {
        binding.fab.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.container, AddFragment())
                .addToBackStack(null)
                .commit()
        }
    }

    /**
     * Initializes and handles interactions with the TabLayout.
     */
    private fun setupTabs() {
        binding.tab.getTabAt(viewModel.selectedTabIndex.value)?.select()
        binding.tab.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                viewModel.selectedTabIndex.value = tab.position
                viewModel.toggleFavorites(tab.position == 1)
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
    }

    /**
     * Configures the SearchView to filter candidates as the user types.
     */
    private fun setupSearchBar() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false
            override fun onQueryTextChange(newText: String?): Boolean {
                viewModel.onSearchChange(newText.orEmpty())
                return true
            }
        })
    }

    /**
     * Observes UI state, candidate list and error messages from the ViewModel.
     */
    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.uiState.collect { state ->

                        when (state.result) {
                            is State.Loading -> {
                                binding.loading.visibility = View.VISIBLE
                                binding.recyclerView.visibility = View.GONE
                                binding.noCandidate.visibility = View.GONE
                            }

                            is State.Success -> {
                                binding.loading.visibility = View.GONE
                                binding.recyclerView.visibility = View.VISIBLE
                                binding.noCandidate.visibility =
                                    if (state.candidate.isEmpty()) View.VISIBLE else View.GONE
                                candidateAdapter.submitList(state.candidate)
                            }

                            is State.Error -> {
                                binding.loading.visibility = View.GONE
                                binding.recyclerView.visibility = View.GONE
                                binding.noCandidate.visibility = View.VISIBLE
                            }

                            is State.Idle -> Unit
                        }
                    }
                }

                // Observe errors
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
     * Handles click on a candidate item. Navigates to the detail screen.
     *
     * @param item The clicked [Candidate]
     */
    override fun onItemClick(item: Candidate) {
        val id = item.id
        if (id != null) {
            val fragment = DetailFragment.newInstance(id)
            parentFragmentManager.beginTransaction()
                .replace(R.id.container, fragment)
                .addToBackStack(null)
                .commit()
        }
    }
}
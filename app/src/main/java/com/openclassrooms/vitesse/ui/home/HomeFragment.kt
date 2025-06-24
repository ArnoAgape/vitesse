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
        defineRecyclerView()
        observeCandidates()
        setupFab()
        setupSearchBar()
        setupTabs()
    }

    private fun defineRecyclerView() {
        val layoutManager = LinearLayoutManager(context)
        binding.recyclerView.layoutManager = layoutManager
        binding.recyclerView.adapter = candidateAdapter
    }

    private fun observeCandidates() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.uiState.collect { state ->
                        binding.loading.visibility =
                            if (state.result == State.Loading) View.VISIBLE else View.GONE
                    }
                }
                launch {
                    viewModel.displayedCandidatesFlow.collect { list ->
                        candidateAdapter.submitList(list)
                        binding.noCandidate.visibility =
                            if (list.isEmpty()) View.VISIBLE else View.INVISIBLE
                    }
                }
                launch {
                    viewModel.displayedAllCandidatesFlow.collect { list ->
                        candidateAdapter.submitList(list)
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

    private fun setupFab() {
        binding.fab.setOnClickListener {
            parentFragmentManager
                .beginTransaction()
                .replace(R.id.container, AddFragment())
                .addToBackStack(null)
                .commit()
        }
    }

    private fun setupTabs() {
        binding.tab.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                viewModel.currentTab.value = tab.position
                viewModel.selectTab(if(tab.position == 0) "all" else "favorites")
                when (tab.position) {
                    0 -> viewModel.selectTab("all")
                    1 -> viewModel.selectTab("favorites")
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
        binding.tab.getTabAt(viewModel.currentTab.value)?.select()

    }

    private fun setupSearchBar() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                viewModel.onSearchChange(newText.orEmpty())
                return true
            }
        })
    }

    override fun onItemClick(item: Candidate) {
        item.id?.let { id ->
            val fragment = DetailFragment.newInstance(id)
            parentFragmentManager.beginTransaction()
                .replace(R.id.container, fragment)
                .addToBackStack(null)
                .commit()
        }
    }
}


package com.openclassrooms.vitesse.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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
import androidx.core.widget.addTextChangedListener
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

    /*private fun setupSearchBar() {
        binding.searchBar.setOnClickListener {
            binding.searchView
        }

        binding.searchView.editText.addTextChangedListener { editable ->
            val query = editable?.toString().orEmpty()
            viewModel.updateSearchQuery(query)
        }
    }*/

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


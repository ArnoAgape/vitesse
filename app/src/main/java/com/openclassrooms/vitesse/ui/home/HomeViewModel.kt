package com.openclassrooms.vitesse.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openclassrooms.vitesse.data.repository.CandidateRepository
import com.openclassrooms.vitesse.domain.model.Candidate
import dagger.hilt.android.lifecycle.HiltViewModel
import com.openclassrooms.vitesse.states.State
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: CandidateRepository
) : ViewModel() {

    private val _allCandidatesFlow = MutableStateFlow<List<Candidate>>(emptyList())

    private val _favoriteCandidatesFlow = MutableStateFlow<List<Candidate>>(emptyList())

    private val searchQuery = MutableStateFlow("")

    private val _errorFlow = MutableStateFlow<String?>(null)
    val errorFlow: StateFlow<String?> = _errorFlow.asStateFlow()

    private val _uiState = MutableStateFlow(HomeUIState())
    val uiState: StateFlow<HomeUIState> = _uiState.asStateFlow()

    val displayedCandidatesFlow = combine(_allCandidatesFlow, searchQuery) { list, query ->
        if (query.isBlank()) list
        else list.filter {
            it.firstname.contains(query, ignoreCase = true) ||
            it.lastname.contains(query, ignoreCase = true)
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val selectedTab = MutableStateFlow("all")
    val currentTab = MutableStateFlow(0)

    val displayedAllCandidatesFlow = combine(_allCandidatesFlow, selectedTab) { list, tab ->
        when (tab) {
            "favorites" -> list.filter { it.isFavorite }
            else -> list
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun selectTab(tabKey: String) {
        selectedTab.value = tabKey
    }

    fun onSearchChange(query: String) {
        searchQuery.value = query
    }

    init {
        viewModelScope.launch {
            repository.getAllCandidates().collect { result ->
                result.onSuccess { _allCandidatesFlow.value = it }
            }
        }
        loadAllCandidates()
        loadAllFavoriteCandidates()
    }

    private fun loadAllCandidates() {

        _uiState.update {
            it.copy(result = State.Loading)
        }

        viewModelScope.launch {
            repository.getAllCandidates().collect { result ->
                if (result.isSuccess) {
                    _allCandidatesFlow.value = result.getOrNull() ?: emptyList()
                    _uiState.update { it.copy(result = State.Success) }
                } else {
                    _errorFlow.value = result.exceptionOrNull()?.message ?: "Error loading the candidates"
                    _uiState.update { it.copy(result = State.Error) }
                }
            }
        }
    }

    private fun loadAllFavoriteCandidates() {
        viewModelScope.launch {
            repository.getAllFavoriteCandidates().collect { result ->
                if (result.isSuccess) {
                    _favoriteCandidatesFlow.value = result.getOrNull() ?: emptyList()
                }
            }
        }
    }

}

data class HomeUIState(
    val result: State = State.Idle,
    val candidate: List<Candidate> = emptyList()
)
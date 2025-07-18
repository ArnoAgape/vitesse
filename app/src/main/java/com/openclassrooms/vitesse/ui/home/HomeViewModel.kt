package com.openclassrooms.vitesse.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openclassrooms.vitesse.data.repository.CandidateRepository
import com.openclassrooms.vitesse.domain.model.Candidate
import dagger.hilt.android.lifecycle.HiltViewModel
import com.openclassrooms.vitesse.states.State
import kotlinx.coroutines.delay
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

    /** Global UI state used to represent screen-level status (Loading, Success, Error). */
    private val _uiState = MutableStateFlow<State>(State.Loading)

    /** Emits error messages to be displayed in the UI. */
    private val _errorFlow = MutableStateFlow<String?>(null)
    val errorFlow: StateFlow<String?> = _errorFlow.asStateFlow()

    /** All candidates retrieved from the database. */
    private val _allCandidatesFlow = MutableStateFlow<List<Candidate>>(emptyList())

    /** Favorite candidates retrieved from the database. */
    private val _favoriteCandidatesFlow = MutableStateFlow<List<Candidate>>(emptyList())

    /** Toggle to show either all or only favorite candidates. */
    val showFavorites = MutableStateFlow(false)

    /** Search query input by the user. */
    private val _searchQuery = MutableStateFlow("")
    fun onSearchChange(query: String) {
        _searchQuery.value = query
    }

    /** Index of the selected tab in the UI. */
    val selectedTabIndex = MutableStateFlow(0)

    /**
     * Candidates to be shown on screen depending on current filters:
     * - all vs favorites
     * - search query
     */
    private val displayedCandidatesFlow: StateFlow<List<Candidate>> =
        combine(
            _allCandidatesFlow,
            _favoriteCandidatesFlow,
            showFavorites,
            _searchQuery
        ) { all, favorites, showFav, query ->
            val base = if (showFav) favorites else all
            if (query.isBlank()) base
            else base.filter {
                it.firstname.contains(query, ignoreCase = true) ||
                        it.lastname.contains(query, ignoreCase = true)
            }
        }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    /**
     * Combined UI state for the home screen,
     * merging displayedCandidatesFlow and selected candidate data.
     */
    val uiState: StateFlow<HomeUIState> = combine(_uiState, displayedCandidatesFlow)
    { uiState, candidates ->
        HomeUIState(result = uiState, candidate = candidates)
    }.stateIn(viewModelScope, SharingStarted.Lazily, HomeUIState())

    init {
        loadAllCandidates()
        loadAllFavoriteCandidates()
    }

    /** Allows toggling between all candidates and favorites only. */
    fun toggleFavorites(show: Boolean) {
        showFavorites.value = show
    }

    /** Fetches all candidates and updates UI state accordingly. */
    private fun loadAllCandidates() {
        _uiState.update { State.Loading }
        viewModelScope.launch {
            delay(1000) // Simulated delay for loading feedback
            repository.getAllCandidates().collect { result ->
                result.onSuccess {
                    _allCandidatesFlow.value = it
                    _uiState.update { State.Success }
                }.onFailure {
                    _errorFlow.value = it.message ?: "Error loading the candidates"
                    _uiState.update { State.Error }
                }
            }
        }
    }

    /** Fetches all favorite candidates. */
    private fun loadAllFavoriteCandidates() {
        viewModelScope.launch {
            repository.getAllFavoriteCandidates().collect { result ->
                result.onSuccess {
                    _favoriteCandidatesFlow.value = it
                }.onFailure {
                    _errorFlow.value = it.message ?: "Error loading the favorite candidates"
                    _uiState.update { State.Error }
                }
            }
        }
    }
}

/**
 * Represents the state of the Home screen.
 * @param result Loading status (Idle, Loading, Success, Error)
 * @param candidate Not used yet; reserved for potential future use.
 */
data class HomeUIState(
    val result: State = State.Idle,
    val candidate: List<Candidate> = emptyList()
)
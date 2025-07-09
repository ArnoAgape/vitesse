package com.openclassrooms.vitesse.ui.detail

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openclassrooms.vitesse.data.repository.CandidateRepository
import com.openclassrooms.vitesse.data.repository.CurrencyRepository
import com.openclassrooms.vitesse.domain.model.Candidate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel responsible for displaying details of a candidate.
 *
 * @property repository The repository used to get the candidate data.
 */
@HiltViewModel
class DetailViewModel @Inject constructor(
    private val repository: CandidateRepository,
    private val currencyRepository: CurrencyRepository
) : ViewModel() {

    // ----------------------------
    // State
    // ----------------------------

    /** Emits the currently selected candidate. */
    private val _candidateFlow = MutableStateFlow<Candidate?>(null)

    /** Emits the latest exchange rate EUR -> GBP. */
    private val _currencyFlow = MutableStateFlow<Double?>(null)

    /** Emits one-time error messages for the UI. */
    private val _errorFlow = MutableStateFlow<String?>(null)
    val errorFlow: StateFlow<String?> = _errorFlow.asStateFlow()

    /**
     * Combined UI state for the detail screen,
     * merging GBP rate and selected candidate data.
     */
    val uiState: StateFlow<DetailUIState> = combine(_currencyFlow, _candidateFlow)
    { gbp, candidate ->
        DetailUIState(result = gbp, candidate = candidate)
    }.stateIn(viewModelScope, SharingStarted.Lazily, DetailUIState(null, null))

    // ----------------------------
    // Public API
    // ----------------------------

    /**
     * Loads and emits the candidate for the given ID.
     * @param id Candidate's unique identifier.
     */
    fun getCandidateById(id: Long) {
        viewModelScope.launch {
            val result = repository.getCandidate(id)
            if (result.isSuccess) {
                _candidateFlow.value = result.getOrNull()
            } else {
                _errorFlow.value =
                    result.exceptionOrNull()?.message ?: "Error while collecting the candidate"
            }
        }
    }

    /**
     * Fetches and emits the current EUR to GBP exchange rate.
     * Used for converting salary estimates.
     */
    fun getEuroConverted() {
        viewModelScope.launch {
            try {
                val gbpRate = currencyRepository.getEuroToGbpRate()
                _currencyFlow.value = gbpRate
                Log.d("Currency", "1 EUR = $gbpRate GBP")
            } catch (e: Exception) {
                _errorFlow.value = "API Error : ${e.message}"
            }
        }
    }

    /**
     * Deletes the given candidate from the database.
     */
    fun deleteCandidate() {
        val candidate = uiState.value.candidate
        if (candidate == null) {
            return
        }
        viewModelScope.launch {
            repository.deleteCandidate(candidate)
        }
    }

    /**
     * Updates the favorite status of a candidate.
     */
    fun toggleFavorite() {
        val candidate = uiState.value.candidate
        val id = candidate?.id
        val isFavorite = candidate?.isFavorite

        viewModelScope.launch {
            if (id != null && isFavorite != null) {
                repository.updateFavorite(id, !isFavorite)
                getCandidateById(id)
            }
        }
    }

}

data class DetailUIState(
    val result: Double?,
    val candidate: Candidate? = null
)
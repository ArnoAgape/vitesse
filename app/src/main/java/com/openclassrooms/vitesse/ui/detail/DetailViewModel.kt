package com.openclassrooms.vitesse.ui.detail

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openclassrooms.vitesse.data.repository.CandidateRepository
import com.openclassrooms.vitesse.data.repository.CurrencyRepository
import com.openclassrooms.vitesse.domain.model.Candidate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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
    val candidateFlow: StateFlow<Candidate?> = _candidateFlow.asStateFlow()

    /** Emits the latest exchange rate EUR -> GBP. */
    private val _gbpFlow = MutableStateFlow<Double?>(null)
    val gbpFlow: StateFlow<Double?> = _gbpFlow.asStateFlow()

    /** Emits one-time error messages for the UI. */
    private val _errorFlow = MutableStateFlow<String?>(null)
    val errorFlow: StateFlow<String?> = _errorFlow.asStateFlow()

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
                _gbpFlow.value = gbpRate
                Log.d("Currency", "1 EUR = $gbpRate GBP")
            } catch (e: Exception) {
                _errorFlow.value = "API Error : ${e.message}"
            }
        }
    }

    /**
     * Deletes the given candidate from the database.
     * @param candidate Candidate to delete.
     */
    fun deleteCandidate(candidate: Candidate) {
        viewModelScope.launch {
            repository.deleteCandidate(candidate)
        }
    }

    /**
     * Updates the favorite status of a candidate.
     * @param id Candidate ID.
     * @param isFavorite New favorite value.
     */
    fun toggleFavorite(id: Long, isFavorite: Boolean) {
        viewModelScope.launch {
            repository.updateFavorite(id, isFavorite)
        }
    }
}

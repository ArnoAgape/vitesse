package com.openclassrooms.vitesse.ui.edit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.openclassrooms.vitesse.data.repository.CandidateRepository
import com.openclassrooms.vitesse.domain.model.Candidate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for managing the editing of a [Candidate].
 * Handles fetching, updating and preparing birthdate formatting.
 */
@HiltViewModel
class EditViewModel @Inject constructor(
    private val repository: CandidateRepository
) : ViewModel() {

    // --- State flows ---

    /** Emits the current candidate being edited */
    private val _candidateFlow = MutableStateFlow<Candidate?>(null)
    val candidateFlow: StateFlow<Candidate?> = _candidateFlow.asStateFlow()

    /** Emits error messages in case of failures */
    private val _errorFlow = MutableStateFlow<String?>(null)
    val errorFlow: StateFlow<String?> = _errorFlow.asStateFlow()

    // --- Birthdate handling ---

    /** Stores the birthdate formatted for database storage */
    private var birthdateToStore: String = ""

    /**
     * Sets the birthdate value to be stored (in database format).
     *
     * @param date the formatted birthdate string
     */
    fun setBirthdateForDb(date: String) {
        birthdateToStore = date
    }

    /**
     * Returns the birthdate previously set for storage.
     *
     * @return the stored birthdate in DB format
     */
    fun getBirthdateForDb(): String = birthdateToStore

    // --- Repository actions ---

    /**
     * Retrieves a candidate by their ID from the repository.
     * Updates [candidateFlow] or emits an error.
     *
     * @param id the ID of the candidate to fetch
     */
    fun getCandidateById(id: Long) {
        viewModelScope.launch {
            val result = repository.getCandidate(id)
            if (result.isSuccess) {
                _candidateFlow.value = result.getOrNull()
            } else {
                _errorFlow.value = result.exceptionOrNull()?.message
                    ?: "Error while collecting the candidate"
            }
        }
    }

    /**
     * Updates the provided [candidate] in the repository.
     *
     * @param candidate the candidate object with updated fields
     */
    fun updateCandidate(candidate: Candidate) {
        viewModelScope.launch {
            repository.updateCandidate(candidate)
        }
    }
}
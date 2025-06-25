package com.openclassrooms.vitesse.ui.add

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
 * ViewModel responsible for managing the state and logic related to adding a candidate.
 *
 * @property repository The repository used to access and modify candidate data.
 */
@HiltViewModel
class AddViewModel @Inject constructor(
    private val repository: CandidateRepository
) : ViewModel() {

    // region StateFlows

    private val _candidateFlow = MutableStateFlow<Candidate?>(null)

    private val _errorFlow = MutableStateFlow<String?>(null)
    /** Exposes error messages in case of failure during candidate creation. */
    val errorFlow: StateFlow<String?> = _errorFlow.asStateFlow()

    // endregion

    // region Public Methods

    /**
     * Adds a new candidate to the repository.
     *
     * @param candidate The [Candidate] object to be added.
     */
    fun addCandidate(candidate: Candidate) {
        viewModelScope.launch {
            val result = repository.addCandidate(candidate)
            if (result.isFailure) {
                _errorFlow.value = result.exceptionOrNull()?.message
                    ?: "Error while adding a candidate"
            } else {
                _candidateFlow.value = candidate
            }
        }
    }

    /**
     * Sets the birthdate string that will be stored in the database.
     *
     * @param date Formatted birthdate string.
     */
    fun setBirthdateForDb(date: String) {
        birthdateToStore = date
    }

    /**
     * Gets the birthdate string to store in the database.
     *
     * @return Formatted birthdate string.
     */
    fun getBirthdateForDb(): String = birthdateToStore

    // endregion

    // region Private Properties

    private var birthdateToStore: String = ""

    // endregion
}
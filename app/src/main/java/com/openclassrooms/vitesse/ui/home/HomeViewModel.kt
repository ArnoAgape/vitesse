package com.openclassrooms.vitesse.ui.home

import androidx.lifecycle.ViewModel
import com.openclassrooms.vitesse.domain.model.Candidate
import com.openclassrooms.vitesse.domain.usecase.AddNewCandidateUseCase
import com.openclassrooms.vitesse.domain.usecase.GetAllCandidatesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getAllCandidatesUseCase: GetAllCandidatesUseCase,
    private val addNewCandidateUseCase: AddNewCandidateUseCase
) : ViewModel() {

    private val _candidatesFlow = MutableStateFlow<List<Candidate>>(emptyList())
    val candidatesFlow: StateFlow<List<Candidate>> = _candidatesFlow.asStateFlow()

    private val _errorFlow = MutableStateFlow<String?>(null)
    val errorFlow: StateFlow<String?> = _errorFlow.asStateFlow()

    init {
        loadAllCandidates()
    }

    private fun loadAllCandidates() {
        viewModelScope.launch {
            val result = getAllCandidatesUseCase.execute()
            if (result.isSuccess) {
                _candidatesFlow.value = result.getOrNull() ?: emptyList()
            } else {
                _errorFlow.value = result.exceptionOrNull()?.message ?: "Error loading the candidates"
            }
        }
    }

    fun addNewCandidate(candidate: Candidate) {
        viewModelScope.launch {
            val result = addNewCandidateUseCase.execute(candidate)
            if (result.isSuccess) {
                loadAllCandidates()
            } else {
                _errorFlow.value = result.exceptionOrNull()?.message ?: "Error while adding a candidate"
            }
        }
    }
}
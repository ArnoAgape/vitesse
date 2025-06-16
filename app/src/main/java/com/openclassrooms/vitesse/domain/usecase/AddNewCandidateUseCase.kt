package com.openclassrooms.vitesse.domain.usecase

import com.openclassrooms.vitesse.data.repository.CandidateRepository
import com.openclassrooms.vitesse.domain.model.Candidate
import javax.inject.Inject

class AddNewCandidateUseCase @Inject constructor(private val candidateRepository: CandidateRepository) {
    suspend fun execute(candidate: Candidate): Result<Unit> {
        return candidateRepository.addCandidate(candidate)
    }
}
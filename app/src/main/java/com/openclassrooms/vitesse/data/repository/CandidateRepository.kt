package com.openclassrooms.vitesse.data.repository

import com.openclassrooms.vitesse.data.dao.CandidateDao
import com.openclassrooms.vitesse.domain.model.Candidate
import javax.inject.Singleton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

/**
 * Repository class responsible for managing candidate data operations.
 * It acts as an abstraction layer between the data sources (Room via [CandidateDao]) and the ViewModels.
 *
 * This class provides suspend functions for one-time operations (insert, update, delete),
 * and Flow-based functions for observing lists of candidates.
 */
@Singleton
class CandidateRepository(
    private val candidateDao: CandidateDao
) {

    /**
     * Retrieves all candidates from the database as a [Flow] of [Result] containing a list of [Candidate].
     */
    fun getAllCandidates(): Flow<Result<List<Candidate>>> {
        return candidateDao.getAllCandidates()
            .map { dtoList -> Result.success(dtoList.map { Candidate.fromDto(it) }) }
            .catch { e -> emit(Result.failure(e)) }
    }

    /**
     * Retrieves all favorite candidates (isFavorite == true) as a [Flow] of [Result] containing a list of [Candidate].
     */
    fun getAllFavoriteCandidates(): Flow<Result<List<Candidate>>> {
        return candidateDao.getAllFavoriteCandidates(true)
            .map { dtoList -> Result.success(dtoList.map { Candidate.fromDto(it) }) }
            .catch { e -> emit(Result.failure(e)) }
    }

    /**
     * Retrieves a specific candidate by ID.
     *
     * @param id The candidate's ID.
     * @return [Result] containing the [Candidate] if found, or an exception otherwise.
     */
    suspend fun getCandidate(id: Long?): Result<Candidate> {
        return withContext(Dispatchers.IO) {
            try {
                val user = candidateDao.getCandidateById(id).let { Candidate.fromDto(it) }
                Result.success(user)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    /**
     * Inserts a new candidate into the database.
     *
     * @param candidate The candidate to insert.
     * @return [Result] indicating success or failure.
     */
    suspend fun addCandidate(candidate: Candidate): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                candidateDao.insertCandidate(candidate.toDto())
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    /**
     * Updates an existing candidate in the database.
     *
     * @param candidate The candidate to update.
     * @return [Result] indicating success or failure.
     */
    suspend fun updateCandidate(candidate: Candidate): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                candidateDao.updateCandidate(candidate.toDto())
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    /**
     * Updates the favorite status of a candidate.
     *
     * @param id The ID of the candidate.
     * @param isFavorite New favorite status to apply.
     * @return [Result] indicating success or failure.
     */
    suspend fun updateFavorite(id: Long?, isFavorite: Boolean): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                candidateDao.updateFavorite(id, isFavorite)
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    /**
     * Deletes a candidate from the database using their ID.
     *
     * @param candidate The candidate to delete.
     * @return [Result] indicating success or failure.
     */
    suspend fun deleteCandidate(candidate: Candidate): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                candidate.id?.let {
                    candidateDao.deleteCandidateById(it)
                }
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}

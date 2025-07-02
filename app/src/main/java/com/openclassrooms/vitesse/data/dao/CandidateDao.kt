package com.openclassrooms.vitesse.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.openclassrooms.vitesse.data.dto.CandidateDto
import kotlinx.coroutines.flow.Flow

@Dao
interface CandidateDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCandidate(candidate: CandidateDto): Long

    @Query("SELECT * FROM candidate")
    fun getAllCandidates(): Flow<List<CandidateDto>>

    @Query("SELECT * FROM candidate WHERE id = :id")
    suspend fun getCandidateById(id: Long?): CandidateDto

    @Query("SELECT * FROM candidate WHERE favorite = :isFavorite")
    fun getAllFavoriteCandidates(isFavorite: Boolean): Flow<List<CandidateDto>>

    @Query("DELETE FROM candidate WHERE id = :id")
    suspend fun deleteCandidateById(id: Long?)

    @Update
    suspend fun updateCandidate(candidate: CandidateDto)

    @Query("UPDATE candidate SET favorite = :isFavorite WHERE id = :id")
    suspend fun updateFavorite(id: Long?, isFavorite: Boolean)

    @Query("SELECT * FROM candidate WHERE id = :id")
    suspend fun getCandidateByIdOrNull(id: Long): CandidateDto?

}
package com.openclassrooms.vitesse.domain.model

import com.openclassrooms.vitesse.data.dto.CandidateDto

data class Candidate(
    val id: Long?,
    val firstname: String,
    val lastname: String,
    val phone: String,
    val email: String,
    val birthdate: String,
    val salary: Double,
    val notes: String,
    val profilePicture: String,
    val isFavorite: Boolean = false
) {

    fun toDto(): CandidateDto {
        return CandidateDto(
            id = id,
            firstname = firstname,
            lastname = lastname,
            phone = phone,
            email = email,
            birthdate = birthdate,
            salary = salary,
            notes = notes,
            profilePicture = profilePicture,
            isFavorite = isFavorite
        )
    }

    companion object {
        fun fromDto(dto: CandidateDto): Candidate {
            return Candidate(
                id = dto.id,
                firstname = dto.firstname,
                lastname = dto.lastname,
                phone = dto.phone,
                email = dto.email,
                birthdate = dto.birthdate,
                salary = dto.salary,
                notes = dto.notes,
                profilePicture = dto.profilePicture,
                isFavorite = dto.isFavorite
            )
        }
    }
}
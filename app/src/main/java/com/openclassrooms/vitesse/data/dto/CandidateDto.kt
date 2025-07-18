package com.openclassrooms.vitesse.data.dto

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "candidate")
data class CandidateDto(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long?,


    @ColumnInfo(name = "firstname")
    val firstname: String,

    @ColumnInfo(name = "lastname")
    val lastname: String,

    @ColumnInfo(name = "phone")
    val phone: String,

    @ColumnInfo(name = "email")
    val email: String,

    @ColumnInfo(name = "birthdate")
    val birthdate: String,

    @ColumnInfo(name = "salary")
    val salary: Int,

    @ColumnInfo(name = "notes")
    val notes: String,

    @ColumnInfo(name = "profile_picture")
    val profilePicture: String,

    @ColumnInfo(name = "favorite")
    val isFavorite: Boolean

)
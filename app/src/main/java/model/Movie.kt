package com.example.moviemuse.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity
data class Movie(
    @PrimaryKey()
    val id: Int,
    val title: String,
    val overview: String,
    @SerializedName("poster_path")
    var posterPath: String,

    val rating: Double // added field for rating
)
package com.example.moviemues.model

import com.google.gson.annotations.SerializedName

data class Movie(
    val id: Int,
    val title: String,
    val overview: String,
    @SerializedName("poster_path")  // Map JSON field "poster_path" to "posterPath"
    val posterPath: String,

    val rating: Double // added field for rating
)
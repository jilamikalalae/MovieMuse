package com.example.moviemuse.model

import com.google.gson.annotations.SerializedName

data class Movie(
    val id: Int,
    val title: String,
    val overview: String,
    @SerializedName("poster_path")
    val posterPath: String,

    val rating: Double,

    val genre: String? = "Unknown"
) {

    val imageUrl: String
        get() = "https://image.tmdb.org/t/p/w500$posterPath"
}

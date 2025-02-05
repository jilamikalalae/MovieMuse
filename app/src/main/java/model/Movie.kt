package com.example.moviemues.model

data class Movie(
    val id: Int,
    val title: String,
    val overview: String,
    val posterPath: String,  // existing field
    val rating: Double // added field for rating
)

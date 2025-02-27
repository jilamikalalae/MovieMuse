package com.example.moviemuse.model


data class ReviewResponse(
    val results: List<Review>  // API returns a `results` array
)

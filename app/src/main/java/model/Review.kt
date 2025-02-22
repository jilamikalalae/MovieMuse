package com.example.moviemuse.model

data class Review(

    val author: String = "",
    val content: String = "",
    val rating: Int = 0,
    val timestamp: com.google.firebase.Timestamp? = null
)

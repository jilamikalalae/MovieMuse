package com.example.moviemuse.model

import com.google.gson.annotations.SerializedName

data class Review(
    val author: String,
    @SerializedName("content") val content: String
)

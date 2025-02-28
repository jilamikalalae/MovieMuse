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

    val rating: Double,

    val genre: String? = "Unknown"
) {

    val imageUrl: String
        get() = "https://image.tmdb.org/t/p/w500$posterPath"
}

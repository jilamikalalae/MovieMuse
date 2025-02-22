package model

import com.google.gson.annotations.SerializedName

data class Trailer(
    val name: String,
    val key: String,
    val type: String
)

data class TrailerResponse (
    val results: List<Trailer>
)

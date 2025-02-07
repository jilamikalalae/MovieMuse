package com.example.moviemues.Network

import retrofit2.http.GET
import retrofit2.http.Query
import com.example.moviemues.model.MovieResponse

interface MovieApiService {
    @GET("movie/popular")
    suspend fun getPopularMovies(
        @Query("api_key") apiKey: String,
        @Query("page") page: Int
    ): MovieResponse
}
package com.example.moviemuse.Network

import retrofit2.http.GET
import retrofit2.http.Query
import com.example.moviemuse.model.MovieResponse

interface MovieApiService {
    @GET("movie/popular")
    suspend fun getPopularMovies(
        @Query("api_key") apiKey: String,
        @Query("page") page: Int
    ): MovieResponse
}
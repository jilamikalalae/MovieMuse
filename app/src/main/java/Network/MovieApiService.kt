package com.example.moviemues.network

import com.example.moviemues.model.Movie
import retrofit2.http.GET

interface MovieApiService {

    @GET("movie/popular?api_key=YOUR_API_KEY")
    suspend fun getPopularMovies(): List<Movie>
}

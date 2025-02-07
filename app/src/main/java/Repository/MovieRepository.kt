package com.example.moviemues.repository

import com.example.moviemues.Network.MovieApiService
import com.example.moviemues.Network.RetrofitClient
import com.example.moviemues.model.Movie
import com.example.moviemues.model.MovieResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MovieRepository {

    private val movieApiService = RetrofitClient.retrofitService

    suspend fun fetchPopularMovies(): List<Movie> {
        return withContext(Dispatchers.IO) {
            val apiKey = "your_api_key_here" // Replace with actual API key
            val page = 1 // Adjust page as needed

            val response: MovieResponse = movieApiService.getPopularMovies(apiKey, page)
            response.results // Extract the movie list
        }
    }
}

package com.example.moviemues.repository

import android.util.Log
import com.example.moviemues.BuildConfig
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
            val apiKey = BuildConfig.TMDB_API_KEY // Replace with actual API key
            val page = 1

            val response: MovieResponse = movieApiService.getPopularMovies(apiKey, page)

            val updatedMovies = response.results.map { movie ->
                movie.copy(posterPath = BuildConfig.TMDB_BASE_IMAGE_URL + movie.posterPath)
            }

            Log.d("data", updatedMovies.toString())

            updatedMovies
        }
    }

}

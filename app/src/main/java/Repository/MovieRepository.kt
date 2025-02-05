package com.example.moviemues.repository

import com.example.moviemues.network.MovieApiService
import com.example.moviemues.network.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State
import com.example.moviemues.model.Movie as Movie1

class MovieRepository {

    private val movieApiService = RetrofitClient.retrofitService

    private val _movies = mutableStateOf<List<Movie1>>(emptyList())
    val movies: State<List<Movie1>> = _movies

    suspend fun fetchPopularMovies() {
        withContext(Dispatchers.IO) {
            val response = movieApiService.getPopularMovies("your_api_key", 1)
            _movies.value = response.results
        }
    }
}

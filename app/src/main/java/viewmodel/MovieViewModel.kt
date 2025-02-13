package com.example.moviemuse.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moviemuse.BuildConfig
import com.example.moviemuse.repository.MovieRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.example.moviemuse.model.Movie

class MovieViewModel : ViewModel() {
    private val repository = MovieRepository()

    // StateFlow to hold movies data
    private val _movies = MutableStateFlow<List<Movie>>(emptyList())
    val movies: StateFlow<List<Movie>> = _movies

    init {
        fetchMovies()
    }

    private fun fetchMovies() {
        viewModelScope.launch {
            try {
                val fetchedMovies = repository.fetchPopularMovies() ?: emptyList()
                Log.d("MovieViewModel", "Fetched Movies: $fetchedMovies")
                val updatedMovies = fetchedMovies.map { movie ->
                    movie.copy(posterPath = BuildConfig.TMDB_BASE_IMAGE_URL + movie.posterPath)
                }
                _movies.value = updatedMovies

            } catch (e: Exception) {
                Log.e("MovieViewModel", "Error fetching movies $e", e)
            }
        }
    }
}

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
import com.example.moviemuse.model.Review

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

    fun getMovieById(movieId: Int): StateFlow<Movie?> {
        val movieState = MutableStateFlow<Movie?>(null)

        viewModelScope.launch {
            try {
                val movie = _movies.value.find { it.id == movieId }
                movieState.value = movie
            } catch (e: Exception) {
                Log.e("MovieViewModel", "Error fetching movie details", e)
            }
        }
        return movieState
    }

    fun getReviews(movieId: Int): StateFlow<List<Review>> {
        val reviewsState = MutableStateFlow<List<Review>>(emptyList())

        viewModelScope.launch {
            try {
                val response = repository.fetchMovieReviews(movieId)
                reviewsState.value = response
            } catch (e: Exception) {
                Log.e("MovieViewModel", "Error fetching reviews", e)
            }
        }
        return reviewsState

    }

    // In your existing MovieViewModel.kt, add this function
    fun addReview(content: String, rating: Int, movieId: Int) {
        viewModelScope.launch {
            try {
                // You can implement the API call here
                // For now, we'll just update the UI
                val newReview = Review(
                    author = "User", // Replace with actual user name
                    content = content
                )
                // Update your reviews state
            } catch (e: Exception) {
                Log.e("MovieViewModel", "Error adding review", e)
            }
        }
    }


}

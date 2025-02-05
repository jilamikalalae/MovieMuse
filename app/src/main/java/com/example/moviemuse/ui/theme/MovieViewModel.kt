package com.example.moviemues.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.moviemues.ui.Movie

class MovieViewModel : ViewModel() {
    private val _movies = MutableLiveData<List<Movie>>(emptyList()) // Ensure it's initialized
    val movies: LiveData<List<Movie>> = _movies
}


    private fun loadMovies() {
        _movies.value = listOf(
            Movie("Inception", "https://image.tmdb.org/t/p/w500/qmDpIHrmpJINaRKAfWQfftjCdyi.jpg", 4.5f),
            Movie("Interstellar", "https://image.tmdb.org/t/p/w500/gEU2QniE6E77NI6lCU6MxlNBvIx.jpg", 4.7f),
            Movie("The Dark Knight", "https://image.tmdb.org/t/p/w500/qJ2tW6WMUDux911r6m7haRef0WH.jpg", 5.0f)
        )
    }
}

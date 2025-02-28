package com.example.moviemuse.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.moviemuse.model.Movie
import com.example.moviemuse.repository.RecentRepository
import com.example.moviemuse.roomDb.MovieDao
import com.example.moviemuse.roomDb.MovieDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RecentMovieViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: RecentRepository
    val storedMovies: LiveData<List<Movie>>

    private val _movies = MutableStateFlow<List<Movie>>(emptyList())
    val movies: StateFlow<List<Movie>> = _movies

    init {
        val movieDao = MovieDatabase.getDatabase(application).movieDao()
        repository = RecentRepository(movieDao)
        storedMovies = repository.getAllMoviesFromDb()
    }

    fun addMovieToDb(movie: Movie) {
        viewModelScope.launch (Dispatchers.IO) {
            repository.addMovieToDb(movie)
        }
    }

    fun deleteAllMovies() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteAllMovies() // Call the repository to delete movies
        }
    }
}
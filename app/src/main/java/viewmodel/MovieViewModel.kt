package viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moviemues.repository.MovieRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.example.moviemues.model.Movie

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
            // Fetch movies and update state
            val fetchedMovies = repository.fetchPopularMovies() // This now returns List<Movie>
            _movies.value = fetchedMovies // Set the list of movies to the state flow
        }
    }
}

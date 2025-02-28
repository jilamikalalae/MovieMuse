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
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import model.Trailer

class MovieViewModel : ViewModel() {
    private val repository = MovieRepository()

    private val _movies = MutableStateFlow<List<Movie>>(emptyList())
    val movies: StateFlow<List<Movie>> = _movies

    private val _reviews = MutableStateFlow<List<Review>>(emptyList())
    val reviews: StateFlow<List<Review>> = _reviews

    private val _trailers = MutableStateFlow<List<Trailer>>(emptyList())
    val trailers: StateFlow<List<Trailer>> = _trailers

    private val _selectedGenre = MutableStateFlow<String?>(null)  // ✅ แก้เป็น String?
    val selectedGenre: StateFlow<String?> = _selectedGenre

    fun setGenre(genre: String?) {  // ✅ เพิ่มฟังก์ชัน setGenre()
        _selectedGenre.value = genre
    }

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

    fun getReviews(movieId: Int) {
        viewModelScope.launch {
            // Fetch reviews from Firestore
            val firebaseReviews = try {
                val db = FirebaseFirestore.getInstance()
                val snapshot = db.collection("movies")
                    .document(movieId.toString())
                    .collection("reviews")
                    .get()
                    .await()
                snapshot.documents.mapNotNull { it.toObject(Review::class.java) }
            } catch (e: Exception) {
                Log.e("MovieViewModel", "Error fetching Firebase reviews", e)
                emptyList()
            }

            // Fetch reviews from TMDB
            val tmdbReviews = try {
                repository.fetchMovieReviews(movieId) ?: emptyList()
            } catch (e: Exception) {
                Log.e("MovieViewModel", "Error fetching TMDB reviews", e)
                emptyList()
            }

            Log.d("Movies Reviews", tmdbReviews.toString())

            // Combine both lists
            _reviews.value = firebaseReviews + tmdbReviews
        }
    }

    fun getTrailers(movieId: Int) {
        viewModelScope.launch {
            try {
                val response = repository.fetchMovieTrailer(movieId)
                _trailers.value = response
                Log.d("MovieTrailers", response.toString())
            } catch (e: Exception) {
                Log.e("MovieTrailers", "Error fetching trailers", e)
            }
        }
    }

    fun addReview(content: String, rating: Int, movieId: Int, userName: String) {
        viewModelScope.launch {
            try {
                val db = FirebaseFirestore.getInstance()
                val reviewData = hashMapOf(
                    "author" to userName,
                    "content" to content,
                    "rating" to rating,
                    "timestamp" to FieldValue.serverTimestamp()
                )
                db.collection("movies")
                    .document(movieId.toString())
                    .collection("reviews")
                    .add(reviewData)
                    .addOnSuccessListener { documentReference ->
                        Log.d("MovieViewModel", "Review added with ID: ${documentReference.id}")

                        getReviews(movieId)
                    }
                    .addOnFailureListener { e ->
                        Log.e("MovieViewModel", "Error adding review", e)
                    }
            } catch (e: Exception) {
                Log.e("MovieViewModel", "Error adding review", e)
            }
        }
    }
}

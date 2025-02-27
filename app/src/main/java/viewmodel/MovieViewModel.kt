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

//class MovieViewModel : ViewModel() {
//
//    private val repository = MovieRepository()
//
//    private val _movies = MutableStateFlow<List<Movie>>(emptyList())
//    val movies: StateFlow<List<Movie>> = _movies
//
//    init {
//        fetchMovies()
//    }
//
//    private fun fetchMovies() {
//        viewModelScope.launch {
//            try {
//                val fetchedMovies = repository.fetchPopularMovies() ?: emptyList()
//                Log.d("MovieViewModel", "Fetched Movies: $fetchedMovies")
//                val updatedMovies = fetchedMovies.map { movie ->
//                    // Prepend the full image URL
//                    movie.copy(posterPath = BuildConfig.TMDB_BASE_IMAGE_URL + movie.posterPath)
//                }
//                _movies.value = updatedMovies
//            } catch (e: Exception) {
//                Log.e("MovieViewModel", "Error fetching movies $e", e)
//            }
//        }
//    }
//
//    // 2) Get a single Movie from the current list (no Firestore needed here)
//    fun getMovieById(movieId: Int): StateFlow<Movie?> {
//        val movieState = MutableStateFlow<Movie?>(null)
//        viewModelScope.launch {
//            try {
//                val movie = _movies.value.find { it.id == movieId }
//                movieState.value = movie
//            } catch (e: Exception) {
//                Log.e("MovieViewModel", "Error fetching movie details", e)
//            }
//        }
//        return movieState
//    }
//
//    fun getReviews(movieId: Int): StateFlow<List<Review>> {
//        val reviewsState = MutableStateFlow<List<Review>>(emptyList())
//
//        val db = FirebaseFirestore.getInstance()
//        val reviewsCollectionRef = db
//            .collection("movies")
//            .document(movieId.toString())
//            .collection("reviews")
//
//        reviewsCollectionRef.addSnapshotListener { snapshot, e ->
//            if (e != null) {
//                Log.e("MovieViewModel", "Error fetching reviews from Firestore", e)
//                return@addSnapshotListener
//            }
//            if (snapshot != null) {
//                val reviewList = snapshot.documents.mapNotNull { doc ->
//                    doc.toObject(Review::class.java)
//                }
//                reviewsState.value = reviewList
//            }
//        }
//
//        return reviewsState
//    }
//
//    fun addReview(content: String, rating: Int, movieId: Int) {
//        viewModelScope.launch {
//            try {
//                val db = FirebaseFirestore.getInstance()
//                val movieDocRef = db.collection("movies").document(movieId.toString())
//
//                val reviewData = hashMapOf(
//                    "author" to "User", // replace with actual user name if you have it
//                    "content" to content,
//                    "rating" to rating,
//                    "timestamp" to FieldValue.serverTimestamp()
//                )
//
//                movieDocRef.collection("reviews")
//                    .add(reviewData)
//                    .addOnSuccessListener { docRef ->
//                        Log.d("MovieViewModel", "Review added with ID: ${docRef.id}")
//                    }
//                    .addOnFailureListener { e ->
//                        Log.e("MovieViewModel", "Error adding review to Firestore", e)
//                    }
//            } catch (e: Exception) {
//                Log.e("MovieViewModel", "Error adding review", e)
//            }
//        }
//    }
//}

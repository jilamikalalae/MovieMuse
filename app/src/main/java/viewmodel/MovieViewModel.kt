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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class MovieViewModel : ViewModel() {
    private val repository = MovieRepository()

    // StateFlow to hold movies data
    private val _movies = MutableStateFlow<List<Movie>>(emptyList())
    val movies: StateFlow<List<Movie>> = _movies

    private val _userFavorites = MutableStateFlow<List<Int>>(emptyList())
    val userFavorites: StateFlow<List<Int>> = _userFavorites

    init {
        fetchMovies()
        fetchUserFavorites()
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

private fun fetchUserFavorites() {
    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
    val db = FirebaseFirestore.getInstance()
    val userDocRef = db.collection("users").document(userId)

    userDocRef.addSnapshotListener { snapshot, error ->
        if (error != null) {
            Log.e("MovieViewModel", "Error fetching favorites", error)
            return@addSnapshotListener
        }
        if (snapshot != null && snapshot.exists()) {
            // Cast as List<Long> and convert to Int
            val favsLong = snapshot.get("favorites") as? List<Long> ?: emptyList()
            _userFavorites.value = favsLong.map { it.toInt() }
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

    fun toggleFavorite(movie: Movie) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val db = FirebaseFirestore.getInstance()
        val userDocRef = db.collection("users").document(userId)
        val currentFavs = _userFavorites.value

        if (currentFavs.contains(movie.id)) {
            userDocRef.update("favorites", FieldValue.arrayRemove(movie.id))
                .addOnSuccessListener { Log.d("MovieViewModel", "Removed favorite: ${movie.id}") }
                .addOnFailureListener { e -> Log.e("MovieViewModel", "Error removing favorite", e) }
        } else {
            userDocRef.update("favorites", FieldValue.arrayUnion(movie.id))
                .addOnSuccessListener { Log.d("MovieViewModel", "Added favorite: ${movie.id}") }
                .addOnFailureListener { e -> Log.e("MovieViewModel", "Error adding favorite", e) }
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

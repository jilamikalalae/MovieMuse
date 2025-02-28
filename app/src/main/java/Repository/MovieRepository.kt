package com.example.moviemuse.repository

import android.util.Log
import com.example.moviemuse.BuildConfig
import com.example.moviemuse.Network.MovieApiService
import com.example.moviemuse.Network.RetrofitClient
import com.example.moviemuse.model.Movie
import com.example.moviemuse.model.MovieResponse
import com.example.moviemuse.model.Review
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import model.Trailer

class MovieRepository {

    private val movieApiService = RetrofitClient.retrofitService

    suspend fun fetchPopularMovies(): List<Movie> {
        return withContext(Dispatchers.IO) {
            val apiKey = BuildConfig.TMDB_API_KEY // Replace with actual API key
            val page = 1

            val response = movieApiService.getPopularMovies(apiKey, page)

            Log.d("response", response.toString())
//
//            val updatedMovies = response.results.map { movie ->
//                movie.copy(posterPath = BuildConfig.TMDB_BASE_IMAGE_URL + movie.posterPath)
//            }
//
//            Log.d("data", updatedMovies.toString())

            response.results
        }
    }

    suspend fun fetchMovieReviews(movieId: Int): List<Review> {
        return withContext(Dispatchers.IO) {
            try {
                val response = movieApiService.getMovieReviews(movieId, BuildConfig.TMDB_API_KEY)
                response.results  // Extract the list of reviews from `results`
            } catch (e: Exception) {
                Log.e("MovieRepository", "Error fetching reviews", e)
                emptyList()
            }
        }
    }

    suspend fun fetchMovieTrailer(movieId: Int): List<Trailer> {
        return withContext(Dispatchers.IO) {
            try {
                val response = movieApiService.getMovieVideos(movieId, BuildConfig.TMDB_API_KEY)
                response.results
            } catch (e: Exception) {
                Log.e("MovieRepository", "Error fetching reviews", e)
                emptyList()
            }
        }
    }

    suspend fun searchMovie(keyword: String): List<Movie> {
        return withContext(Dispatchers.IO) {
            try {
                val response = movieApiService.searchMovie( BuildConfig.TMDB_API_KEY,1,keyword)
                response.results
            } catch (e: Exception) {
                Log.e("MovieRepository", "Error search movie", e)
                emptyList()
            }
        }
    }

    suspend fun getMovieById(movieId: Int): Movie? {
        return withContext(Dispatchers.IO) {
            try {
                val response = movieApiService.getMovieDetail( movieId,BuildConfig.TMDB_API_KEY)
                response
            } catch (e: Exception) {
                Log.e("MovieRepository", "Error get movie by id", e)
                null
            }
        }
    }


}

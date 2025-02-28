package com.example.moviemuse.repository;

import androidx.lifecycle.LiveData
import com.example.moviemuse.model.Movie
import com.example.moviemuse.roomDb.MovieDao

class RecentRepository (private val movieDao: MovieDao){
    fun getAllMoviesFromDb(): LiveData<List<Movie>> = movieDao.getAll()

    suspend fun addMovieToDb(movie: Movie) {
        movieDao.add(movie)
    }

    suspend fun deleteAllMovies() {
        movieDao.deleteAll()
    }

}
package com.example.moviemuse.roomDb

import androidx.room.Database
import com.example.moviemuse.model.Movie

//@Database(entities = [Movie::class], version = 1)
abstract class MovieDatabase {
    companion object {
        const val NAME = "Movie_DB"
    }

    abstract fun getAll(): MovieDao
}
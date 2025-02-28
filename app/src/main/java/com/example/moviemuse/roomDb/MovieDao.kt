package com.example.moviemuse.roomDb

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.moviemuse.model.Movie

@Dao
interface MovieDao {
    @Query("SELECT * FROM Movie")
    fun getAll(): LiveData<List<Movie>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun add(movie: Movie)

    @Query("DELETE FROM Movie")
    fun deleteAll()
}
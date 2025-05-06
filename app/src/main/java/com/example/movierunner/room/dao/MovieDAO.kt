package com.example.movierunner.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.movierunner.room.model.Movie
@Dao
interface MovieDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(movie: Movie): Long

    @Insert
    suspend fun insertAll(movieList:List<Movie>): List<Long>

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(movie: Movie)

    @Delete
    suspend fun delete(movie: Movie)

    @Query("SELECT * FROM movie")
    suspend fun getAlL(): List<Movie>

    @Query("SELECT * From movie WHERE id = :movieId")
    fun getMovie(movieId: Int):Movie

    @Query("SELECT DISTINCT * FROM movie WHERE id IN (:movieIds)")
    suspend fun getAllMoviesOfActor(movieIds: List<Long>): List<Movie>


}
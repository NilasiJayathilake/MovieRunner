package com.example.movierunner.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.example.movierunner.room.model.ActorsWithMovie
import com.example.movierunner.room.model.MovieActorsCrossRef
import com.example.movierunner.room.model.MovieWithActors


@Dao
interface MovieActorsCrossRefDAO {
    @Insert
    suspend fun insert(ref: MovieActorsCrossRef)

    @Insert
    suspend fun insertAll(crossRefs:List<MovieActorsCrossRef>)

    @Transaction
    @Query("SELECT * FROM movie")
    suspend fun getMoviesWithActors(): List<MovieWithActors>

    @Transaction
    @Query("SELECT * FROM Actor")
    suspend fun getActorsWithMovies(): List<ActorsWithMovie>

    // This Query is to retrieve the movieIds of the passed Actor
    @Query("SELECT movieId FROM MovieActorsCrossRef WHERE actorId== :id")
    suspend fun getMovieIdsOfActors(id:Int): List<Long>
}
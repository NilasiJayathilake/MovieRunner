package com.example.movierunner.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.movierunner.room.model.Actor
@Dao
interface ActorDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(actor: Actor)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(actorList: List<Actor>): List<Long>

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(actor: Actor)

    @Delete
    suspend fun delete(actor: Actor)

    @Query("SELECT name FROM actor WHERE :id")
    suspend fun getActorName(id:Long): List<String>

    // Query to search for actors the Pattern will return any name with the entered sk in any part of the name
    @Query("SELECT id FROM actor WHERE name LIKE '%' || :pattern || '%'")
    suspend fun searchForActor(pattern: String): List<Long>
}
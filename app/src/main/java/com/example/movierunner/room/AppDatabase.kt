package com.example.movierunner.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.movierunner.room.dao.ActorDAO
import com.example.movierunner.room.dao.MovieActorsCrossRefDAO
import com.example.movierunner.room.dao.MovieDAO
import com.example.movierunner.room.model.Actor
import com.example.movierunner.room.model.Movie
import com.example.movierunner.room.model.MovieActorsCrossRef

@Database(entities = [Movie:: class, Actor:: class, MovieActorsCrossRef::class], version = 1)
abstract class AppDatabase : RoomDatabase(){
    abstract fun movieDao(): MovieDAO
    abstract fun actorDao(): ActorDAO
    abstract fun crossRefDAO(): MovieActorsCrossRefDAO
}
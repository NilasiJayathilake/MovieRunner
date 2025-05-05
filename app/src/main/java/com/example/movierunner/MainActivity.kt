package com.example.movierunner

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.room.Room
import com.example.movierunner.room.AppDatabase
import com.example.movierunner.screens.HomeScreen
import com.example.movierunner.screens.SearchActorScreen
import com.example.movierunner.screens.SearchScreen
import com.example.movierunner.ui.theme.MovieRunnerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "movie-database"
        ).build()
        val movieDAO = db.movieDao()
        val actorDAO = db.actorDao()
        val crossRef = db.crossRefDAO()
        setContent{
            MovieRunnerTheme(darkTheme = true) {
//                HomeScreen(movieDAO = movieDAO, actorDAO =  actorDAO, crossRefDAO =  crossRef)
//                SearchScreen(movieDAO = movieDAO, actorDAO =  actorDAO, crossRefDAO =  crossRef)
                  SearchActorScreen(movieDAO = movieDAO, actorDAO =  actorDAO, crossRefDAO =  crossRef)
            }

        }
    }

  }

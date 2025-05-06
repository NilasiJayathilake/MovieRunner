package com.example.movierunner

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.room.Room
import com.example.movierunner.room.AppDatabase
import com.example.movierunner.room.dao.ActorDAO
import com.example.movierunner.room.dao.MovieActorsCrossRefDAO
import com.example.movierunner.room.dao.MovieDAO
import com.example.movierunner.screens.HomeScreen
import com.example.movierunner.screens.SearchActorScreen
import com.example.movierunner.screens.SearchScreen
import com.example.movierunner.screens.SearchWebScreen
import com.example.movierunner.ui.theme.MovieRunnerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "movie-database"
        ).fallbackToDestructiveMigration().build()
        val movieDAO = db.movieDao()
        val actorDAO = db.actorDao()
        val crossRef = db.crossRefDAO()
        setContent{
            MovieRunnerTheme(darkTheme = true) {
                AppNavigation(movieDAO,actorDAO,crossRef)
            }

        }
    }

  }
@Composable
fun AppNavigation(
    movieDAO: MovieDAO,
    actorDAO: ActorDAO,
    crossRefDAO: MovieActorsCrossRefDAO
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                movieDAO = movieDAO,
                actorDAO = actorDAO,
                crossRefDAO = crossRefDAO,
                navController = navController // Pass navController for navigation
            )
        }
        composable(Screen.Search.route) {
            SearchScreen(
                movieDAO = movieDAO,
                actorDAO = actorDAO,
                crossRefDAO = crossRefDAO,
                navController = navController
            )
        }
        composable(Screen.SearchActor.route) {
            SearchActorScreen(
                movieDAO = movieDAO,
                actorDAO = actorDAO,
                crossRefDAO = crossRefDAO,
                navController = navController
            )
        }
        composable(Screen.SearchWeb.route){
            SearchWebScreen(
                navController = navController
            )
        }
    }
}
package com.example.movierunner

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.room.Room
import com.example.movierunner.room.AppDatabase
import com.example.movierunner.room.dao.ActorDAO
import com.example.movierunner.room.dao.MovieActorsCrossRefDAO
import com.example.movierunner.room.dao.MovieDAO
import com.example.movierunner.screens.EntryScreen
import com.example.movierunner.screens.HomeScreen
import com.example.movierunner.screens.SearchActorScreen
import com.example.movierunner.screens.SearchScreen
import com.example.movierunner.screens.SearchWebScreen
import com.example.movierunner.ui.theme.MovieRunnerTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

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
            MovieRunnerRoot(movieDAO,actorDAO ,crossRef)
        }
    }

  }
@Composable
fun AppNavigation(
    movieDAO: MovieDAO,
    actorDAO: ActorDAO,
    crossRefDAO: MovieActorsCrossRefDAO,
    isDarkTheme: Boolean,
    onToggleTheme: () -> Unit
) {
    val navController = rememberNavController()
    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.End
            ) {
                Button(onClick = onToggleTheme) {
                    Text(if (isDarkTheme) "☀️" else "🌙")
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Enter.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Enter.route) {
                EntryScreen(navController = navController)
            }
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
            composable(Screen.SearchWeb.route) {
                SearchWebScreen(
                    navController = navController
                )
            }

        }
    }
}

@Composable
fun MovieRunnerRoot(
    movieDAO: MovieDAO,
    actorDAO: ActorDAO,
    crossRefDAO: MovieActorsCrossRefDAO
) {
    var isDarkTheme by rememberSaveable { mutableStateOf(true) }

    MovieRunnerTheme(darkTheme = isDarkTheme) {
        AppNavigation(
            movieDAO = movieDAO,
            actorDAO = actorDAO,
            crossRefDAO = crossRefDAO,
            isDarkTheme = isDarkTheme,
            onToggleTheme = { isDarkTheme = !isDarkTheme }
        )
    }
}

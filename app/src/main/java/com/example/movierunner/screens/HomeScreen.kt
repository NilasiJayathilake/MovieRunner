package com.example.movierunner.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.example.movierunner.Screen
import com.example.movierunner.room.dao.ActorDAO
import com.example.movierunner.room.dao.MovieActorsCrossRefDAO
import com.example.movierunner.room.dao.MovieDAO
import com.example.movierunner.room.model.Actor
import com.example.movierunner.room.model.Movie
import com.example.movierunner.room.model.MovieActorsCrossRef
import kotlinx.coroutines.launch


@Composable
fun HomeScreen(modifier: Modifier = Modifier , movieDAO: MovieDAO ?=null, actorDAO: ActorDAO ?=null, crossRefDAO: MovieActorsCrossRefDAO?=null, navController: NavController) {
    LazyColumn (
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .then(modifier),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center){
        item{
            if (movieDAO != null && actorDAO != null && crossRefDAO != null) {
                AddMoviesToDB(movieDAO, actorDAO, crossRefDAO)
            }

            Button(onClick = {navController.navigate(Screen.Search.route)}) { Text("Search For Movies") }
        }
        item{
            Button(onClick = {navController.navigate(Screen.SearchActor.route) }) { Text("Search For Actors") }
            Button(onClick = {navController.navigate(Screen.SearchWeb.route) }) { Text("Search Movies On Web") }
        }
    }
}

//@Preview(showBackground = true)
//@Composable
//fun DarkHomeScreenPreview() {
//    MovieRunnerTheme (darkTheme = true){
//        HomeScreen()
//    }
//}
//
//@Preview(showBackground = true)
//@Composable
//private fun LightHomeScreenPreview() {
//    MovieRunnerTheme (darkTheme = false){
//        HomeScreen()
//    }
//}

@Composable
fun AddMoviesToDB(movieDAO: MovieDAO, actorDAO: ActorDAO, crossRefDAO: MovieActorsCrossRefDAO){
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    Button(onClick = {
        scope.launch {
            try {
                // Add the Movies
                val moviesToAdd = getMoviesData()
                val movieIds = movieDAO.insertAll(movieList = moviesToAdd).toList()

                // Get Actor Movie Map and Extracting the Actor Names
                val actorMovieMap = getActorMovieMap()
                val uniqueActorNames =
                    actorMovieMap.keys.toList() // Since its a Map it won't save the same actor 2 times

                // Insert all actors and get their IDs
                val actorEntities = uniqueActorNames.map { Actor(name = it) }
                val actorIds = actorDAO.insertAll(actorEntities)

                // Create name to ID mapping for actors
                val actorNameToIdMap = uniqueActorNames.zip(actorIds).toMap()

                // Create and insert cross-references
                val crossRefs = mutableListOf<MovieActorsCrossRef>()

                // Going through the Movies that are getting added, If a title matches what it has on Actor-Movie Map then that Actor is returned
                // Generated Id by adding the Actor to the actorNameToIdMap is accessed and added as the actorId
                moviesToAdd.forEachIndexed { index, movie ->
                    val movieId = movieIds[index].toInt()
                    val movieTitle = movie.title

                    // Find actors for this movie
                    actorMovieMap.forEach { (actorName, movieTitles) ->
                        if (movieTitles.contains(movieTitle)) {
                            val actorId = actorNameToIdMap[actorName]?.toInt() ?: return@forEach
                            crossRefs.add(MovieActorsCrossRef(movieId = movieId, actorId = actorId))
                        }
                    }
                }

                crossRefs.forEach {
                    crossRefDAO.insert(it)
                }
                Toast.makeText(context,"Successfully Initialized Movie Data to Local Storage", Toast.LENGTH_LONG).show()
            }catch (e: Exception){
                Toast.makeText(context,"Movie Initializing Already Performed", Toast.LENGTH_LONG).show()
                println("Initializing Failed Due To: $e")
            }
        }
        }
    ) {
        Text("Add Movies To DB") }
    }

private fun getMoviesData(): List<Movie> {
    return listOf(
        Movie(
            title = "The Shawshank Redemption",
            year = 1994,
            rated = "R",
            released = "14 Oct 1994",
            runtime = "142 min",
            genre = "Drama",
            director = "Frank Darabont",
            writer = "Stephen King, Frank Darabont",
            plot = "Two imprisoned men bond over a number of years, finding solace and eventual redemption through acts of common decency."
        ),
        Movie(
            title = "The Godfather",
            year = 1972,
            rated = "R",
            released = "24 Mar 1972",
            runtime = "175 min",
            genre = "Crime, Drama",
            director = "Francis Ford Coppola",
            writer = "Mario Puzo, Francis Ford Coppola",
            plot = "The aging patriarch of an organized crime dynasty transfers control of his clandestine empire to his reluctant son."
        ),
        Movie(
            title = "The Dark Knight",
            year = 2008,
            rated = "PG-13",
            released = "18 Jul 2008",
            runtime = "152 min",
            genre = "Action, Crime, Drama, Thriller",
            director = "Christopher Nolan",
            writer = "Jonathan Nolan, Christopher Nolan",
            plot = "When the menace known as the Joker wreaks havoc and chaos on the people of Gotham, Batman must accept one of the greatest psychological and physical tests of his ability to fight injustice."
        ),
        Movie(
            title = "The Matrix",
            year = 1999,
            rated = "R",
            released = "31 Mar 1999",
            runtime = "136 min",
            genre = "Action, Sci-Fi",
            director = "Lana Wachowski, Lilly Wachowski",
            writer = "Lana Wachowski, Lilly Wachowski",
            plot = "A computer hacker learns from mysterious rebels about the true nature of his reality and his role in the war against its controllers."
        ),
        Movie(
            title = "Batman: The Dark Knight Returns, Part 1",
            year = 2012,
            rated = "PG-13",
            released = "25 Sep 2012",
            runtime = "76 min",
            genre = "Animation, Action, Crime, Drama, Thriller",
            director = "Jay Oliva",
            writer = "Bob Kane, Frank Miller, Klaus Janson, Bob Goodman",
            plot = "Batman has not been seen for ten years. A new breed of criminal ravages Gotham City, forcing 55-year-old Bruce Wayne back into the cape and cowl. But, does he still have what it takes to fight crime in a new era?"
        )
    )
}


private fun getActorMovieMap(): Map<String, List<String>> {
    // <Actor Name , List<Movies>
    return mapOf(
        "Tim Robbins" to listOf("The Shawshank Redemption"),
        "Morgan Freeman" to listOf("The Shawshank Redemption"),
        "Bob Gunton" to listOf("The Shawshank Redemption"),
        "Marlon Brando" to listOf("The Godfather"),
        "Al Pacino" to listOf("The Godfather"),
        "James Caan" to listOf("The Godfather"),
        "Christian Bale" to listOf("The Dark Knight"),
        "Heath Ledger" to listOf("The Dark Knight"),
        "Aaron Eckhart" to listOf("The Dark Knight"),
        "Keanu Reeves" to listOf("The Matrix"),
        "Laurence Fishburne" to listOf("The Matrix"),
        "Carrie-Anne Moss" to listOf("The Matrix"),
        "Peter Weller" to listOf("Batman: The Dark Knight Returns, Part 1"),
        "Ariel Winter" to listOf("Batman: The Dark Knight Returns, Part 1"),
        "David Selby" to listOf("Batman: The Dark Knight Returns, Part 1")
    )
}

//@Composable
//fun AddMoviesToDB(
//    movieDAO: MovieDAO,
//    actorDAO: ActorDAO,
//    crossRefDAO: MovieActorsCrossRefDAO,
//    onComplete: (Boolean, String) -> Unit = { _, _ -> }
//) {
//    val scope = rememberCoroutineScope()
//    val context = LocalContext.current
//
//    Button(
//        onClick = {
//            scope.launch {
//                try {
//                    // Clear existing data if needed (optional)
//                    // movieDAO.deleteAll()
//                    // actorDAO.deleteAll()
//                    // crossRefDAO.deleteAll()
//
//                    // Define movies with complete information from the provided link
//                    val movies = getMoviesData()
//
//                    // Insert movies and get IDs
//                    val movieIds = movieDAO.insertAll(movies)
//
//                    // Process actors and relationships
//                    val actorMovieMap = getActorMovieMap()
//                    val uniqueActorNames = actorMovieMap.keys.toList()
//
//                    // Insert all actors and get their IDs
//                    val actorEntities = uniqueActorNames.map { Actor(name = it) }
//                    val actorIds = actorDAO.insertAll(actorEntities)
//
//                    // Create name to ID mapping for actors
//                    val actorNameToIdMap = uniqueActorNames.zip(actorIds).toMap()
//
//                    // Create and insert cross-references
//                    val crossRefs = mutableListOf<MovieActorsCrossRef>()
//
//                    // For each movie, find its actors and create cross-references
//                    movies.forEachIndexed { index, movie ->
//                        val movieId = movieIds[index].toInt()
//                        val movieTitle = movie.title
//
//                        // Find actors for this movie
//                        actorMovieMap.forEach { (actorName, movieTitles) ->
//                            if (movieTitles.contains(movieTitle)) {
//                                val actorId = actorNameToIdMap[actorName]?.toInt() ?: return@forEach
//                                crossRefs.add(MovieActorsCrossRef(movieId = movieId, actorId = actorId))
//                            }
//                        }
//                    }
//
//                    // Insert all cross-references
//                    crossRefDAO.insertAll(crossRefs)
//
//                    // Notify completion with success
////                    withContext(Dispatchers.Main) {
////                        onComplete(true, "Successfully added ${movies.size} movies and ${uniqueActorNames.size} actors to database")
////                        Toast.makeText(context, "Database populated successfully", Toast.LENGTH_SHORT).show()
////                    }
//                } catch (e: Exception) {
//                    // Handle errors
////                    withContext(Dispatchers.Main) {
////                        onComplete(false, "Error: ${e.localizedMessage}")
////                        Toast.makeText(context, "Error populating database: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
////                    }
//
//                }
//            }
//        },
//        modifier = Modifier
//            .fillMaxWidth(0.8f)
//            .padding(vertical = 8.dp)
//    ) {
//        Text("Add Movies To DB")
//    }
//}

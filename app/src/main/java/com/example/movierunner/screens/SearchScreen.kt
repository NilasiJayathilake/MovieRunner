package com.example.movierunner.screens

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.example.movierunner.room.dao.ActorDAO
import com.example.movierunner.room.dao.MovieActorsCrossRefDAO
import com.example.movierunner.room.dao.MovieDAO
import com.example.movierunner.room.model.Actor
import com.example.movierunner.room.model.Movie
import com.example.movierunner.room.model.MovieActorsCrossRef
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

@Composable
fun SearchScreen(modifier: Modifier = Modifier , movieDAO: MovieDAO ?=null, actorDAO: ActorDAO ?=null, crossRefDAO: MovieActorsCrossRefDAO?=null, navController: NavController){
    var searchQuery by rememberSaveable { mutableStateOf(" ") }
    var showCard by rememberSaveable { mutableStateOf(false) }
    val movieSaver = Saver<MovieJSON?, List<String>>(
        save = { movie ->
            if (movie == null) emptyList() else listOf(movie.title, movie.year, movie.rated, movie.released, movie.runtime, movie.genre, movie.director, movie.writer, movie.actors, movie.plot)
        },
        restore = { list ->
            if (list.size == 10) {
                MovieJSON(title = list[0], year = list[1], rated = list[2], released = list[3], runtime = list[4], genre = list[5], director = list[6], writer = list[7], actors = list[8], plot = list[9])
            } else null
        }
    )
    var retrievedMovie by rememberSaveable(stateSaver = movieSaver) {
        mutableStateOf(null)
    }

    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    LazyColumn (
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .then(modifier),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement =  Arrangement.Center){
        item{
            SearchBar(searchQuery = searchQuery,onQueryChange = { searchQuery = it }, label = "Search Movies")
            Button(onClick = {
                scope.launch {
                    val result = fetchMovie(searchQuery, context)
                    retrievedMovie = result
                    showCard = true
                }
            }) { Text("Retrieve Movie") }
        }
        item{
            if (showCard && retrievedMovie != null) {
                MovieDetailCard(retrievedMovie!!)
            }
        }
        item{
            if (movieDAO != null && actorDAO != null && crossRefDAO != null && retrievedMovie !=null) {
                retrievedMovie?.let {
                    SaveMovieToDB(
                        modifier = Modifier.padding(vertical = 16.dp),
                        movieDAO = movieDAO,
                        actorDAO = actorDAO,
                        crossRefDAO = crossRefDAO,
                        movieJSON = it
                    )
                }
            }else{
                Button(
                    onClick = {
                        Toast.makeText(
                            context,
                            "Please search for a movie to save to DB",
                            Toast.LENGTH_SHORT
                        ).show()
                    },
                    modifier = Modifier.padding(vertical = 16.dp)
                ) {
                    Text("Save Movie To Database")
                }
            }
        }
    }
}
suspend fun fetchMovie(keyword: String, context: Context): MovieJSON?{
    try {
        // apiKey = f173f72c
        val urlString = "https://www.omdbapi.com/?apikey=f173f72c&t=$keyword"

        val url = URL(urlString)

        val connection: HttpURLConnection = url.openConnection() as HttpURLConnection
        val stb = StringBuilder()
        // A new thread to run the code of the launched coroutine
        withContext(Dispatchers.IO) {
            val bufferedReader = BufferedReader(InputStreamReader(connection.inputStream))
            var line: String? = bufferedReader.readLine()
            while (line != null) {
                stb.append(line + "\n")
                line = bufferedReader.readLine()
            }
        }
        val searchResult = parseMovieJSON(stb)
        withContext(Dispatchers.Main) {
            Toast.makeText(context, "Retrieved Movie Successfully", Toast.LENGTH_SHORT).show()
        }
        return searchResult
    }catch (e: Exception){
        withContext(Dispatchers.Main) {
            Toast.makeText(context, "Failed to retrieve movie: $e", Toast.LENGTH_SHORT).show()
            println(e)
        }
        return null
    }
}

fun parseMovieJSON(jsonString: StringBuilder): MovieJSON {
    val json = JSONObject(jsonString.toString())

    return MovieJSON(
        title = json.getString("Title"),
        year = json.getString("Year"),
        rated = json.getString("Rated"),
        released = json.getString("Released"),
        runtime = json.getString("Runtime"),
        genre = json.getString("Genre"),
        director = json.getString("Director"),
        writer = json.getString("Writer"),
        actors = json.getString("Actors"),
        plot = json.getString("Plot")
    )
}
@Composable
fun SearchBar(
    searchQuery: String,
    onQueryChange: (String) -> Unit,
    label: String
) {
    TextField(
        value = searchQuery,
        onValueChange = onQueryChange,
        label = { Text(label) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        singleLine = true
    )
}

@Composable
fun MovieDetailCard(searchResult: MovieJSON) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        elevation = CardDefaults.cardElevation(8.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("🎬 ${searchResult.title}", style = MaterialTheme.typography.titleLarge)
            Text("Year: ${searchResult.year}", style = MaterialTheme.typography.bodyMedium)
            Text("Rated: ${searchResult.rated}")
            Text("Released: ${searchResult.released}")
            Text("Runtime: ${searchResult.runtime}")
            Text("Genre: ${searchResult.genre}")
            Text("Director: ${searchResult.director}")
            Text("Writer: ${searchResult.writer}")
            Text("Actors: ${searchResult.actors}")
            Spacer(Modifier.height(8.dp))
            Text("Plot:", style = MaterialTheme.typography.labelMedium)
            Text(searchResult.plot)
        }
    }
}

@Composable
fun SaveMovieToDB(modifier: Modifier ?= Modifier, movieDAO: MovieDAO, actorDAO: ActorDAO, crossRefDAO: MovieActorsCrossRefDAO, movieJSON: MovieJSON) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    Button(onClick = {
        scope.launch {
            try {
                // Convert MovieJSON to Movie entity & Add Movie
                val movie = Movie(
                    title = movieJSON.title,
                    year = movieJSON.year.toInt(),
                    rated = movieJSON.rated,
                    released = movieJSON.released,
                    runtime = movieJSON.runtime,
                    genre = movieJSON.genre,
                    director = movieJSON.director,
                    writer = movieJSON.writer,
                    plot = movieJSON.plot
                )
                val movieId = movieDAO.insert(movie).toInt()
                // Actors
                // Get the Actor List & Insert
                val actorNames = movieJSON.actors.split(",").map { it.trim() }
                val actorEntities = actorNames.map { Actor(name = it) }
                val actorIds = actorDAO.insertAll(actorEntities)
                // Create name to ID mapping for actors
                val actorNameToIdMap = actorNames.zip(actorIds).toMap()
                // Create and insert cross-references for this movie
                val crossRefs = actorNames.mapNotNull { actorName ->
                    val actorId = actorNameToIdMap[actorName]?.toInt() ?: return@mapNotNull null

                    MovieActorsCrossRef(movieId = movieId, actorId = actorId)
                }

                // Insert all cross-references
                crossRefs.forEach { crossRefDAO.insert(it) }

                Toast.makeText(
                    context,
                    "Successfully added ${movieJSON.title} to database",
                    Toast.LENGTH_LONG
                ).show()
            }catch (e: Exception){
                Toast.makeText(
                    context,
                    "Failed to add ${movieJSON.title} to database",
                    Toast.LENGTH_LONG
                ).show()
            }}
    }, enabled = true,
        modifier = Modifier.padding(top = 16.dp)){
        Text("Save Movie To Database")
    }
}


data class MovieJSON(
                     val title: String,
                     val year: String,
                     val rated: String,
                     val released: String,
                     val runtime: String,
                     val genre: String,
                     val director: String,
                     val writer: String,
                     val actors: String,
                     val plot: String)
//@Preview
//@Composable
//fun PreviewSearchScreen(modifier: Modifier = Modifier) {
//   MovieRunnerTheme(darkTheme = true) {
//       SearchScreen()
//   }
//
//
//}

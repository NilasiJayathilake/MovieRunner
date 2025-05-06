package com.example.movierunner.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.movierunner.room.dao.ActorDAO
import com.example.movierunner.room.dao.MovieActorsCrossRefDAO
import com.example.movierunner.room.dao.MovieDAO
import com.example.movierunner.room.model.Actor
import com.example.movierunner.room.model.Movie
import com.example.movierunner.ui.theme.MovieRunnerTheme
import kotlinx.coroutines.launch

// Searching For Actor will Return the list of Movies they act
/*  Logic:
    by Accessing actorDAO.search we return all the IDs of the Actors who have part of the String used to Search
    Those IDs are used to return the Movie IDs using crossRefDAO.get(Id)
    The returned Movies are shown in a List
 */
@Composable
fun SearchActorScreen (modifier: Modifier = Modifier, movieDAO: MovieDAO?=null, actorDAO: ActorDAO?=null, crossRefDAO: MovieActorsCrossRefDAO?=null,
                       navController: NavController?){
    var searchActorQuery by rememberSaveable { mutableStateOf("") }
    var searchResults by rememberSaveable { mutableStateOf<List<Movie>>(emptyList()) }
    var matchedActors by rememberSaveable { mutableStateOf<List<String>>(emptyList()) }
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.background)
            .padding(16.dp)
            .then(modifier),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        item{
            Text(
                "Search Movies by Actor",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            SearchBar(searchQuery = searchActorQuery,onQueryChange = { searchActorQuery = it }, label = "Search For Actor")
            if (movieDAO != null && actorDAO != null && crossRefDAO != null) {
                SearchMoviesByActor(movieDAO, actorDAO, crossRefDAO, searchActorQuery){ results, actors ->
                    searchResults = results
                    println(results)
                    matchedActors = actors
                    println(actors)
                }

            }
        }


            println("In Lazy Column: $searchResults")
            items(searchResults) { movie ->
                MovieListItem(
                    movie = movie
                )
            }

        item{
            Text(text="Matched Actors:",  color =  MaterialTheme.colorScheme.secondary)
            Row(
                modifier = Modifier
                    .horizontalScroll(rememberScrollState())
                    .padding(vertical = 8.dp)
            ) {
                matchedActors.forEach { actor ->
                    Text(
                        text = actor,
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .background(MaterialTheme.colorScheme.secondary, RoundedCornerShape(8.dp))
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        style = MaterialTheme.typography.bodySmall,

                    )
                }
            }
        }

    }
}

@Composable
fun SearchMoviesByActor(movieDAO: MovieDAO, actorDAO: ActorDAO, crossRefDAO: MovieActorsCrossRefDAO, searchQuery: String,   onResults: (List<Movie>, List<String>) -> Unit){
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    var isSearching by rememberSaveable { mutableStateOf(false) }
    var searchResults by rememberSaveable { mutableStateOf<List<Movie>>(emptyList()) }
    Button(onClick = {
        onResults(emptyList(), emptyList())
        scope.launch {
            isSearching = true
            try {
                // First retrieving all possible actors Ids
                val actorIds =  actorDAO.searchForActor(searchQuery)

                val matchingActors = actorIds.flatMap { actorId ->
                    actorDAO.getActorName(actorId).filter { actorName ->
                        actorName.contains(searchQuery, ignoreCase = true)
                    }
                }.distinct()
                println("macthing Actors: $matchingActors")

                // Getting All movieIds of each actor and adding it to the movieIdSet
                val movieIdSet = mutableSetOf<Long>()

                actorIds.forEach{
                    actorId ->
                    val movieIds = crossRefDAO.getMovieIdsOfActor(actorId.toInt())
                    movieIdSet.addAll(movieIds)
                }
                // Retrieving all the movies with the above movieIds
                searchResults =  movieDAO.getAllMoviesOfActor(movieIdSet.toList()).distinctBy { it.id }
                println("After macthing Actors: $matchingActors")
                onResults(searchResults, matchingActors)
                if (searchResults.isEmpty()) {
                    Toast.makeText(
                        context,
                        "Movies Not found with actor: $searchQuery",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            } catch (e: Exception) {
                Toast.makeText(
                    context,
                    "Error searching: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            } finally {
                isSearching = false
            }
        }
    }){ Text("Search") }

}

@Composable
fun MovieListItem(movie: Movie) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = movie.title,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Year: ${movie.year}",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Director: ${movie.director}",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewSearchActor(modifier: Modifier = Modifier) {
    MovieRunnerTheme (darkTheme = true){
        SearchActorScreen(navController = null)
    }
}


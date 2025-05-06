package com.example.movierunner.screens

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import androidx.compose.foundation.lazy.items

@Composable
fun SearchWebScreen(modifier: Modifier = Modifier, navController: NavController ) {
    var searchQuery by rememberSaveable { mutableStateOf("") }
    var webResults by rememberSaveable { mutableStateOf(listOf<MovieSummary>()) }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    LazyColumn (
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .then(modifier),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement =  Arrangement.Top
    ){
        item {
            SearchBar(
                searchQuery = searchQuery,
                onQueryChange = { searchQuery = it },
                label = "Search Movies On Web"
            )
            Button(onClick = {scope.launch {
                val result  = searchWeb(searchQuery.trim(), context)
                webResults = result ?: emptyList()
                println(webResults)
            }

            }) {
                Text("Search the Web")
            }
        }
         items(webResults) {
                movie ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Column(Modifier.padding(12.dp)) {
                    Text(movie.title, style = MaterialTheme.typography.titleMedium)
                    Text("Year: ${movie.year}")
                    Text("IMDb ID: ${movie.imdbID}")
                }
            }
        }
    }
}

suspend fun searchWeb(keyword: String, context: Context): List<MovieSummary>? {
    try {
        // apiKey = f173f72c
        val urlString = "https://www.omdbapi.com/?apikey=f173f72c&s=$keyword"

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
        val searchResult = parseWebJSON(stb)
        println("JSON Response:\n$stb")

        withContext(Dispatchers.Main) {
            Toast.makeText(context, "Retrieved Movies Successfully", Toast.LENGTH_SHORT).show()
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
fun parseWebJSON(jsonString: StringBuilder): List<MovieSummary> {
    val resultList = mutableListOf<MovieSummary>()
    val json = JSONObject(jsonString.toString())

    if (json.has("Search")) {
        val searchArray = json.getJSONArray("Search")

        for (i in 0 until minOf(searchArray.length(), 10)) {
            val item = searchArray.getJSONObject(i)

            resultList.add(
                MovieSummary(
                    title = item.getString("Title"),
                    year = item.getString("Year"),
                    imdbID = item.getString("imdbID"),
                )
            )
        }
    }

    return resultList
}

data class MovieSummary(
    val title: String,
    val year: String,
    val imdbID: String
)

//@Preview
//@Composable
//private fun PreviewWebSearch() {
//    MovieRunnerTheme(true) {
//        SearchWebScreen(modifier = Modifier)
//    }
//}
@Composable
fun MovieSearchResults(movies: List<MovieSummary>) {
    LazyColumn {
        items(movies) {
            movie ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Column(Modifier.padding(12.dp)) {
                    Text(movie.title, style = MaterialTheme.typography.titleMedium)
                    Text("Year: ${movie.year}")
                    Text("IMDb ID: ${movie.imdbID}")
                }
            }
        }
    }
}

package com.example.movierunner.screens

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.movierunner.room.dao.ActorDAO
import com.example.movierunner.room.dao.MovieActorsCrossRefDAO
import com.example.movierunner.room.dao.MovieDAO
import com.example.movierunner.room.model.Movie

// Searching For Actor will Return the list of Movies they act
/*  Logic:
    by Accessing actorDAO.search we return all the IDs of the Actors who have part of the String used to Search
    Those IDs are used to return the Movie IDs using crossRefDAO.get(Id)
    The returned Movies are shown in a List
 */
@Composable
fun SearchActorScreen (modifier: Modifier = Modifier, movieDAO: MovieDAO?=null, actorDAO: ActorDAO?=null, crossRefDAO: MovieActorsCrossRefDAO?=null, onMovieSelected: (Movie) -> Unit = {}){

}


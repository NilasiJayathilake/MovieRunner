package com.example.movierunner.room.repository

import com.example.movierunner.room.dao.MovieDAO
import com.example.movierunner.room.model.Movie

class MovieRepository(private val movieDAO: MovieDAO) {
    suspend fun getAllMovies(): List<Movie> = movieDAO.getAlL();

    suspend fun insertMovie(movie: Movie) = movieDAO.insert(movie)

    suspend fun insertMovies(movies: List<Movie>) = movieDAO.insertAll(movies)

    suspend fun deleteMovie(movie: Movie) = movieDAO.delete(movie)
}
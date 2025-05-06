package com.example.movierunner.room.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "movie", indices = [Index(value = ["title", "year"], unique = true)])
data class Movie( @PrimaryKey (autoGenerate = true) var id: Int = 0,
                 var title: String, var year: Int, var rated: String, var released: String,
                 var runtime: String, var genre: String, var director: String, var writer: String, var plot: String) {
}

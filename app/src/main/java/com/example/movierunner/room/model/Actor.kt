package com.example.movierunner.room.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Actor (@PrimaryKey(autoGenerate = true) var id: Int=0, var name: String){
}
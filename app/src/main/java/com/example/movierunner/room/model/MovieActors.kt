package com.example.movierunner.room.model

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Junction
import androidx.room.Relation

@Entity(primaryKeys = ["movieId", "actorId"])
data class MovieActorsCrossRef(val movieId: Int, val actorId: Int) {
}

data class MovieWithActors(
    @Embedded val movie: Movie,
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            value = MovieActorsCrossRef::class,
            parentColumn = "movieId", // <- property in CrossRef
            entityColumn = "actorId"
        )
    )
    var actors:List<Actor>
)

data class ActorsWithMovie(
    @Embedded val actor: Actor,
    @Relation(
         parentColumn = "id",
         entityColumn = "id",
         associateBy = Junction(
             value = MovieActorsCrossRef::class,
             parentColumn = "actorId",
             entityColumn = "movieId")
     )
    var movies: List<Movie>
)

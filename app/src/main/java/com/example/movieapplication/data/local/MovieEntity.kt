package com.example.movieapplication.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.movieapplication.data.model.Movie

/**
 * Room Entity საყვარელი ფილმების შესანახად.
 * შეესაბამება Movie მოდელს, მაგრამ არ შეიცავს isFavorite-ს (რადგან ცხრილში ყოფნა უკვე ნიშნავს ფავორიტს).
 */
@Entity(tableName = "favorites")
data class MovieEntity(
    @PrimaryKey val id: Int,
    val title: String,
    val overview: String,
    val posterPath: String?,
    val backdropPath: String?,
    val voteAverage: Double,
    val releaseDate: String
) {
    /**
     * MovieEntity-დან Movie მოდელზე გადაყვანა
     * (isFavorite ყოველთვის true, რადგან ბაზაშია)
     */
    fun toMovie(): Movie = Movie(
        id = id,
        title = title,
        overview = overview,
        posterPath = posterPath,
        backdropPath = backdropPath,
        voteAverage = voteAverage,
        releaseDate = releaseDate,
        isFavorite = true
    )

    companion object {
        /** Movie მოდელიდან Room Entity-ზე გადაყვანა (ფავორიტად შესანახად) */
        fun fromMovie(movie: Movie): MovieEntity = MovieEntity(
            id = movie.id,
            title = movie.title,
            overview = movie.overview,
            posterPath = movie.posterPath,
            backdropPath = movie.backdropPath,
            voteAverage = movie.voteAverage,
            releaseDate = movie.releaseDate
        )
    }
}
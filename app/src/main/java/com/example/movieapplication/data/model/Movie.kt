package com.example.movieapplication.data.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

/**
 * ფილმის მთავარი მოდელი TMDB API-დან
 * გამოიყენება სიებში (now playing, popular, search) და Details ეკრანზე
 */
@Parcelize
data class Movie(
    val id: Int,

    val title: String,

    val overview: String,

    @SerializedName("poster_path")
    val posterPath: String?,

    @SerializedName("backdrop_path")
    val backdropPath: String?,

    @SerializedName("vote_average")
    val voteAverage: Double,

    @SerializedName("release_date")
    val releaseDate: String,

    @SerializedName("genre_ids")
    val genreIds: List<Int>? = null,

    var isFavorite: Boolean = false
) : Parcelable
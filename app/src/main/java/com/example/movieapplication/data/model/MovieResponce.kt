package com.example.movieapplication.data.model

import com.google.gson.annotations.SerializedName

/**
 * TMDB API-ს პასუხი ფილმების სიის მოთხოვნებზე
 * (now_playing, popular, search, discover/movie)
 */
data class MovieResponse(
    val page: Int,

    val results: List<Movie>,

    @SerializedName("total_pages")
    val totalPages: Int,

    @SerializedName("total_results")
    val totalResults: Int
)
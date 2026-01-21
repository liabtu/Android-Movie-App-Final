package com.example.movieapplication.data.model

/**
 * TMDB API-ს პასუხი ჟანრების სიის მოთხოვნაზე (/genre/movie/list)
 * შეიცავს ყველა ჟანრის სიას
 */
data class GenreResponse(
    val genres: List<Genre>             // ჟანრების მასივი
)
package com.example.movieapplication.data.repository

import com.example.movieapplication.data.local.AppDatabase
import com.example.movieapplication.data.local.MovieEntity
import com.example.movieapplication.data.model.GenreResponse
import com.example.movieapplication.data.model.Movie
import com.example.movieapplication.data.model.MovieResponse
import com.example.movieapplication.data.remote.MovieApi
import com.example.movieapplication.util.Constants
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import retrofit2.Response

/**
 * რეპოზიტორი — ცენტრალური ადგილი მონაცემების მართვისთვის.
 * აკავშირებს:
 *  - Remote API (Retrofit) — ფილმების/ჟანრების მიღება TMDB-დან
 *  - Local Database (Room) — საყვარელი ფილმების შენახვა/წაშლა
 */
class MovieRepository(
    private val api: MovieApi,
    private val database: AppDatabase
) {
    val genreMap = mutableMapOf<Int, String>()


    // Remote API მოთხოვნები (TMDB-დან)
    /**  მიმდინარე ფილმების მიღება */
    suspend fun getNowPlaying(page: Int = 1): Response<MovieResponse> {
        return api.getNowPlaying(Constants.API_KEY, page)
    }

    /** პოპულარული ფილმების მიღება */
    suspend fun getPopularMovies(page: Int = 1): Response<MovieResponse> {
        return api.getPopularMovies(Constants.API_KEY, page)
    }

    /** ფილმების ძებნა საძიებო სიტყვით */
    suspend fun searchMovies(query: String, page: Int = 1): Response<MovieResponse> {
        return api.searchMovies(Constants.API_KEY, query, page)
    }

    /** ფილმების მიღება კონკრეტული ჟანრის მიხედვით */
    suspend fun getMoviesByGenre(genreId: Int, page: Int = 1): Response<MovieResponse> {
        return api.getMoviesByGenre(Constants.API_KEY, genreId, page)
    }

    /** ჟანრების სრული სიის ჩატვირთვა TMDB-დან */
    suspend fun loadGenres(): Boolean {
        return try {
            val response = api.getGenres()
            if (response.isSuccessful) {
                response.body()?.genres?.forEach { genre ->
                    genreMap[genre.id] = genre.name
                }
                true
            } else {
                false
            }
        } catch (e: Exception) {
            // Log.e("MovieRepository", "Genres loading failed", e)
            false
        }
    }

    /** ჟანრის ID-ებიდან სახელების მიღება (ქეშიდან) */
    fun getGenreNames(genreIds: List<Int>?): String {
        if (genreIds.isNullOrEmpty() || genreMap.isEmpty()) return ""
        return genreIds.mapNotNull { genreMap[it] }.joinToString(", ")
    }

    // Local Database (Room) მეთოდები — საყვარელი ფილმები
    /** ყველა საყვარელი ფილმის მიღება (Flow-ით, რეალურ დროში განახლებისთვის) */
    fun getFavoriteMovies(): Flow<List<Movie>> {
        return database.movieDao().getAllFavorites().map { entities ->
            entities.map { it.toMovie() }
        }
    }

    suspend fun addToFavorites(movie: Movie) {
        database.movieDao().insert(MovieEntity.fromMovie(movie))
    }

    suspend fun removeFromFavorites(movie: Movie) {
        database.movieDao().delete(MovieEntity.fromMovie(movie))
    }

    suspend fun isFavorite(movieId: Int): Boolean {
        return database.movieDao().getById(movieId) != null
    }
}
package com.example.movieapplication.data.remote

import com.example.movieapplication.data.model.GenreResponse
import com.example.movieapplication.data.model.MovieResponse
import com.example.movieapplication.util.Constants
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

/** Retrofit ინტერფეისი TMDB API-სთან კომუნიკაციისთვის. */
interface MovieApi {

    /** მიმდინარე ფილმების მიღება */
    @GET("movie/now_playing")
    suspend fun getNowPlaying(
        @Query("api_key") apiKey: String = Constants.API_KEY,
        @Query("page") page: Int = 1
    ): Response<MovieResponse>

    /** პოპულარული ფილმების მიღება (/movie/popular) */
    @GET("movie/popular")
    suspend fun getPopularMovies(
        @Query("api_key") apiKey: String = Constants.API_KEY,
        @Query("page") page: Int = 1
    ): Response<MovieResponse>

    /** ფილმების ძებნა საძიებო სიტყვით (/search/movie) */
    @GET("search/movie")
    suspend fun searchMovies(
        @Query("api_key") apiKey: String = Constants.API_KEY,
        @Query("query") query: String,
        @Query("page") page: Int = 1
    ): Response<MovieResponse>

    /**
     * ყველა ჟანრის სიის მიღება (/genre/movie/list)
     * ეს სია იყენება ჟანრის ID-ების სახელებად გადაყვანისთვის
     */
    @GET("genre/movie/list")
    suspend fun getGenres(
        @Query("api_key") apiKey: String = Constants.API_KEY
    ): Response<GenreResponse>

    /** ფილმების მიღება კონკრეტული ჟანრის მიხედვით (/discover/movie) */
    @GET("discover/movie")
    suspend fun getMoviesByGenre(
        @Query("api_key") apiKey: String = Constants.API_KEY,
        @Query("with_genres") genreId: Int,
        @Query("page") page: Int = 1
    ): Response<MovieResponse>
}
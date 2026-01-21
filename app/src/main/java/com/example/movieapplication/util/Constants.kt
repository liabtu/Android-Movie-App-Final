package com.example.movieapplication.util

import com.example.movieapplication.BuildConfig
/**
 * აპლიკაციის მუდმივები (constants).
 * შეიცავს API-ს ბაზურ მისამართებს, გასაღებს და სხვა ფიქსირებულ მნიშვნელობებს.
 * API_KEY აღებულია BuildConfig-დან (local.properties-ის უსაფრთხოებისთვის)
 */
object Constants {
    const val BASE_URL = "https://api.themoviedb.org/3/"
    const val API_KEY = BuildConfig.TMDB_API_KEY
    const val IMAGE_BASE_URL = "https://image.tmdb.org/t/p/w500"

}
package com.example.movieapplication.data.remote

import com.example.movieapplication.util.Constants
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Retrofit-ის ინსტანსის შექმნა.
 * შეიცავს:
 *  - HTTP ლოგირებას (debug რეჟიმში)
 *  - Base URL TMDB-სთვის
 *  - Gson კონვერტერს JSON-ის პარსინგისთვის
 */
object RetrofitInstance {

    /** Retrofit ობიექტი lazy-ით (შეიქმნება პირველ გამოძახებაზე) */
    private val retrofit by lazy {
        // HTTP ლოგირება (debug-ისთვის ჩანს მოთხოვნები და პასუხები)
        val logging = HttpLoggingInterceptor()
        logging.setLevel(HttpLoggingInterceptor.Level.BODY)

        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()

        Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
    }

    /**  MovieApi ინტერფეისის ინსტანსი, გამოიყენება ყველა API მოთხოვნისთვის */
    val api: MovieApi by lazy {
        retrofit.create(MovieApi::class.java)
    }
}
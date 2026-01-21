package com.example.movieapplication.data.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

/**
 * ჟანრის მოდელი TMDB API-დან
 * გამოიყენება ჟანრების სიისთვის (genre/movie/list) და ფილმის ჟანრების ID-ების სახელებად გადაყვანისთვის
 */
@Parcelize
data class Genre(
    val id: Int,
    @SerializedName("name")
    val name: String
) : Parcelable
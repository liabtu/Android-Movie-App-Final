package com.example.movieapplication.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

/**
 * Room DAO ინტერფეისი საყვარელი ფილმების მართვისთვის.
 * იყენებს Flow-ს რეალურ დროში განახლებისთვის.
 */
@Dao
interface MovieDao {

    @Query("SELECT * FROM favorites")
    fun getAllFavorites(): Flow<List<MovieEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(movie: MovieEntity)

    @Delete
    suspend fun delete(movie: MovieEntity)

    @Query("SELECT * FROM favorites WHERE id = :id")
    suspend fun getById(id: Int): MovieEntity?
}
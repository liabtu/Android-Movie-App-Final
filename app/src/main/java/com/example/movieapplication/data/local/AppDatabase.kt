package com.example.movieapplication.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * Room Database კლასი აპლიკაციისთვის.
 * შეიცავს ერთ ცხრილს: favorites (MovieEntity)
 */
@Database(entities = [MovieEntity::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    /** DAO ინტერფეისი ფილმების ოპერაციებისთვის */
    abstract fun movieDao(): MovieDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        /**
         * Singleton-ის მიღება (thread-safe)
         * @return AppDatabase ინსტანსი
         */
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "movie_database"  // ბაზის სახელი
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
package com.example.movieapplication

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.movieapplication.data.local.AppDatabase
import com.example.movieapplication.data.remote.RetrofitInstance
import com.example.movieapplication.data.repository.MovieRepository
import com.example.movieapplication.databinding.ActivityMainBinding
import com.example.movieapplication.util.ThemePreferences
import kotlinx.coroutines.launch

/**
 * აპლიკაციის მთავარი აქტივობა.
 *
 * - ინიციალიზებს თემას (Light/Dark) SharedPreferences-დან
 * - აყენებს Navigation Component-ს + Bottom Navigation
 * - ჩატვირთავს ჟანრების სიას TMDB-დან აპლიკაციის დაწყებისას
 *   (რომ Details და სხვა ეკრანებზე ჟანრები უკვე მზად იყოს)
 */
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        // თემის ინიციალიზაცია ყველაზე ადრე (super.onCreate-მდე!)
        // ეს უზრუნველყოფს, რომ Splash ეკრანზეც სწორი თემა იყოს
        val isDark = ThemePreferences.isDarkMode(this)
        AppCompatDelegate.setDefaultNightMode(
            if (isDark) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO
        )

        super.onCreate(savedInstanceState)

        // ViewBinding-ის ინიციალიზაცია
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Navigation Component-ის დაყენება
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment

        val navController = navHostFragment.navController
        binding.bottomNavigation.setupWithNavController(navController)

        // ჟანრების წინასწარ ჩატვირთვა აპლიკაციის დაწყებისას
        // ეს ერთჯერადი ოპერაციაა — ჟანრები შეინახება ქეშში (genreMap)
        lifecycleScope.launch {
            val repository = MovieRepository(
                RetrofitInstance.api,
                AppDatabase.getDatabase(this@MainActivity)
            )
            repository.loadGenres()
        }
    }
}
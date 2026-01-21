package com.example.movieapplication.util

import android.content.Context

/**
 * SharedPreferences-ის მენეჯერი თემის (Dark/Light) შესანახად.
 * ინახავს მომხმარებლის არჩევანს აპლიკაციის გადატვირთვის შემდეგაც.
 */
object ThemePreferences {

    private const val PREF_NAME = "theme_prefs"
    private const val KEY_DARK_MODE = "dark_mode"

    /**თემის შეცვლა და შენახვა*/
    fun setDarkMode(context: Context, isDark: Boolean) {
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .edit()
            .putBoolean(KEY_DARK_MODE, isDark)
            .apply()  // apply() ასინქრონულია და უფრო სწრაფი
    }

    /**ამოწმებს მიმდინარე თემას SharedPreferences-დან*/
    fun isDarkMode(context: Context): Boolean {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .getBoolean(KEY_DARK_MODE, false)  // ნაგულისხმევი: Light Mode
    }
}
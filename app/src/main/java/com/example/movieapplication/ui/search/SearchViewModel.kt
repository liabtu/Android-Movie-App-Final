package com.example.movieapplication.ui.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.movieapplication.data.model.Movie
import com.example.movieapplication.data.repository.MovieRepository
import kotlinx.coroutines.launch

/**
 * ViewModel ფილმების ძებნის ეკრანისთვის (SearchFragment).
 * - მართავს ძებნის მოთხოვნებს (Retrofit-ით)
 * - აჩვენებს loading სტატუსს
 * - ინახავს და განაახლებს საყვარელი ფილმების სიას
 * - მხარს უჭერს ფავორიტის გადართვას
 */
class SearchViewModel(
    private val repository: MovieRepository
) : ViewModel() {

    // ძებნის შედეგები (LiveData — UI-სთვის)
    private val _searchResults = MutableLiveData<List<Movie>>()
    val searchResults: LiveData<List<Movie>> = _searchResults

    // Loading სტატუსი (ProgressBar-ისთვის)
    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    // საყვარელი ფილმების სია (Room-დან Flow-ით)
    private val _favorites = MutableLiveData<List<Movie>>()
    val favorites: LiveData<List<Movie>> = _favorites

    init {
        // საყვარელი ფილმების რეალურ დროში დაკვირვება
        observeFavorites()
    }

    /** ფილმების ძებნა TMDB-ში */
    fun searchMovies(query: String) {
        if (query.isBlank()) return

        viewModelScope.launch {
            _loading.value = true
            val response = repository.searchMovies(query)
            if (response.isSuccessful) {
                _searchResults.value = response.body()?.results ?: emptyList()
            }
            _loading.value = false
        }
    }

    /**  საყვარელი ფილმების Flow-ის დაკვირთვა და LiveData-ში გადატანა */
    private fun observeFavorites() {
        viewModelScope.launch {
            repository.getFavoriteMovies().collect { favs ->
                _favorites.postValue(favs)
            }
        }
    }

    /** ფავორიტის სტატუსის გადართვა */
    fun toggleFavorite(movie: Movie) {
        viewModelScope.launch {
            movie.isFavorite = !movie.isFavorite
            if (movie.isFavorite) {
                repository.addToFavorites(movie)
            } else {
                repository.removeFromFavorites(movie)
            }
        }
    }

    /** ViewModel-ის Factory — რეპოზიტორის გადაცემა */
    class Factory(private val repository: MovieRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(SearchViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return SearchViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel")
        }
    }
}
package com.example.movieapplication.ui.watchlist

import androidx.lifecycle.*
import com.example.movieapplication.data.model.Movie
import com.example.movieapplication.data.repository.MovieRepository
import kotlinx.coroutines.launch

/**
 * ViewModel საყვარელი ფილმების ეკრანისთვის (WatchlistFragment).
 * - მართავს საყვარელი ფილმების სიას (Room-დან Flow-ით)
 * - აკონტროლებს ფავორიტის სტატუსის გადართვას (დამატება/წაშლა)
 * - ავტომატურად განაახლებს სიას, როცა ბაზაში რამე იცვლება
 */
class WatchlistViewModel(private val repository: MovieRepository) : ViewModel() {

    // საყვარელი ფილმების სია (LiveData — UI-სთვის)
    private val _watchlist = MutableLiveData<List<Movie>>()
    val watchlist: LiveData<List<Movie>> = _watchlist

    init {
        observeWatchlist()
    }

    /**
     * Room-ის Flow-ის დაკვირთვა და LiveData-ში გადატანა
     * - ავტომატურად განახლდება, როცა ბაზაში ფავორიტი იცვლება
     */
    private fun observeWatchlist() {
        viewModelScope.launch {
            repository.getFavoriteMovies().collect { movies ->
                _watchlist.postValue(movies)
            }
        }
    }

    /** ფავორიტის სტატუსის გადართვა */
    fun toggleFavorite(movie: Movie) {
        viewModelScope.launch {
            val isFav = repository.isFavorite(movie.id)
            if (isFav) {
                repository.removeFromFavorites(movie)
            } else {
                repository.addToFavorites(movie)
            }
        }
    }

    /** ViewModel-ის Factory — რეპოზიტორის გადაცემა */
    class Factory(private val repository: MovieRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(WatchlistViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return WatchlistViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
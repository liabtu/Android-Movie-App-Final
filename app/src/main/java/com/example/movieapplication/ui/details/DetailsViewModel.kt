package com.example.movieapplication.ui.details

import androidx.lifecycle.*
import com.example.movieapplication.data.model.Movie
import com.example.movieapplication.data.repository.MovieRepository
import kotlinx.coroutines.launch

/**
 * ViewModel ფილმის დეტალური ეკრანისთვის (DetailsFragment).
 * - მართავს ფავორიტის სტატუსს (შემოწმება, დამატება/წაშლა)
 * - იყენებს Repository-ს Room-თან კომუნიკაციისთვის
 */
class DetailsViewModel(val repository: MovieRepository) : ViewModel() {

    // LiveData ფავორიტის სტატუსისთვის (UI-სთვის)
    private val _isFavorite = MutableLiveData<Boolean>()
    val isFavorite: LiveData<Boolean> = _isFavorite

    fun checkFavoriteStatus(movieId: Int) {
        viewModelScope.launch {
            val isFav = repository.isFavorite(movieId)
            _isFavorite.postValue(isFav)
        }
    }

    /** ფავორიტის სტატუსის გადართვა (დამატება ან წაშლა) */
    fun toggleFavorite(movie: Movie) {
        viewModelScope.launch {
            val currentStatus = repository.isFavorite(movie.id)
            if (currentStatus) {
                repository.removeFromFavorites(movie)
                _isFavorite.postValue(false)
            } else {
                repository.addToFavorites(movie)
                _isFavorite.postValue(true)
            }
        }
    }

    /** ViewModel-ის Factory — რეპოზიტორის გადაცემა */
    class Factory(private val repository: MovieRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(DetailsViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return DetailsViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
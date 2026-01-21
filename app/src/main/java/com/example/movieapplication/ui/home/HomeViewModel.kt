package com.example.movieapplication.ui.home

import androidx.lifecycle.*
import com.example.movieapplication.data.model.Movie
import com.example.movieapplication.data.repository.MovieRepository
import kotlinx.coroutines.launch

/**
 * ViewModel მთავარი ეკრანისთვის (HomeFragment).
 * მართავს ახალ და პოპულარულ ფილმებს
 * მხარს უჭერს პაგინაციას (infinite scroll)
 * ფავორიტის გადართვა
 * ჟანრის ფილტრაცია (discover API-ით)
 * ძებნა (searchMovies)
 */
class HomeViewModel(val repository: MovieRepository) : ViewModel() {

    // ახალი ფილმები (Featured + New Movies)
    private val _nowPlaying = MutableLiveData<List<Movie>>()
    val nowPlaying: LiveData<List<Movie>> = _nowPlaying

    // პოპულარული ფილმები (ფილტრირებული ან ძებნილი)
    private val _popularMovies = MutableLiveData<List<Movie>>()
    val popularMovies: LiveData<List<Movie>> = _popularMovies

    // საყვარელი ფილმები (Room-დან, რეალურ დროში)
    val favorites = repository.getFavoriteMovies().asLiveData()

    // პაგინაციის ცვლადები
    private var currentPopularPage = 1
    private var isFetchingPopular = false
    private var isSearching = false

    // ყველა ჩატვირთული პოპულარული ფილმი
    private val allPopularMovies = mutableListOf<Movie>()

    init {
        fetchMovies()
    }

    /**
     * საწყისი ჩატვირთვა ან ძებნის გაუქმებისას
     * ახალი ფილმები + პოპულარული
     */
    fun fetchMovies() {
        isSearching = false
        fetchNowPlaying()
        // თუ სია ცარიელია — ჩატვირთოს პირველი გვერდი
        if (allPopularMovies.isEmpty()) {
            fetchNextPopularPage()
        } else {
            _popularMovies.postValue(allPopularMovies.toList())
        }
    }

    /** ახალი ფილმების ჩატვირთვა */
    private fun fetchNowPlaying() {
        viewModelScope.launch {
            val response = repository.getNowPlaying(1)
            if (response.isSuccessful) _nowPlaying.postValue(response.body()?.results)
        }
    }

    /** პოპულარული ფილმების შემდეგი გვერდის ჩატვირთვა (infinite scroll) */
    fun fetchNextPopularPage() {
        if (isFetchingPopular || isSearching) return
        isFetchingPopular = true

        viewModelScope.launch {
            try {
                val response = repository.getPopularMovies(currentPopularPage)
                if (response.isSuccessful) {
                    val newMovies = response.body()?.results ?: emptyList()
                    allPopularMovies.addAll(newMovies)
                    _popularMovies.postValue(allPopularMovies.toList())
                    currentPopularPage++
                }
            } finally {
                isFetchingPopular = false
            }
        }
    }

    /**
     * ფავორიტის სტატუსის გადართვა
     * თუ ფავორიტია → წაშლა
     * თუ არა → დამატება
     */
    fun toggleFavorite(movie: Movie) {
        viewModelScope.launch {
            if (repository.isFavorite(movie.id)) {
                repository.removeFromFavorites(movie)
            } else {
                repository.addToFavorites(movie)
            }
        }
    }

    /** ფილმების მიღება კონკრეტული ჟანრის მიხედვით (discover API) */
    fun fetchMoviesByGenre(genreId: Int) {
        viewModelScope.launch {
            val response = repository.getMoviesByGenre(genreId)
            if (response.isSuccessful) {
                _popularMovies.postValue(response.body()?.results ?: emptyList())
            }
        }
    }

    /** უკან დაბრუნება ყველა პოპულარულ ფილმზე */
    fun fetchPopularMovies() {
        viewModelScope.launch {
            val response = repository.getPopularMovies()
            if (response.isSuccessful) {
                _popularMovies.postValue(response.body()?.results ?: emptyList())
            }
        }
    }

    /**
     * ფილმების ძებნა საძიებო სიტყვით
     * - თუ ცარიელია — უკან popular
     */
    fun searchMovies(query: String) {
        if (query.isEmpty()) {
            fetchMovies()
            return
        }

        isSearching = true
        viewModelScope.launch {
            try {
                val response = repository.searchMovies(query, 1)
                if (response.isSuccessful) {
                    _popularMovies.postValue(response.body()?.results ?: emptyList())
                }
            } catch (e: Exception) {
            }
        }
    }

    /** ViewModel-ის Factory — რეპოზიტორის გადაცემა */
    class Factory(private val repository: MovieRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return HomeViewModel(repository) as T
        }
    }
}
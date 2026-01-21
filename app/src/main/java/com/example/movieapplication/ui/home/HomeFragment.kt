package com.example.movieapplication.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.movieapplication.adapter.MovieAdapter
import com.example.movieapplication.data.local.AppDatabase
import com.example.movieapplication.data.remote.RetrofitInstance
import com.example.movieapplication.data.repository.MovieRepository
import com.example.movieapplication.databinding.FragmentHomeBinding
import com.example.movieapplication.util.ThemePreferences
import com.example.movieapplication.util.loadImage
import com.google.android.material.chip.Chip
import kotlinx.coroutines.launch

/**
 * აპლიკაციის მთავარი ეკრანი (Home)
 * აჩვენებს Featured ფილმს, ახალ და პოპულარულ ფილმებს
 * მხარს უჭერს ძებნას (SearchView)
 * აქვს თემის გადართვის Switch
 * ჟანრების ჩიპები ჰორიზონტალურად (ფილტრაციით)
 * Infinite scroll პოპულარულ ფილმებზე
 */
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HomeViewModel by viewModels {
        HomeViewModel.Factory(MovieRepository(RetrofitInstance.api, AppDatabase.getDatabase(requireContext())))
    }

    private lateinit var newMoviesAdapter: MovieAdapter
    private lateinit var popularAdapter: MovieAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerViews()
        setupSearch()
        observeViewModel()
        setupThemeSwitch()
        loadAndShowGenres()  // ჟანრების ჩატვირთვა და Chip-ების დამატება
    }

    /**
     * ჟანრების წინასწარ ჩატვირთვა და Chip-ების დინამიურად დამატება
     * იყენებს loadGenres() რეპოზიტორიდან
     * "All" ჩიპი ნაგულისხმევად ჩართულია
     * დაჭერისას ფილტრავს popular სიას (discover API-ით)
     */
    private fun loadAndShowGenres() {
        lifecycleScope.launch {
            // ჟანრების ჩატვირთვა
            viewModel.repository.loadGenres()

            // პოპულარული ჟანრების ID-ები + fallback სახელები
            val popularGenres = listOf(
                28 to "Action",
                12 to "Adventure",
                16 to "Animation",
                35 to "Comedy",
                80 to "Crime",
                18 to "Drama",
                10751 to "Family",
                878 to "Science Fiction",
                53 to "Thriller",
                14 to "Fantasy",
                27 to "Horror",
                9648 to "Mystery"
            )

            // წაშლა ძველი ჩიპების
            binding.chipGroupGenres.removeAllViews()

            // "ყველა" ჩიპი (ნაგულისხმევად ჩართული)
            val allChip = Chip(requireContext()).apply {
                text = "All"
                isCheckable = true
                isChecked = true
                setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) {
                        viewModel.fetchPopularMovies()
                        // სხვა ჩიპების გამორთვა
                        for (i in 1 until binding.chipGroupGenres.childCount) {
                            val chip = binding.chipGroupGenres.getChildAt(i) as? Chip
                            chip?.isChecked = false
                        }
                    }
                }
            }
            binding.chipGroupGenres.addView(allChip)

            // დანარჩენი ჟანრები
            popularGenres.forEach { (id, defaultName) ->
                val name = viewModel.repository.genreMap[id] ?: defaultName
                val chip = Chip(requireContext()).apply {
                    text = name
                    isCheckable = true
                    isCheckedIconVisible = false
                    setOnCheckedChangeListener { _, isChecked ->
                        if (isChecked) {
                            viewModel.fetchMoviesByGenre(id)
                            (binding.chipGroupGenres.getChildAt(0) as? Chip)?.isChecked = false
                        } else if (binding.chipGroupGenres.checkedChipId == -1) {
                            // თუ ყველა გამორთულია - "All"
                            (binding.chipGroupGenres.getChildAt(0) as? Chip)?.isChecked = true
                        }
                    }
                }
                binding.chipGroupGenres.addView(chip)
            }
        }
    }

    /**
     * თემის გადართვის Switch-ის ინიციალიზაცია
     * მიმდინარე სტატუსი SharedPreferences-დან
     * დაჭერისას ინახავს და ცვლის თემას (ავტომატურად გადაიტვირთება UI)
     */
    private fun setupThemeSwitch() {
        binding.switchTheme.isChecked = ThemePreferences.isDarkMode(requireContext())

        binding.switchTheme.setOnCheckedChangeListener { _, isChecked ->
            ThemePreferences.setDarkMode(requireContext(), isChecked)
            AppCompatDelegate.setDefaultNightMode(
                if (isChecked) AppCompatDelegate.MODE_NIGHT_YES
                else AppCompatDelegate.MODE_NIGHT_NO
            )
        }
    }

    /**
     * RecyclerView-ების ინიციალიზაცია
     * newMovies: ჰორიზონტალური სია (ახალი ფილმები)
     * popularMovies: გრიდი (2 სვეტი) პოპულარული ფილმებისთვის
     * Infinite scroll popular-ზე (NestedScrollView-ის სქროლის დაკვირვება)
     */
    private fun setupRecyclerViews() {
        val onMovieClick: (com.example.movieapplication.data.model.Movie) -> Unit = { movie ->
            val action = HomeFragmentDirections.actionHomeFragmentToMovieDetailsFragment(movie)
            findNavController().navigate(action)
        }

        newMoviesAdapter = MovieAdapter(emptyList(), onMovieClick, { viewModel.toggleFavorite(it) })
        popularAdapter = MovieAdapter(emptyList(), onMovieClick, { viewModel.toggleFavorite(it) })

        binding.rvNewMovies.adapter = newMoviesAdapter

        binding.rvPopularMovies.apply {
            adapter = popularAdapter
            isNestedScrollingEnabled = false
        }

        binding.root.setOnScrollChangeListener { v: androidx.core.widget.NestedScrollView, _, scrollY, _, _ ->
            if (scrollY == v.getChildAt(0).measuredHeight - v.measuredHeight) {
                viewModel.fetchNextPopularPage()
            }
        }
    }

    /**
     * ViewModel-ის LiveData-ების დაკვირვება
     * nowPlaying → Featured + ახალი ფილმები
     * popularMovies → პოპულარული სია
     * favorites → ფავორიტის იკონების განახლება ყველა სიაში
     */
    private fun observeViewModel() {
        viewModel.nowPlaying.observe(viewLifecycleOwner) { movies ->
            newMoviesAdapter.updateMovies(movies)
            movies.firstOrNull()?.let {
                binding.tvFeaturedTitle.text = it.title
                binding.ivFeatured.loadImage(it.backdropPath)
            }
        }

        viewModel.popularMovies.observe(viewLifecycleOwner) { movies ->
            popularAdapter.updateMovies(movies)
        }

        viewModel.favorites.observe(viewLifecycleOwner) { favs ->
            val favIds = favs.map { it.id }.toSet()
            newMoviesAdapter.updateFavoriteStatus(favIds)
            popularAdapter.updateFavoriteStatus(favIds)
        }
    }

    /**
     * ძებნის ლოგიკა (SearchView)
     * Submit (Enter) → ძებნა
     */
    private fun setupSearch() {
        binding.searchView.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (!query.isNullOrEmpty()) {
                    viewModel.searchMovies(query)
                    binding.searchView.clearFocus()
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText.isNullOrEmpty()) {
                    viewModel.fetchMovies()
                } else if (newText.length > 2) {
                    viewModel.searchMovies(newText)
                }
                return true
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null  // Memory leak-ის თავიდან ასაცილებლად
    }
}
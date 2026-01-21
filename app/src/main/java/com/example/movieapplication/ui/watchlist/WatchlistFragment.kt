package com.example.movieapplication.ui.watchlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.movieapplication.adapter.MovieAdapter
import com.example.movieapplication.data.local.AppDatabase
import com.example.movieapplication.data.remote.RetrofitInstance
import com.example.movieapplication.data.repository.MovieRepository
import com.example.movieapplication.databinding.FragmentWatchlistBinding

/**
 * საყვარელი ფილმების ეკრანი (Watchlist)
 * აჩვენებს მომხმარებლის მიერ დამატებულ ფავორიტ ფილმებს Room-დან
 * იყენებს GridLayoutManager-ს (2 სვეტი)
 * თუ სია ცარიელია — ჩანს "ცარიელია" შეტყობინება
 * ფილმზე დაკლიკება → Details ეკრანი
 * ფავორიტის ღილაკი → წაშლა Watchlist-იდან
 */
class WatchlistFragment : Fragment() {

    private var _binding: FragmentWatchlistBinding? = null
    private val binding get() = _binding!!

    // ViewModel-ის ინიციალიზაცია Factory-ით (რეპოზიტორის გადაცემა)
    private val viewModel: WatchlistViewModel by viewModels {
        WatchlistViewModel.Factory(
            MovieRepository(
                RetrofitInstance.api,
                AppDatabase.getDatabase(requireContext())
            )
        )
    }

    private lateinit var adapter: MovieAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWatchlistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        observeViewModel()
    }

    /**
     * RecyclerView-ის და ადაპტერის ინიციალიზაცია
     * GridLayoutManager 2 სვეტით
     * onItemClick → Details-ზე გადასვლა
     * onFavoriteClick → ViewModel-ში გადართვა (წაშლა)
     */
    private fun setupRecyclerView() {
        adapter = MovieAdapter(
            emptyList(),
            onItemClick = { movie ->
                val action = WatchlistFragmentDirections.actionWatchlistFragmentToMovieDetailsFragment(movie)
                findNavController().navigate(action)
            },
            onFavoriteClick = { movie ->
                viewModel.toggleFavorite(movie)
            }
        )

        binding.rvFavorites.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rvFavorites.adapter = adapter
    }

    /**
     * ViewModel-ის LiveData-ების დაკვირვება
     * watchlist → ფილმების სია (ცარიელი თუ არა — შეტყობინების ჩვენება)
     * favorites → ფავორიტის სტატუსის განახლება
     */
    private fun observeViewModel() {
        viewModel.watchlist.observe(viewLifecycleOwner) { movies ->
            if (movies.isEmpty()) {
                // სია ცარიელია - ჩანს "ცარიელია" შეტყობინება
                binding.tvEmptyMsg.visibility = View.VISIBLE
                binding.rvFavorites.visibility = View.GONE
            } else {
                // სია არ არის ცარიელი - ჩანს ფილმები
                binding.tvEmptyMsg.visibility = View.GONE
                binding.rvFavorites.visibility = View.VISIBLE

                // 1. სიის განახლება
                adapter.updateMovies(movies)

                // 2. ფავორიტის იკონების განახლება
                val favIds = movies.map { it.id }.toSet()
                adapter.updateFavoriteStatus(favIds)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
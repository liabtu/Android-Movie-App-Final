package com.example.movieapplication.ui.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.movieapplication.adapter.MovieAdapter
import com.example.movieapplication.data.remote.RetrofitInstance
import com.example.movieapplication.data.repository.MovieRepository
import com.example.movieapplication.data.local.AppDatabase
import com.example.movieapplication.databinding.FragmentSearchBinding

/**
 * ფილმების ძებნის ეკრანი (Search)
 * - იყენებს SearchView-ს (EditText) ფილმების საძიებლად
 * - აჩვენებს შედეგებს RecyclerView-ში
 * - მხარს უჭერს ფავორიტად დამატებას/წაშლას
 * - გადადის Details ეკრანზე ფილმზე დაკლიკებით
 */
class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    // ViewModel-ის ინიციალიზაცია Factory-ით (რეპოზიტორის გადაცემა)
    private val viewModel: SearchViewModel by viewModels {
        SearchViewModel.Factory(
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
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // RecyclerView-ის და ადაპტერის ინიციალიზაცია
        adapter = MovieAdapter(emptyList(), { movie ->
            // ფილმზე დაკლიკება → Details ეკრანზე გადასვლა
            val action = SearchFragmentDirections.actionSearchFragmentToMovieDetailsFragment(movie)
            findNavController().navigate(action)
        }, { movie ->
            // ფავორიტის ღილაკზე დაჭერა → ViewModel-ში გადართვა
            viewModel.toggleFavorite(movie)
        })

        binding.rvSearchResults.layoutManager = LinearLayoutManager(requireContext())
        binding.rvSearchResults.adapter = adapter

        // საძიებო ველის Enter/Search ღილაკზე დაჭერის ლოგიკა
        binding.etSearch.setOnEditorActionListener { _, _, _ ->
            val query = binding.etSearch.text.toString()
            if (query.isNotBlank()) {
                viewModel.searchMovies(query)
            }
            true
        }

        // ძებნის შედეგების დაკვირვება
        viewModel.searchResults.observe(viewLifecycleOwner) { movies ->
            adapter.updateMovies(movies)
        }

        viewModel.loading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        // საყვარელი ფილმების სტატუსის განახლება (ფავორიტის იკონის ფერი)
        viewModel.favorites.observe(viewLifecycleOwner) { favs ->
            val favIds = favs.map { it.id }.toSet()
            adapter.updateFavoriteStatus(favIds)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
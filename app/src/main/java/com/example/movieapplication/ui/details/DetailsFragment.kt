package com.example.movieapplication.ui.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.example.movieapplication.R
import com.example.movieapplication.data.remote.RetrofitInstance
import com.example.movieapplication.data.repository.MovieRepository
import androidx.navigation.fragment.findNavController
import com.example.movieapplication.data.local.AppDatabase
import com.example.movieapplication.databinding.FragmentDetailsBinding
import com.example.movieapplication.util.loadImage
import kotlinx.coroutines.launch

/**
 * ფილმის დეტალური ეკრანი (Details)
 * - აჩვენებს ფილმის სრულ ინფორმაციას (სათაური, აღწერა, რეიტინგი, გამოსვლის თარიღი, ჟანრები, ფონი)
 * - საშუალებას აძლევს ფავორიტად დამატებას/წაშლას
 * - იღებს Movie ობიექტს არგუმენტად Navigation Safe Args-ით
 */
class DetailsFragment : Fragment() {

    private var _binding: FragmentDetailsBinding? = null
    private val binding get() = _binding!!

    // ViewModel-ის ინიციალიზაცია Factory-ით (რეპოზიტორის გადაცემა)
    private val viewModel: DetailsViewModel by viewModels {
        DetailsViewModel.Factory(
            MovieRepository(
                RetrofitInstance.api,
                AppDatabase.getDatabase(requireContext())
            )
        )
    }

    // Navigation Safe Args-ით მიღებული Movie ობიექტი
    private val args: DetailsFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val movie = args.movie

        // ფილმის მონაცემების შევსება UI-ში
        binding.apply {
            ivBackdrop.loadImage(movie.backdropPath)          // ფონის სურათი
            tvDetailTitle.text = movie.title                  // სათაური
            tvDetailRating.text = "⭐ ${String.format("%.1f", movie.voteAverage)}" // რეიტინგი
            tvReleaseDate.text = movie.releaseDate            // გამოსვლის თარიღი
            tvOverview.text = movie.overview                  // აღწერა

            // ჟანრების სახელების ჩვენება (ქეშიდან)
            tvGenres.text = viewModel.repository.getGenreNames(movie.genreIds)
        }

        // ჟანრების წინასწარ ჩატვირთვა
        lifecycleScope.launch {
            viewModel.repository.loadGenres()
            binding.tvGenres.text = viewModel.repository.getGenreNames(movie.genreIds)
        }

        // 1. ფავორიტის სტატუსის შემოწმება ბაზაში
        viewModel.checkFavoriteStatus(movie.id)

        // 2. Watchlist ღილაკზე დაჭერის ლოგიკა (დამატება/წაშლა)
        binding.btnWatchlist.setOnClickListener {
            viewModel.toggleFavorite(movie)
        }

        // 3. ფავორიტის სტატუსის ცვლილების დაკვირვება (LiveData-ით)
        viewModel.isFavorite.observe(viewLifecycleOwner) { isFav ->
            val icon = if (isFav) R.drawable.ic_watchlist_filled else R.drawable.ic_watchlist
            binding.btnWatchlist.setImageResource(icon)
        }

        // უკან დაბრუნების ღილაკი (Navigation-ით)
        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null  // Memory leak-ის თავიდან ასაცილებლად
    }
}
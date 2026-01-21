package com.example.movieapplication.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.movieapplication.R
import com.example.movieapplication.data.model.Movie
import com.example.movieapplication.databinding.ItemMovieBinding
import com.example.movieapplication.util.Constants

/**
 * RecyclerView-ის ადაპტერი ფილმების სიის საჩვენებლად (item_movie.xml-ისთვის).
 * მხარს უჭერს:
 *  - ფილმის დაკლიკებას (Details-ზე გადასვლა)
 *  - ფავორიტად მონიშვნას/მოხსნას (ფერის ცვლილება + callback)
 */
class MovieAdapter(
    private var movies: List<Movie> = emptyList(),
    private val onItemClick: (Movie) -> Unit,
    private val onFavoriteClick: (Movie) -> Unit
) : RecyclerView.Adapter<MovieAdapter.MovieViewHolder>() {

    /**
     * ViewHolder — ერთი item_movie-ის წარმომადგენელი
     * აკეთებს binding-ს და ფილმის მონაცემების შევსებას
     */
    inner class MovieViewHolder(private val binding: ItemMovieBinding) :
        RecyclerView.ViewHolder(binding.root) {

        /**
         * ავსებს ერთი ფილმის მონაცემებს UI-ში
         */
        fun bind(movie: Movie) {
            binding.tvMovieTitle.text = movie.title

            binding.tvMovieRating.text = "⭐ ${String.format("%.1f", movie.voteAverage ?: 0.0)}"

            val posterUrl = "${Constants.IMAGE_BASE_URL}${movie.posterPath}"
            Glide.with(binding.ivMoviePoster.context)
                .load(posterUrl)
                .into(binding.ivMoviePoster)

            val favColor =
                if (movie.isFavorite) Color.parseColor("#6C47FF") else Color.parseColor("#CCCCCC")
            binding.ivFavIcon.setColorFilter(favColor)

            binding.ivFavIcon.setOnClickListener {
                onFavoriteClick(movie)
            }

            // Watch Trailer ღილაკი + მთლიანი item-ის დაკლიკება → Details-ზე გადასვლა
            binding.btnWatchTrailer.setOnClickListener { onItemClick(movie) }
            binding.root.setOnClickListener { onItemClick(movie) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val binding = ItemMovieBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MovieViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        holder.bind(movies[position])
    }

    override fun getItemCount(): Int = movies.size

    /** განაახლებს ფილმების სიას DiffUtil-ით (ეფექტურად, მხოლოდ შეცვლილი item-ები იცვლება) */
    fun updateMovies(newMovieList: List<Movie>) {
        val diffCallback = MovieDiffCallback(this.movies, newMovieList)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        this.movies = newMovieList
        diffResult.dispatchUpdatesTo(this)
    }

    /** განაახლებს ფავორიტის სტატუსს ყველა ფილმში (ბაზიდან მიღებული ID-ებით) */
    fun updateFavoriteStatus(favIds: Set<Int>) {
        val updatedList = movies.map { it.copy(isFavorite = it.id in favIds) }
        updateMovies(updatedList)
    }

    /**  DiffUtil-ის Callback — ეხმარება RecyclerView-ს გაიგოს, რომელი item-ები შეიცვალა */
    class MovieDiffCallback(
        private val oldList: List<Movie>,
        private val newList: List<Movie>
    ) : DiffUtil.Callback() {
        override fun getOldListSize() = oldList.size
        override fun getNewListSize() = newList.size

        override fun areItemsTheSame(oldPos: Int, newPos: Int) =
            oldList[oldPos].id == newList[newPos].id

        override fun areContentsTheSame(oldPos: Int, newPos: Int) =
            oldList[oldPos] == newList[newPos]
    }
}
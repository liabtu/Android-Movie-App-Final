package com.example.movieapplication.util

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.example.movieapplication.R

/**
 * ImageView-ის გაფართოება — სურათის ჩატვირთვის მარტივი ფუნქცია Glide-ით.
 * იყენებს placeholder-ს და error-ს ცარიელი/შეცდომის შემთხვევაში.
 */
fun ImageView.loadImage(path: String?) {
    if (path.isNullOrEmpty()) {
        this.setImageResource(R.drawable.bg_gradient_shadow)
        return
    }

    Glide.with(this.context)
        .load(Constants.IMAGE_BASE_URL + path)
        .placeholder(R.drawable.bg_gradient_shadow) // ჩატვირთვის დროს
        .error(R.drawable.bg_gradient_shadow)       // შეცდომის დროს
        .into(this)
}
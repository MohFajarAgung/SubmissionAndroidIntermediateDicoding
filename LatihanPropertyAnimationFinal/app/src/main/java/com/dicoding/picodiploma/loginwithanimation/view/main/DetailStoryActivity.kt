package com.dicoding.picodiploma.loginwithanimation.view.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners

import com.dicoding.picodiploma.loginwithanimation.databinding.ActivityDetailStoryBinding

class DetailStoryActivity : AppCompatActivity() {
    private lateinit var binding : ActivityDetailStoryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        setupData()

    }
    private fun setupData(){
        val storyName = intent.getStringExtra("name")
        val imageStory = intent.getStringExtra("image")
        val descStory = intent.getStringExtra("description")

        Glide.with(applicationContext)
            .load(imageStory)
            .transform(RoundedCorners(16))
            .into(binding.imageView)

        binding.nameTextView.text = storyName
        binding.descTextView.text = descStory
    }

}
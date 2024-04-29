package com.putragandad.newsmvvmpractice.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.putragandad.newsmvvmpractice.R
import com.putragandad.newsmvvmpractice.databinding.ActivityNewsBinding

class NewsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNewsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val bottomNavController = this.findNavController(R.id.newsNavHostFragment)

        binding.bottomNavigation.setupWithNavController(bottomNavController)
    }
}
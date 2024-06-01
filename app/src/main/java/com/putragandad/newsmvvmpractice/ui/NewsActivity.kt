package com.putragandad.newsmvvmpractice.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.putragandad.newsmvvmpractice.R
import com.putragandad.newsmvvmpractice.databinding.ActivityNewsBinding
import com.putragandad.newsmvvmpractice.db.ArticleDatabase
import com.putragandad.newsmvvmpractice.repositories.NewsRepository
import com.putragandad.newsmvvmpractice.ui.viewmodels.NewsViewModel
import com.putragandad.newsmvvmpractice.ui.viewmodels.NewsViewModelFactory

class NewsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNewsBinding

    lateinit var viewModel: NewsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val bottomNavController = this.findNavController(R.id.newsNavHostFragment)
        binding.bottomNavigation.setupWithNavController(bottomNavController)

        val newsRepository = NewsRepository(ArticleDatabase(this))
        val viewModelFactory = NewsViewModelFactory(newsRepository)
        viewModel = ViewModelProvider(this, viewModelFactory).get(NewsViewModel::class.java)
    }
}
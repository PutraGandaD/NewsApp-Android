package com.putragandad.newsmvvmpractice.ui.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.putragandad.newsmvvmpractice.R
import com.putragandad.newsmvvmpractice.adapters.NewsAdapter
import com.putragandad.newsmvvmpractice.databinding.FragmentBreakingNewsBinding
import com.putragandad.newsmvvmpractice.databinding.FragmentSearchNewsBinding
import com.putragandad.newsmvvmpractice.db.ArticleDatabase
import com.putragandad.newsmvvmpractice.repositories.NewsRepository
import com.putragandad.newsmvvmpractice.ui.NewsActivity
import com.putragandad.newsmvvmpractice.ui.viewmodels.NewsViewModel
import com.putragandad.newsmvvmpractice.ui.viewmodels.NewsViewModelFactory
import com.putragandad.newsmvvmpractice.util.Constants
import com.putragandad.newsmvvmpractice.util.Resource
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SearchNewsFragment : Fragment() {
    private lateinit var viewModel: NewsViewModel
    private lateinit var newsAdapter: NewsAdapter

    private var _binding: FragmentSearchNewsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSearchNewsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val newsRepository = NewsRepository(ArticleDatabase(requireActivity()))
        val viewModelFactory = NewsViewModelFactory(requireActivity().application, newsRepository)
        viewModel = ViewModelProvider(requireActivity(), viewModelFactory).get(NewsViewModel::class.java)

        setUpRecyclerView()

        newsAdapter.setOnItemClickListener {
            val bundle = Bundle().apply {
                putSerializable(Constants.ARTICLE_BUNDLE, it)
            }
            findNavController().navigate(
                R.id.action_searchNewsFragment_to_articleFragment,
                bundle
            )
        }

        var job: Job? = null
        binding.etSearch.addTextChangedListener{ editable ->
            job?.cancel() // cancel current job when new typing activities detected
            job = MainScope().launch {
                delay(Constants.SEARCH_NEWS_TIME_DELAY) // add delay
                editable?.let {
                    if(editable.toString().isNotEmpty()) {
                        viewModel.searchNews(editable.toString())
                    } else {
                        newsAdapter.differ.submitList(emptyList())
                    }
                }
            }
        }

        viewModel.searchNews.observe(viewLifecycleOwner, Observer { response ->
            when(response) {
                is Resource.Success -> {
                    hideProgressBar()
                    response.data?.let { newsResponse ->
                        newsAdapter.differ.submitList(newsResponse.articles)
                    }
                }
                is Resource.Error -> {
                    hideProgressBar()
                    response.message?.let { message ->
                        Log.d(Constants.SEARCH_NEWS_LOGS_TAG, "An error occured : $message")
                        Snackbar.make(view, "No Internet Connection", Snackbar.LENGTH_LONG).show()
                    }
                }
                is Resource.Loading -> {
                    showProgressBar()
                }
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun hideProgressBar() {
        binding.paginationProgressBar.visibility = View.GONE
    }

    private fun showProgressBar() {
        binding.paginationProgressBar.visibility = View.VISIBLE
    }

    private fun setUpRecyclerView() {
        newsAdapter = NewsAdapter()
        val recyclerView = binding.rvSearchNews
        recyclerView.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(activity)
        }
    }
}
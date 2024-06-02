package com.putragandad.newsmvvmpractice.ui.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.putragandad.newsmvvmpractice.NewsApplication
import com.putragandad.newsmvvmpractice.R
import com.putragandad.newsmvvmpractice.adapters.NewsAdapter
import com.putragandad.newsmvvmpractice.databinding.FragmentBreakingNewsBinding
import com.putragandad.newsmvvmpractice.db.ArticleDatabase
import com.putragandad.newsmvvmpractice.repositories.NewsRepository
import com.putragandad.newsmvvmpractice.ui.NewsActivity
import com.putragandad.newsmvvmpractice.ui.viewmodels.NewsViewModel
import com.putragandad.newsmvvmpractice.ui.viewmodels.NewsViewModelFactory
import com.putragandad.newsmvvmpractice.util.Constants
import com.putragandad.newsmvvmpractice.util.Resource

class BreakingNewsFragment : Fragment() {
    private lateinit var viewModel: NewsViewModel
    private lateinit var newsAdapter: NewsAdapter

    private var _binding: FragmentBreakingNewsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentBreakingNewsBinding.inflate(inflater, container, false)
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
                R.id.action_breakingNewsFragment_to_articleFragment,
                bundle
            )
            //Toast.makeText(requireActivity(), "Clicked!", Toast.LENGTH_SHORT).show()
        }

        viewModel.breakingNews.observe(viewLifecycleOwner, Observer { response ->
            when(response) {
                is Resource.Success -> {
                    hideProgressBar()
                    response.data?.let { newsResponse ->
                        hideOfflineStatus()
                        newsAdapter.differ.submitList(newsResponse.articles.toList())
                        val totalPages = newsResponse.totalResults / Constants.QUERY_PAGE_SIZE + 2
                        isLastPage = viewModel.breakingNewsPage == totalPages
                    }
                }
                is Resource.Error -> {
                    hideProgressBar()
                    response.message?.let { message ->
                        Log.d(Constants.BREAKING_NEWS_LOGS_TAG, "An error occured : $message")
                        Snackbar.make(view, "No Internet Connection", Snackbar.LENGTH_LONG).show()
                        showOfflineStatus()
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
        isLoading = false
    }

    private fun showProgressBar() {
        binding.paginationProgressBar.visibility = View.VISIBLE
        isLoading = true
    }

    private fun hideOfflineStatus() {
        binding.tvOffline.visibility = View.GONE
        binding.btnOffline.visibility = View.GONE
    }

    private fun showOfflineStatus() {
        binding.tvOffline.visibility = View.VISIBLE
        binding.btnOffline.visibility = View.VISIBLE
        binding.btnOffline.setOnClickListener {
            viewModel.getBreakingNews("us")
        }
    }

    var isLoading = false
    var isLastPage = false
    var isScrolling = false

    val scrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)

            if(newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                isScrolling = true
            }
        }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            val layoutManager = recyclerView.layoutManager as LinearLayoutManager
            val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
            val visibleItemCount = layoutManager.childCount
            val totalItemCount = layoutManager.itemCount

            val isNotLoadingAndNotLastPage = !isLoading && !isLastPage
            val isAtLastItem = firstVisibleItemPosition + visibleItemCount >= totalItemCount
            val isNotAtBeginning = firstVisibleItemPosition >= 0
            val isTotalMoreThanVisible = totalItemCount >= Constants.QUERY_PAGE_SIZE
            val shouldPaginate = isNotLoadingAndNotLastPage && isAtLastItem && isNotAtBeginning &&
                    isTotalMoreThanVisible && isScrolling
            if(shouldPaginate) {
                viewModel.getBreakingNews("us")
                isScrolling = false
            } else {
                binding.rvBreakingNews.setPadding(0,0,0,0)
            }
        }
    }

    private fun setUpRecyclerView() {
        newsAdapter = NewsAdapter()
        val recyclerView = binding.rvBreakingNews
        recyclerView.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(activity)
            addOnScrollListener(this@BreakingNewsFragment.scrollListener)
        }
    }
}
package com.putragandad.newsmvvmpractice.ui.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.putragandad.newsmvvmpractice.R
import com.putragandad.newsmvvmpractice.adapters.NewsAdapter
import com.putragandad.newsmvvmpractice.databinding.FragmentBreakingNewsBinding
import com.putragandad.newsmvvmpractice.databinding.FragmentSavedNewsBinding
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

class SavedNewsFragment : Fragment() {
    private lateinit var viewModel: NewsViewModel
    private lateinit var newsAdapter: NewsAdapter

    private var _binding: FragmentSavedNewsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSavedNewsBinding.inflate(inflater, container, false)
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
                R.id.action_savedNewsFragment_to_articleFragment,
                bundle
            )
            //Toast.makeText(requireActivity(), "Clicked!", Toast.LENGTH_SHORT).show()
        }

        // add swipe to delete
        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val article = newsAdapter.differ.currentList[position]
                viewModel.deleteArticle(article)

                Snackbar.make(view, "Successfully deleted article", Snackbar.LENGTH_LONG).apply {
                    setAction("Undo") {
                        viewModel.saveArticle(article)
                    }
                    show()
                }
            }
        }

        ItemTouchHelper(itemTouchHelperCallback).apply {
            attachToRecyclerView(binding.rvSavedNews)
        }

        viewModel.getSavedNews().observe(viewLifecycleOwner, Observer { articles ->
            // Update the RecyclerView only if there is no search query
            if (binding.etSearchSavedNews.text.trim().isNullOrEmpty()) {
                newsAdapter.differ.submitList(articles)
            }
        })

        var job: Job? = null
        binding.etSearchSavedNews.addTextChangedListener { editable ->
            job?.cancel() // Cancel current job when new typing activities detected
            job = MainScope().launch {
                delay(Constants.SEARCH_NEWS_TIME_DELAY) // Add delay
                editable?.let {
                    val query = editable.toString().trim()
                    viewModel.searchSavedNews(query)
                    viewModel.searchedSavedNews.observe(viewLifecycleOwner, Observer { articles ->
                        newsAdapter.differ.submitList(articles)
                    })
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setUpRecyclerView() {
        newsAdapter = NewsAdapter()
        val recyclerView = binding.rvSavedNews
        recyclerView.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(activity)
        }
    }

}
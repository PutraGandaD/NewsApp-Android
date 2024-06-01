package com.putragandad.newsmvvmpractice.repositories

import com.putragandad.newsmvvmpractice.api.RetrofitInstance
import com.putragandad.newsmvvmpractice.db.ArticleDatabase
import com.putragandad.newsmvvmpractice.models.Article

class NewsRepository(
    val db: ArticleDatabase
) {
    suspend fun getBreakingNews(countryCode: String, pageNumber: Int) =
        RetrofitInstance.api.getBreakingNews(countryCode, pageNumber)

    suspend fun searchNews(searchQuery: String, pageNumber: Int) =
        RetrofitInstance.api.searchForNews(searchQuery, pageNumber)

    suspend fun upsert(article: Article) = db.getArticleDao().upsert(article)

    fun getSavedNews() = db.getArticleDao().getAllArticles()

    suspend fun searchSavedNews(query: String) = db.getArticleDao().searchSavedNews(query)

    suspend fun deleteArticle(article: Article) = db.getArticleDao().deleteArticle(article)
}
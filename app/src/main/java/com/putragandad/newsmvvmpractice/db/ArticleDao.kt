package com.putragandad.newsmvvmpractice.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.putragandad.newsmvvmpractice.models.Article

@Dao
interface ArticleDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    // Update or insert the article
    suspend fun upsert(article: Article) : Long

    @Query("SELECT * FROM articles ORDER BY id DESC")
    // Get the most updated Article with LiveData
    fun getAllArticles() : LiveData<List<Article>>

    @Delete
    suspend fun deleteArticle(article: Article)

    @Query("SELECT * FROM articles WHERE title LIKE '%' || :query || '%'")
    suspend fun searchSavedNews(query: String): List<Article>
}
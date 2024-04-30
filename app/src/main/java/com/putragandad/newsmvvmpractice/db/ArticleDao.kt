package com.putragandad.newsmvvmpractice.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.putragandad.newsmvvmpractice.Article

@Dao
interface ArticleDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    // Update or insert the article
    suspend fun upsert(article: Article) : Long

    @Query("SELECT * FROM articles")
    // Get the most updated Article with LiveData
    fun getAllArticles() : LiveData<List<Article>>

    @Delete
    suspend fun deleteArticle(article: Article)
}
package com.putragandad.newsmvvmpractice.api

import com.putragandad.newsmvvmpractice.NewsResponse
import com.putragandad.newsmvvmpractice.util.Constants
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

// define a request to the API
interface NewsAPI {
    @GET("v2/top-headlines")
    suspend fun getBreakingNews(
        @Query("country")
        countryCode: String = "us",
        @Query("page")
        pageNumber: Int = 1,
        @Query("apiKey")
        apiKey: String = Constants.API_KEY
    ) : Response<NewsResponse>

    @GET("v2/everything")
    suspend fun searchForNews(
        @Query("q")
        searchQuery: String,
        @Query("page")
        pageNumber: Int = 1,
        @Query("apiKey")
        apiKey: String = Constants.API_KEY
    ) : Response<NewsResponse>
}
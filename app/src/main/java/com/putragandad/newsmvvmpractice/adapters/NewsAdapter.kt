package com.putragandad.newsmvvmpractice.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.putragandad.newsmvvmpractice.R
import com.putragandad.newsmvvmpractice.models.Article

class NewsAdapter: RecyclerView.Adapter<NewsAdapter.ArticleViewHolder>() {
    inner class ArticleViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val ivArticle: ImageView = itemView.findViewById(R.id.iv_article_items)
        val tvTitleArticle: TextView = itemView.findViewById(R.id.tv_article_title)
        val tvAuthorArticle: TextView = itemView.findViewById(R.id.tv_article_author)
        val tvDatesArticle: TextView = itemView.findViewById(R.id.tv_article_dates)
        val tvPreviewArticle: TextView = itemView.findViewById(R.id.tv_article_content_preview)
    }

    private val differCallback = object : DiffUtil.ItemCallback<Article>() {
        override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem.url == newItem.url
        }

        override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        return ArticleViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_article_preview,
                parent,
                false
            )
        )
    }

    private var onItemClickListener: ((Article) -> Unit)? = null

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        val article = differ.currentList[position] // get current article from differ

        holder.itemView.apply {
            Glide.with(this).load(article.urlToImage).into(holder.ivArticle) // load image
            holder.tvAuthorArticle.text = article.source.name
            holder.tvTitleArticle.text = article.title
            holder.tvPreviewArticle.text = article.description
            holder.tvDatesArticle.text = article.publishedAt

            setOnClickListener {
                onItemClickListener?.let {
                    Log.d("ARTICLE_CLICKED_LOG", article.toString())
                    it(article)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    fun setOnItemClickListener(listener: (Article) -> Unit) {
        onItemClickListener = listener
    }
}
package com.hayakai.ui.map

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil3.load
import coil3.request.fallback
import coil3.request.placeholder
import com.hayakai.R
import com.hayakai.data.local.entity.News
import com.hayakai.databinding.ItemNewsBinding

class NewsListAdapter : ListAdapter<News, NewsListAdapter.ViewHolder>(DIFF_CALLBACK) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemNewsBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }


    inner class ViewHolder(
        val binding: ItemNewsBinding
    ) : RecyclerView.ViewHolder(
        binding.root
    ) {

        fun bind(news: News) {
            binding.apply {
                image.load(news.image) {
                    placeholder(R.drawable.placeholder_image)
                    fallback(R.drawable.placeholder_image)
                }
                title.text = news.title
                displayUrl.text = news.displayLink
                itemView.setOnClickListener {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(news.link)).apply {
                        addCategory(Intent.CATEGORY_BROWSABLE)
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    }
                    itemView.context.startActivity(intent)
                }
            }
        }

    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<News>() {
            override fun areItemsTheSame(oldItem: News, newItem: News): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: News,
                newItem: News
            ): Boolean {
                return oldItem.title == newItem.title
            }
        }
    }
}
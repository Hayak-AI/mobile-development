package com.hayakai.ui.map

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil3.load
import com.hayakai.data.local.entity.News
import com.hayakai.databinding.ItemNewsBinding
import com.hayakai.ui.detailpost.DetailPostActivity

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
                image.load(news.image)
                title.text = news.title
                itemView.setOnClickListener {
                    val intent = Intent(itemView.context, DetailPostActivity::class.java)
                    intent.putExtra(DetailPostActivity.EXTRA_POST, news)
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
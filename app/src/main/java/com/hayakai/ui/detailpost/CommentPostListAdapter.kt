package com.hayakai.ui.detailpost

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil3.load
import com.hayakai.R
import com.hayakai.data.local.entity.CommentPost
import com.hayakai.databinding.ItemCommentBinding

class CommentPostListAdapter(
    private val onClick: (CommentPost) -> Unit
) : ListAdapter<CommentPost, CommentPostListAdapter.ViewHolder>(DIFF_CALLBACK) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemCommentBinding.inflate(
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
        val binding: ItemCommentBinding
    ) : RecyclerView.ViewHolder(
        binding.root
    ) {

        fun bind(commentPost: CommentPost) {
            binding.apply {
                authorImage.load(commentPost.userImage ?: R.drawable.fallback_user)
                author.text = commentPost.userName
                textComment.text = commentPost.content
                btnDelete.isVisible = commentPost.byMe
                btnDelete.setOnClickListener {
                    onClick(commentPost)
                }
            }
        }

    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<CommentPost>() {
            override fun areItemsTheSame(oldItem: CommentPost, newItem: CommentPost): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: CommentPost,
                newItem: CommentPost
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}
package com.hayakai.ui.detailpost

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil3.load
import com.hayakai.R
import com.hayakai.data.local.entity.CommentPost
import com.hayakai.data.remote.response.PostDataItemComment
import com.hayakai.databinding.ItemCommentBinding

class CommentPostListAdapter(
    private val onClick: (CommentPost) -> Unit
) : PagingDataAdapter<PostDataItemComment, CommentPostListAdapter.ViewHolder>(DIFF_CALLBACK) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemCommentBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(it) }
    }


    inner class ViewHolder(
        val binding: ItemCommentBinding
    ) : RecyclerView.ViewHolder(
        binding.root
    ) {

        fun bind(postDataItemComment: PostDataItemComment) {
            val commentPost = postDataItemComment.let {
                CommentPost(
                    it.commentId,
                    it.postId,
                    it.content,
                    it.user.id,
                    it.user.name,
                    it.user.profilePhoto,
                    it.byMe,
                    it.createdAt
                )
            }
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
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<PostDataItemComment>() {
            override fun areItemsTheSame(
                oldItem: PostDataItemComment,
                newItem: PostDataItemComment
            ): Boolean {
                return oldItem.commentId == newItem.commentId
            }

            override fun areContentsTheSame(
                oldItem: PostDataItemComment,
                newItem: PostDataItemComment
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}
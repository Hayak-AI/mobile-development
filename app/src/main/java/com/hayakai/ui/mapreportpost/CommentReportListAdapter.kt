package com.hayakai.ui.mapreportpost

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil3.load
import com.hayakai.R
import com.hayakai.data.local.entity.CommentReport
import com.hayakai.data.remote.response.DataItemComment
import com.hayakai.databinding.ItemCommentBinding

class CommentReportListAdapter(
    private val onClick: (CommentReport) -> Unit
) : PagingDataAdapter<DataItemComment, CommentReportListAdapter.ViewHolder>(DIFF_CALLBACK) {
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

        fun bind(dataItemComment: DataItemComment) {
            val commentReport = dataItemComment.let {
                CommentReport(
                    it.commentId,
                    it.reportId,
                    it.content,
                    it.user.id,
                    it.user.name,
                    it.user.profilePhoto,
                    it.byMe,
                    it.createdAt
                )
            }
            binding.apply {
                authorImage.load(if (commentReport.userImage.isNullOrEmpty()) R.drawable.fallback_user else commentReport.userImage)
                author.text = commentReport.userName
                textComment.text = commentReport.content
                btnDelete.isVisible = commentReport.byMe
                btnDelete.setOnClickListener {
                    onClick(commentReport)
                }
            }
        }

    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<DataItemComment>() {
            override fun areItemsTheSame(
                oldItem: DataItemComment,
                newItem: DataItemComment
            ): Boolean {
                return oldItem.commentId == newItem.commentId
            }

            override fun areContentsTheSame(
                oldItem: DataItemComment,
                newItem: DataItemComment
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}
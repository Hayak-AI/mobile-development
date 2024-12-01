package com.hayakai.ui.mapreportpost

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil3.load
import com.hayakai.data.local.entity.CommentReport
import com.hayakai.databinding.ItemCommentBinding

class CommentReportListAdapter(
    private val onClick: (CommentReport) -> Unit
) : ListAdapter<CommentReport, CommentReportListAdapter.ViewHolder>(DIFF_CALLBACK) {
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

        fun bind(commentReport: CommentReport) {
            binding.apply {
                authorImage.load(commentReport.userImage)
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
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<CommentReport>() {
            override fun areItemsTheSame(oldItem: CommentReport, newItem: CommentReport): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: CommentReport,
                newItem: CommentReport
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}
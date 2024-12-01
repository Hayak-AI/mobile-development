package com.hayakai.ui.community

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil3.load
import com.hayakai.R
import com.hayakai.data.local.entity.CommunityPost
import com.hayakai.databinding.ItemPostBinding

class CommunityPostListAdapter(
    private val onDelete: (CommunityPost) -> Unit,
    private val onEdit: (CommunityPost) -> Unit
) : ListAdapter<CommunityPost, CommunityPostListAdapter.ViewHolder>(DIFF_CALLBACK) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemPostBinding.inflate(
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
        val binding: ItemPostBinding
    ) : RecyclerView.ViewHolder(
        binding.root
    ) {

        fun bind(communityPost: CommunityPost) {
            binding.apply {
                userImage.load(communityPost.userImage)
                userName.text = communityPost.userName
                title.text = communityPost.title
                userLocation.text = communityPost.locationName
                content.text = communityPost.content
//                btnDelete.isVisible = communityPost.byMe
                btnMenu.setOnClickListener { v: View ->
                    val popup = PopupMenu(v.context, v)
                    popup.menuInflater.inflate(R.menu.popup_menu_post, popup.menu)
                    popup.setOnMenuItemClickListener { item ->
                        when (item.itemId) {
                            R.id.delete -> {
                                onDelete(communityPost)
                                true
                            }

                            R.id.edit -> {
                                onEdit(communityPost)
                                true
                            }

                            else -> false
                        }
                    }
                    popup.show()
                }
            }
        }

    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<CommunityPost>() {
            override fun areItemsTheSame(oldItem: CommunityPost, newItem: CommunityPost): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: CommunityPost,
                newItem: CommunityPost
            ): Boolean {
                return oldItem.content == newItem.content
            }
        }
    }
}
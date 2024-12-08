package com.hayakai.ui.community

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil3.load
import coil3.request.fallback
import coil3.request.placeholder
import com.hayakai.R
import com.hayakai.data.local.entity.CommunityPost
import com.hayakai.databinding.ItemPostBinding
import com.hayakai.ui.detailpost.DetailPostActivity

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
                userImage.load(communityPost.userImage) {
                    placeholder(R.drawable.fallback_user)
                    fallback(R.drawable.fallback_user)
                }
                userName.text = if (communityPost.byMe) itemView.context.getString(
                    R.string.by_me,
                    communityPost.userName
                ) else communityPost.userName
                title.text = communityPost.title
                if (communityPost.locationName.isEmpty()) {
                    userLocation.visibility = View.GONE
                    pinLocation.visibility = View.GONE
                } else {
                    userLocation.visibility = View.VISIBLE
                    pinLocation.visibility = View.VISIBLE
                    userLocation.text = communityPost.locationName
                }
                content.text = communityPost.content
                tvCategory.text = communityPost.category
                btnComment.text =
                    itemView.context.getString(R.string.total_comments, communityPost.totalComments)
                btnMenu.setOnClickListener { v: View ->
                    val popup = PopupMenu(v.context, v)
                    popup.menuInflater.inflate(
                        if (communityPost.byMe) R.menu.popup_menu_my_post else R.menu.popup_menu_post,
                        popup.menu
                    )
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

                            R.id.detail -> {
                                val intent =
                                    Intent(itemView.context, DetailPostActivity::class.java)
                                intent.putExtra(DetailPostActivity.EXTRA_POST, communityPost)
                                itemView.context.startActivity(intent)
                                true
                            }

                            else -> false
                        }
                    }
                    popup.show()
                }
                itemView.setOnClickListener {
                    val intent = Intent(itemView.context, DetailPostActivity::class.java)
                    intent.putExtra(DetailPostActivity.EXTRA_POST, communityPost)
                    itemView.context.startActivity(intent)
                }

                btnComment.setOnClickListener {
                    val intent = Intent(itemView.context, DetailPostActivity::class.java)
                    intent.putExtra(DetailPostActivity.EXTRA_POST, communityPost)
                    itemView.context.startActivity(intent)
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
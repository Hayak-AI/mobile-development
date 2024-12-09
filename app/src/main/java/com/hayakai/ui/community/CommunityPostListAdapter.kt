package com.hayakai.ui.community

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil3.load
import coil3.request.fallback
import coil3.request.placeholder
import com.hayakai.R
import com.hayakai.data.local.entity.CommunityPost
import com.hayakai.data.remote.response.PostItem
import com.hayakai.databinding.ItemPostBinding
import com.hayakai.ui.detailpost.DetailPostActivity

class CommunityPostListAdapter(
    private val onDelete: (CommunityPost) -> Unit,
    private val onEdit: (CommunityPost) -> Unit
) : PagingDataAdapter<PostItem, CommunityPostListAdapter.ViewHolder>(DIFF_CALLBACK) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemPostBinding.inflate(
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
        val binding: ItemPostBinding
    ) : RecyclerView.ViewHolder(
        binding.root
    ) {

        fun bind(communityPost: PostItem) {
            val data = communityPost.let {
                CommunityPost(
                    it.id,
                    it.title,
                    it.content,
                    it.category,
                    it.user.id,
                    it.user.name,
                    it.user.image,
                    it.byMe,
                    it.createdAt,
                    it.updatedAt,
                    it.location?.locationName ?: "",
                    it.location?.latitude ?: 0.0,
                    it.location?.longitude ?: 0.0,
                    it.totalComments
                )
            }
            binding.apply {
                userImage.load(if (data.userImage.isNullOrEmpty()) R.drawable.fallback_user else data.userImage) {
                    placeholder(R.drawable.fallback_user)
                    fallback(R.drawable.fallback_user)
                }
                userName.text = if (data.byMe) itemView.context.getString(
                    R.string.by_me,
                    data.userName
                ) else data.userName
                title.text = data.title
                if (data.locationName.isEmpty()) {
                    userLocation.visibility = View.GONE
                    pinLocation.visibility = View.GONE
                } else {
                    userLocation.visibility = View.VISIBLE
                    pinLocation.visibility = View.VISIBLE
                    userLocation.text = data.locationName
                }
                content.text = data.content
                tvCategory.text = data.category
                btnComment.text =
                    itemView.context.getString(R.string.total_comments, data.totalComments)
                btnMenu.setOnClickListener { v: View ->
                    val popup = PopupMenu(v.context, v)
                    popup.menuInflater.inflate(
                        if (data.byMe) R.menu.popup_menu_my_post else R.menu.popup_menu_post,
                        popup.menu
                    )
                    popup.setOnMenuItemClickListener { item ->
                        when (item.itemId) {
                            R.id.delete -> {
                                onDelete(data)
                                true
                            }

                            R.id.edit -> {
                                onEdit(data)
                                true
                            }

                            R.id.detail -> {
                                val intent =
                                    Intent(itemView.context, DetailPostActivity::class.java)
                                intent.putExtra(DetailPostActivity.EXTRA_POST, data)

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
                    intent.putExtra(DetailPostActivity.EXTRA_POST, data)
                    itemView.context.startActivity(intent)
                }

                btnComment.setOnClickListener {
                    val intent = Intent(itemView.context, DetailPostActivity::class.java)
                    intent.putExtra(DetailPostActivity.EXTRA_POST, data)
                    itemView.context.startActivity(intent)
                }
            }
        }

    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<PostItem>() {
            override fun areItemsTheSame(oldItem: PostItem, newItem: PostItem): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: PostItem,
                newItem: PostItem
            ): Boolean {
                return oldItem.content == newItem.content
            }
        }
    }
}
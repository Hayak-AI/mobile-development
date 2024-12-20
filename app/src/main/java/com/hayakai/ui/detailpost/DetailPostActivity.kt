package com.hayakai.ui.detailpost

import android.content.DialogInterface
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import coil3.load
import coil3.request.fallback
import coil3.request.placeholder
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.hayakai.R
import com.hayakai.data.local.entity.CommunityPost
import com.hayakai.data.remote.dto.DeleteCommentDto
import com.hayakai.data.remote.dto.DeletePostDto
import com.hayakai.data.remote.dto.NewPostCommentDto
import com.hayakai.databinding.ActivityDetailPostBinding
import com.hayakai.ui.common.LoadingStateAdapter
import com.hayakai.ui.community.CommunityViewModel
import com.hayakai.ui.editpost.EditPostActivity
import com.hayakai.ui.profile.ProfileActivity
import com.hayakai.utils.MyResult
import com.hayakai.utils.ViewModelFactory
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class DetailPostActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding: ActivityDetailPostBinding
    private val communityViewModel: CommunityViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }
    private val detailPostViewModel: DetailPostViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var communityPost: CommunityPost

    private lateinit var adapter: CommentPostListAdapter

    private val resultSelectMap =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == EditPostActivity.RESULT_CODE && result.data != null) {

                communityPost = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    result.data?.getParcelableExtra(
                        EditPostActivity.EXTRA_POST,
                        CommunityPost::class.java
                    )
                } else {
                    @Suppress("DEPRECATION")
                    result.data?.getParcelableExtra(EditPostActivity.EXTRA_POST)
                } ?: CommunityPost()
                setupView()
                setupViewModel()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailPostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()
        setupAction()
        setupData()
        setupView()

        if (savedInstanceState == null) {
            detailPostViewModel.getPostComments(communityPost.id)
        }

        setupViewModel()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun setupAction() {
        binding.backButton.setOnClickListener(this)
        binding.tilNewComment.setEndIconOnClickListener {
            val newPostCommentDto = NewPostCommentDto(
                communityPost.id,
                binding.etNewComment.text.toString()
            )
            newCommentPost(newPostCommentDto)
        }
    }

    private fun setupData() {
        communityPost = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(EXTRA_POST, CommunityPost::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra(EXTRA_POST)
        } ?: CommunityPost()
    }

    private fun setupView() {

        binding.apply {
            userImage.load(if (communityPost.userImage.isNullOrEmpty()) R.drawable.fallback_user else communityPost.userImage) {
                placeholder(R.drawable.fallback_user)
                fallback(R.drawable.fallback_user)
            }
            userName.text = if (communityPost.byMe) getString(
                R.string.by_me,
                communityPost.userName
            ) else communityPost.userName
            tvTitle.text = communityPost.title
            if (communityPost.locationName.isEmpty()) {
                userLocation.visibility = View.GONE
                pinLocation.visibility = View.GONE
            } else {
                userLocation.visibility = View.VISIBLE
                pinLocation.visibility = View.VISIBLE
                userLocation.text = communityPost.locationName
            }
            tvContent.text = communityPost.content
            tvCategory.text = communityPost.category
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

                        else -> false
                    }
                }
                popup.show()
            }
        }
    }

    private fun setupViewModel() {
        val layoutManager =
            LinearLayoutManager(
                this,
            )
        binding.comments.layoutManager = layoutManager
        adapter = CommentPostListAdapter(
            onClick = { commentPost ->
                MaterialAlertDialogBuilder(
                    this,
                    R.style.MaterialAlertDialog_DeleteConfirmation
                )
                    .setTitle("Hapus Komentar")
                    .setMessage("Apakah Anda yakin ingin menghapus komentar ini?")
                    .setPositiveButton(getString(R.string.yes)) { dialog, _ ->
                        deleteCommentPost(
                            dialog,
                            DeleteCommentDto(commentPost.id)
                        )
                    }
                    .setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                        dialog.dismiss()
                    }
                    .show()
            }
        )
        binding.comments.adapter = adapter
        binding.comments.adapter = adapter.withLoadStateFooter(
            footer = LoadingStateAdapter {
                adapter.retry()
            }
        )

        detailPostViewModel.postComments
            .observe(this) {
                adapter.submitData(lifecycle, it)
            }

        binding.swiperefresh.setOnRefreshListener {
            binding.comments.removeAllViewsInLayout()
            adapter.refresh()
            binding.swiperefresh.isRefreshing = false
        }
        binding.retryButton.setOnClickListener {
            adapter.retry()
        }
        lifecycleScope.launch {
            adapter.loadStateFlow.collectLatest { loadStates ->
                if (loadStates.refresh is LoadState.Error) {
                    binding.loadingLayout.visibility = View.VISIBLE
                } else if (loadStates.refresh is LoadState.NotLoading && adapter.itemCount == 0) {
                    binding.loadingLayout.visibility = View.VISIBLE
                } else {
                    binding.loadingLayout.visibility = View.GONE
                }
                binding.swiperefresh.isRefreshing = loadStates.refresh is LoadState.Loading
            }
        }
    }

    private fun newCommentPost(newPostCommentDto: NewPostCommentDto) {
        detailPostViewModel.newPostComment(newPostCommentDto)
            .observe(this) { result ->
                when (result) {
                    is MyResult.Loading -> {
                    }

                    is MyResult.Success -> {
                        binding.etNewComment.text?.clear()
                        detailPostViewModel.getPostComments(communityPost.id)
                        adapter.refresh()
                        Toast.makeText(
                            this,
                            "Berhasil menambahkan komentar",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    is MyResult.Error -> {
                        Toast.makeText(this, result.error, Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }
    }

    private fun deleteCommentPost(dialog: DialogInterface, deleteCommentDto: DeleteCommentDto) {
        detailPostViewModel.deletePostComment(deleteCommentDto)
            .observe(this) { result ->
                when (result) {
                    is MyResult.Loading -> {

                    }

                    is MyResult.Success -> {
                        Toast.makeText(this, result.data, Toast.LENGTH_SHORT).show()
                        detailPostViewModel.getPostComments(communityPost.id)
                        adapter.refresh()
                        dialog.dismiss()

                    }

                    is MyResult.Error -> {
                        Toast.makeText(this, result.error, Toast.LENGTH_SHORT).show()
                    }
                }
            }
    }

    private fun onDelete(communityPost: CommunityPost) {
        communityViewModel.deletePost(DeletePostDto(communityPost.id))
            .observe(this) { result ->
                when (result) {
                    is MyResult.Loading -> {

                    }

                    is MyResult.Success -> {
                        Toast.makeText(
                            this,
                            "Post deleted",
                            Toast.LENGTH_SHORT
                        ).show()
                        finish()
                    }

                    is MyResult.Error -> {
                        Toast.makeText(
                            this,
                            result.error,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }

    }

    private fun onEdit(communityPost: CommunityPost) {
        val intent = Intent(this, EditPostActivity::class.java)
        intent.putExtra(EditPostActivity.EXTRA_POST, communityPost)
        resultSelectMap.launch(intent)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.back_button -> {
                finish()
            }

            R.id.btn_profile -> {
                val intent = Intent(this, ProfileActivity::class.java)
                startActivity(intent)
            }

        }
    }

    companion object {
        const val EXTRA_POST = "extra_post"
    }
}
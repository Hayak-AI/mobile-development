package com.hayakai.ui.community

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.hayakai.R
import com.hayakai.data.remote.dto.DeletePostDto
import com.hayakai.databinding.FragmentExploreBinding
import com.hayakai.ui.common.LoadingStateAdapter
import com.hayakai.ui.editpost.EditPostActivity
import com.hayakai.utils.MyResult
import com.hayakai.utils.ViewModelFactory
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ExploreFragment : Fragment() {
    private var _binding: FragmentExploreBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val communityViewModel: CommunityViewModel by viewModels {
        ViewModelFactory.getInstance(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentExploreBinding.inflate(inflater, container, false)
        val root: View = binding.root



        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViewModel()
    }

    private fun setupViewModel() {

        val layoutManager =
            LinearLayoutManager(
                requireContext(),
            )
        binding.recyclerView.layoutManager = layoutManager
        val adapter = CommunityPostListAdapter(
            onEdit = { communityPost ->
                val intent = Intent(requireContext(), EditPostActivity::class.java)
                intent.putExtra(EditPostActivity.EXTRA_POST, communityPost)
                requireContext().startActivity(intent)
            },
            onDelete = { communityPost ->
                MaterialAlertDialogBuilder(
                    requireContext(),
                    R.style.MaterialAlertDialog_DeleteConfirmation
                )
                    .setTitle("Delete Post")
                    .setMessage("Are you sure you want to delete this post?")
                    .setPositiveButton("Yes") { dialog, _ ->
                        communityViewModel.deletePost(DeletePostDto(communityPost.id))
                            .observe(viewLifecycleOwner) { result ->
                                when (result) {
                                    is MyResult.Loading -> {

                                    }

                                    is MyResult.Success -> {
                                        Toast.makeText(
                                            requireContext(),
                                            "Post deleted",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }

                                    is MyResult.Error -> {
                                        Toast.makeText(
                                            requireContext(),
                                            result.error,
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                            }
                        dialog.dismiss()
                    }
                    .setNegativeButton("No") { dialog, _ ->
                        dialog.dismiss()
                    }
                    .show()
            }
        )
        binding.recyclerView.adapter = adapter.withLoadStateFooter(
            footer = LoadingStateAdapter {
                adapter.retry()
            }
        )

        communityViewModel.allPosts
            .observe(viewLifecycleOwner) {
                adapter.submitData(viewLifecycleOwner.lifecycle, it)
            }

        binding.swiperefresh.setOnRefreshListener {
            binding.recyclerView.removeAllViewsInLayout()
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

    override fun onResume() {
        super.onResume()
        setupViewModel()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
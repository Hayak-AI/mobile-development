package com.hayakai.ui.community

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.hayakai.R
import com.hayakai.data.remote.dto.DeletePostDto
import com.hayakai.databinding.FragmentExploreBinding
import com.hayakai.utils.MyResult
import com.hayakai.utils.ViewModelFactory

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
        communityViewModel.getAllPosts()
            .observe(viewLifecycleOwner) { result ->
                when (result) {
                    is MyResult.Loading -> {
                    }

                    is MyResult.Success -> {
//                        binding.tvNotFound.visibility =
//                            if (contacts.data.isEmpty()) View.VISIBLE else View.GONE
                        val layoutManager =
                            LinearLayoutManager(
                                requireContext(),
                            )
                        binding.recyclerView.layoutManager = layoutManager
                        val adapter = CommunityPostListAdapter(
                            onEdit = { communityPost ->
                                Toast.makeText(
                                    requireContext(),
                                    "Edit ${communityPost.title}",
                                    Toast.LENGTH_SHORT
                                ).show()
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
                                        dialog.dismiss()
                                    }
                                    .setNegativeButton("No") { dialog, _ ->
                                        dialog.dismiss()
                                    }
                                    .show()
                            }
                        )
                        adapter.submitList(result.data)
                        binding.recyclerView.adapter = adapter
                    }

                    is MyResult.Error -> {
                        Toast.makeText(requireContext(), result.error, Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
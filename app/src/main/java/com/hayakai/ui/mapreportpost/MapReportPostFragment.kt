package com.hayakai.ui.mapreportpost

import android.content.DialogInterface
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import coil3.load
import coil3.request.fallback
import coil3.request.placeholder
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.hayakai.R
import com.hayakai.data.local.entity.MapReport
import com.hayakai.data.remote.dto.DeleteCommentDto
import com.hayakai.data.remote.dto.DeleteReportMapDto
import com.hayakai.data.remote.dto.NewCommentReportDto
import com.hayakai.databinding.FragmentMapReportPostBinding
import com.hayakai.ui.common.LoadingStateAdapter
import com.hayakai.utils.MyResult
import com.hayakai.utils.ViewModelFactory
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MapReportPostFragment : BottomSheetDialogFragment(), View.OnClickListener {
    private var _binding: FragmentMapReportPostBinding? = null

    private val binding get() = _binding!!
    private lateinit var reportMap: MapReport

    private val mapReportPostFragmentViewModel: MapReportPostFragmentViewModel by viewModels {
        ViewModelFactory.getInstance(requireContext())
    }

    private lateinit var adapter: CommentReportListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMapReportPostBinding.inflate(inflater, container, false)
        val root: View = binding.root

        if (arguments != null) {
            reportMap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                arguments?.getParcelable(TAG, MapReport::class.java)
            } else {
                @Suppress("DEPRECATION")
                arguments?.getParcelable(TAG)
            } ?: MapReport()


            binding.authorImage.load(if (reportMap.userImage.isNullOrEmpty()) R.drawable.fallback_user else reportMap.userImage)
            binding.author.text = reportMap.userName
            binding.verified.text = if (reportMap.verified) "Verified" else "Not Verified"
            binding.name.text = reportMap.name
            binding.description.text = reportMap.description
            binding.image.load(reportMap.evidenceUrl) {
                fallback(R.drawable.fallback_report)
                placeholder(R.drawable.fallback_report)
            }
            binding.btnDelete.visibility = if (reportMap.byMe) View.VISIBLE else View.GONE

            mapReportPostFragmentViewModel.getReportComments(reportMap.id)
            setupViewModel()
        }

        return root
    }

    private fun setupViewModel() {
        val layoutManager =
            LinearLayoutManager(
                requireContext(),
            )
        binding.comments.layoutManager = layoutManager
        adapter = CommentReportListAdapter(
            onClick = { commentReport ->
                MaterialAlertDialogBuilder(
                    requireContext(),
                    R.style.MaterialAlertDialog_DeleteConfirmation
                )
                    .setTitle("Hapus Komentar")
                    .setMessage("Apakah Anda yakin ingin menghapus komentar ini?")
                    .setPositiveButton(getString(R.string.yes)) { dialog, _ ->
                        deleteCommentReport(
                            dialog,
                            DeleteCommentDto(commentReport.id)
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

        mapReportPostFragmentViewModel.reportComments
            .observe(viewLifecycleOwner) {
                adapter.submitData(lifecycle, it)
            }

//        binding.swiperefresh.setOnRefreshListener {
//            binding.comments.removeAllViewsInLayout()
//            adapter.refresh()
//            binding.swiperefresh.isRefreshing = false
//        }
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
//                binding.swiperefresh.isRefreshing = loadStates.refresh is LoadState.Loading
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Behavior of the bottom sheet
        val behavior = BottomSheetBehavior.from(binding.bottomSheet)


//
//        binding.dragHandle.setOnDragListener { _, _ ->
//            behavior.state = BottomSheetBehavior.STATE_EXPANDED
//            Log.d(TAG, "onViewCreated: Drag Handle Clicked")
//            false
//        }
        behavior.apply {

//            addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
//                override fun onStateChanged(bottomSheet: View, newState: Int) {
//                    if (newState == BottomSheetBehavior.STATE_HIDDEN) {
//                        dismiss()
//                    }
//
//                }
//
//                override fun onSlide(bottomSheet: View, slideOffset: Float) {
//                }
//            })
        }

        setupAction()
    }

    private fun newCommentReport(newCommentReportDto: NewCommentReportDto) {
        mapReportPostFragmentViewModel.newCommentReport(newCommentReportDto)
            .observe(viewLifecycleOwner) { result ->
                when (result) {
                    is MyResult.Loading -> {
                    }

                    is MyResult.Success -> {
                        binding.etCreateComment.text?.clear()
                        mapReportPostFragmentViewModel.getReportComments(reportMap.id)
                        adapter.refresh()
                        Toast.makeText(
                            requireContext(),
                            "Berhasil menambahkan komentar",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    is MyResult.Error -> {
                        Toast.makeText(requireContext(), result.error, Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }
    }

    fun setupAction() {
        binding.btnDelete.setOnClickListener(this)
        binding.tilCreateComment.setEndIconOnClickListener {
            val comment = binding.etCreateComment.text.toString()
            if (comment.isNotEmpty()) {
                newCommentReport(
                    NewCommentReportDto(
                        reportMap.id,
                        comment
                    )
                )
            }

        }
    }

    private fun deleteReportMap(dialog: DialogInterface, deleteReportMapDto: DeleteReportMapDto) {
        mapReportPostFragmentViewModel.deleteReportMap(deleteReportMapDto)
            .observe(viewLifecycleOwner) { result ->
                when (result) {
                    is MyResult.Loading -> {

                    }

                    is MyResult.Success -> {
                        Toast.makeText(requireContext(), result.data, Toast.LENGTH_SHORT).show()
                        dialog.dismiss()
                        dismiss()
                    }

                    is MyResult.Error -> {
                        Toast.makeText(requireContext(), result.error, Toast.LENGTH_SHORT).show()
                    }
                }
            }
    }

    private fun deleteCommentReport(dialog: DialogInterface, deleteCommentDto: DeleteCommentDto) {
        mapReportPostFragmentViewModel.deleteCommentReport(deleteCommentDto)
            .observe(viewLifecycleOwner) { result ->
                when (result) {
                    is MyResult.Loading -> {

                    }

                    is MyResult.Success -> {
                        mapReportPostFragmentViewModel.getReportComments(reportMap.id)
                        adapter.refresh()
                        Toast.makeText(
                            requireContext(),
                            "Berhasil menghapus komentar",
                            Toast.LENGTH_SHORT
                        ).show()
                        dialog.dismiss()
                    }

                    is MyResult.Error -> {
                        Toast.makeText(requireContext(), result.error, Toast.LENGTH_SHORT).show()
                    }
                }
            }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btn_delete -> {
                MaterialAlertDialogBuilder(
                    requireContext(),
                    R.style.MaterialAlertDialog_DeleteConfirmation
                )
                    .setTitle("Hapus Laporan?")
                    .setMessage("Apakah Anda yakin ingin menghapus laporan ini?")
                    .setPositiveButton(getString(R.string.yes)) { dialog, _ ->
                        deleteReportMap(dialog, DeleteReportMapDto(reportMap.id))
                    }
                    .setNegativeButton(getString(R.string.cancel)) { dialog, _ ->
                        dialog.dismiss()
                    }
                    .show()
            }


        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "MapReportPostFragment"
    }
}
package com.hayakai.ui.mapreportpost

import android.content.DialogInterface
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.Toast
import androidx.fragment.app.viewModels
import coil3.load
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.hayakai.R
import com.hayakai.data.local.entity.MapReport
import com.hayakai.data.remote.dto.DeleteReportMapDto
import com.hayakai.databinding.FragmentMapReportPostBinding
import com.hayakai.utils.MyResult
import com.hayakai.utils.ViewModelFactory

class MapReportPostFragment : BottomSheetDialogFragment(), View.OnClickListener {
    private var _binding: FragmentMapReportPostBinding? = null

    private val binding get() = _binding!!
    private lateinit var reportMap: MapReport

    private val mapReportPostFragmentViewModel: MapReportPostFragmentViewModel by viewModels {
        ViewModelFactory.getInstance(requireContext())
    }


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


            binding.authorImage.load(reportMap.userImage)
            binding.author.text = reportMap.userName
            binding.verified.text = if (reportMap.verified) "Verified" else "Not Verified"
            binding.name.text = reportMap.name
            binding.description.text = reportMap.description
            binding.image.load(reportMap.evidenceUrl)
            binding.btnDelete.visibility = if (reportMap.byMe) View.VISIBLE else View.GONE

            // comments map report
        }

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bottomSheet: FrameLayout =
            dialog?.findViewById(com.google.android.material.R.id.design_bottom_sheet)!!

        // Height of the view
        bottomSheet.layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT

        // Behavior of the bottom sheet
        val behavior = BottomSheetBehavior.from(bottomSheet)
        behavior.apply {
            peekHeight = resources.displayMetrics.heightPixels // Pop-up height
            state = BottomSheetBehavior.STATE_EXPANDED // Expanded state

            addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                        dismiss()
                    }
                }

                override fun onSlide(bottomSheet: View, slideOffset: Float) {}
            })
        }

        setupAction()
    }


    fun setupAction() {
        binding.btnDelete.setOnClickListener(this)
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

    override fun onClick(v: View?) {
        when (v) {
            binding.btnDelete -> {
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
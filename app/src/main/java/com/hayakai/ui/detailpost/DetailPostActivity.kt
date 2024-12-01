package com.hayakai.ui.detailpost

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import coil3.load
import com.hayakai.R
import com.hayakai.data.local.entity.CommunityPost
import com.hayakai.data.remote.dto.DeletePostDto
import com.hayakai.databinding.ActivityDetailPostBinding
import com.hayakai.ui.community.CommunityViewModel
import com.hayakai.ui.profile.ProfileActivity
import com.hayakai.utils.MyResult
import com.hayakai.utils.ViewModelFactory

class DetailPostActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding: ActivityDetailPostBinding
    private val communityViewModel: CommunityViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var communityPost: CommunityPost
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailPostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()
        setupAction()
        setupView()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun setupAction() {
        binding.backButton.setOnClickListener(this)
    }

    private fun setupView() {
        communityPost = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra(EXTRA_POST, CommunityPost::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra(EXTRA_POST)
        } ?: CommunityPost()

        binding.apply {
            userImage.load(communityPost.userImage)
            userName.text = communityPost.userName
            tvTitle.text = communityPost.title
            userLocation.text = communityPost.locationName
            tvContent.text = communityPost.content

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

            R.id.btn_save -> {
//                if (!validateName() or !validatePhoneNumber() or !validateEmail() or !validateMessage()) return
//
//                val name = binding.etFullName.text.toString()
//                val phoneNumber = binding.etPhone.text.toString()
//                val email = binding.etEmail.text.toString()
//                val message = binding.etMessage.text.toString()
//                val notify = binding.switchNotify.isChecked
//
//                updateContact(
//                    UpdateContactDto(
//                        contact.id,
//                        name,
//                        email,
//                        phoneNumber,
//                        notify,
//                        message
//                    )
//                )

            }

        }
    }

    companion object {
        const val EXTRA_POST = "extra_post"
    }
}
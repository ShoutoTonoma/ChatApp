package com.example.chatapp.ui.fragments


import android.view.View
import com.example.chatapp.MainActivity
import com.example.chatapp.activities.RegisterActivity
import com.example.chatapp.databinding.FragmentSettingsBinding
import com.example.chatapp.utilits.APP_ACTIVITY
import com.example.chatapp.utilits.AUTH
import com.example.chatapp.utilits.USER
import com.example.chatapp.utilits.downloadAndSetImage
import com.example.chatapp.utilits.replaceActivity
import com.example.chatapp.utilits.setCurrentFragment


class SettingsFragment : BaseFragment<FragmentSettingsBinding>(
    FragmentSettingsBinding::inflate
) {
    

    override fun onStart() {
        super.onStart()
        initFields()
    }


    private fun initFields() {
        APP_ACTIVITY.mBinding.tvActionBar.text = "Settings"
        binding.settingsFullName.text = USER.fullname
        binding.settingsUserPhoto.downloadAndSetImage(USER.photoUrl)
    }

    private fun initFunc() {
        binding.settingsLogout.setOnClickListener {
            AUTH.signOut()
            (activity as MainActivity).replaceActivity(RegisterActivity())
        }
        binding.settingsBtnEditProfile.setOnClickListener {
            APP_ACTIVITY.setCurrentFragment(EditProfileFragment())
            APP_ACTIVITY.mBinding.tvActionBar.text = "Edit profile"
            APP_ACTIVITY.mBinding.backArrowLayout.visibility = View.VISIBLE
        }
    }


    override fun onResume() {
        super.onResume()
        APP_ACTIVITY.mBinding.backArrowLayout.visibility = View.GONE
        initFields()
        initFunc()
    }
}
package com.example.chatapp.ui.fragments


import android.view.View
import com.example.chatapp.databinding.FragmentSettingsBinding
import com.example.chatapp.ui.fragments.base.BaseFragment
import com.example.chatapp.utilits.APP_ACTIVITY
import com.example.chatapp.database.AUTH
import com.example.chatapp.utilits.AppStates
import com.example.chatapp.database.USER
import com.example.chatapp.utilits.downloadAndSetImage
import com.example.chatapp.utilits.replaceFragment
import com.example.chatapp.utilits.restartActivity


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
        binding.settingsStatus.text = USER.status
        binding.settingsUserPhoto.downloadAndSetImage(USER.photoUrl)
    }

    private fun initFunc() {
        binding.settingsLogout.setOnClickListener {
            AppStates.updateStates(AppStates.OFFLINE)
            AUTH.signOut()
            restartActivity()
        }
        binding.settingsBtnEditProfile.setOnClickListener {
            replaceFragment(EditProfileFragment())
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
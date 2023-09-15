package com.example.chatapp.ui.fragments


import android.app.Activity
import android.content.Intent
import com.example.chatapp.R
import com.example.chatapp.databinding.FragmentEditProfileBinding
import com.example.chatapp.ui.fragments.base.BaseFragment
import com.example.chatapp.utilits.APP_ACTIVITY
import com.example.chatapp.database.CURRENT_UID
import com.example.chatapp.database.FOLDER_PROFILE_IMAGE
import com.example.chatapp.database.REF_STORAGE_ROOT
import com.example.chatapp.database.USER
import com.example.chatapp.utilits.downloadAndSetImage
import com.example.chatapp.database.getUrlFromStorage
import com.example.chatapp.database.putImageToStorage
import com.example.chatapp.database.putUrlToDatabase
import com.example.chatapp.utilits.replaceFragment
import com.example.chatapp.database.setValuesToDatabase
import com.example.chatapp.utilits.showToast
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView

class EditProfileFragment : BaseFragment<FragmentEditProfileBinding>(
    FragmentEditProfileBinding::inflate
) {


    override fun onResume() {
        super.onResume()
        initFullnameList()
        initFields()
    }

    private fun initFields() {
        APP_ACTIVITY.mBinding.backArrowLayout.setOnClickListener { replaceFragment(SettingsFragment()) }
        with(binding) {
            dataSaveBtn.setOnClickListener { change() }
            editProfileBtn.setOnClickListener { changePhotoUser() }
            editProfileUserPhoto.downloadAndSetImage(USER.photoUrl)
        }

    }

    private fun changePhotoUser() {
        CropImage.activity()
            .setAspectRatio(1, 1)
            .setRequestedSize(250, 250)
            .setCropShape(CropImageView.CropShape.OVAL)
            .start(APP_ACTIVITY, this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE
            &&  resultCode == Activity.RESULT_OK && data != null) {
            val uri = CropImage.getActivityResult(data).uri
            val path = REF_STORAGE_ROOT.child(FOLDER_PROFILE_IMAGE)
                .child(CURRENT_UID)
            putImageToStorage(uri, path) {
                getUrlFromStorage(path) {
                    putUrlToDatabase(it) {
                        binding.editProfileUserPhoto.downloadAndSetImage(it)
                        showToast(getString(R.string.toast_data_update))
                        USER.photoUrl = it
                    }
                }
            }
        }
    }

    private fun initFullnameList() {
        val fullDataList = listOf(USER.fullname, USER.region)
        with(binding) {
            epFullnameInput.setText(fullDataList[0])
            epRegionInput.setText(fullDataList[1])
        }
    }


    private fun change() {
        val fullName = binding.epFullnameInput.text.toString()
        val region = binding.epRegionInput.text.toString()
        setValuesToDatabase(fullName, region)
    }

}
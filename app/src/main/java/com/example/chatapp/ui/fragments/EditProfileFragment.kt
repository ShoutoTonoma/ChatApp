package com.example.chatapp.ui.fragments


import android.app.Activity
import android.content.Intent
import com.example.chatapp.R
import com.example.chatapp.databinding.FragmentEditProfileBinding
import com.example.chatapp.utilits.APP_ACTIVITY
import com.example.chatapp.utilits.CHILD_FULLNAME
import com.example.chatapp.utilits.CHILD_PHONE
import com.example.chatapp.utilits.CHILD_REGION
import com.example.chatapp.utilits.CURRENT_UID
import com.example.chatapp.utilits.FOLDER_PROFILE_IMAGE
import com.example.chatapp.utilits.NODE_USERS
import com.example.chatapp.utilits.REF_DATABASE_ROOT
import com.example.chatapp.utilits.REF_STORAGE_ROOT
import com.example.chatapp.utilits.USER
import com.example.chatapp.utilits.downloadAndSetImage
import com.example.chatapp.utilits.getUrlFromStorage
import com.example.chatapp.utilits.putImageToStorage
import com.example.chatapp.utilits.putUrlToDatabase
import com.example.chatapp.utilits.setCurrentFragment
import com.example.chatapp.utilits.setValueToDb
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

    fun initFields() {
        APP_ACTIVITY.mBinding.backArrowLayout.setOnClickListener { APP_ACTIVITY.setCurrentFragment(SettingsFragment())}
        with(binding) {
            dataSaveBtn.setOnClickListener { change() }
            editProfileBtn.setOnClickListener { changePhotoUser() }
            editProfileUserPhoto.downloadAndSetImage(USER.photoUrl)
        }

    }

    private fun changePhotoUser() {
        CropImage.activity()
            .setAspectRatio(1, 1)
            .setRequestedSize(600, 600)
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

        if (fullName.isEmpty() && region.isEmpty()) {
            showToast("Заполните все данные")
        } else {
            setValueToDb(region, CHILD_REGION) {
                USER.region = region
            }
            setValueToDb(fullName, CHILD_FULLNAME) {
                    showToast(getString(R.string.toast_data_update))
                    USER.fullname = fullName
                    APP_ACTIVITY.setCurrentFragment(SettingsFragment())
            }
        }
    }

}
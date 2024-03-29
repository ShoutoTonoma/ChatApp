package com.example.chatapp.ui.fragments

import android.view.View
import androidx.fragment.app.Fragment
import com.example.chatapp.R
import com.example.chatapp.utilits.APP_ACTIVITY


class MainFragment : Fragment(R.layout.fragment_chats) {


    override fun onResume() {
        super.onResume()
        APP_ACTIVITY.mBinding.tvActionBar.text = "Messages"
        APP_ACTIVITY.mBinding.backArrowLayout.visibility = View.GONE
    }
}
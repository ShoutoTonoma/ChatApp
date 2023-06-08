package com.example.chatapp.ui.fragments

import android.view.View
import androidx.fragment.app.Fragment
import com.example.chatapp.MainActivity
import com.example.chatapp.R
import com.example.chatapp.utilits.APP_ACTIVITY


class ContactsFragment : Fragment(R.layout.fragment_contacts) {


    override fun onResume() {
        super.onResume()
        APP_ACTIVITY.mBinding.backArrowLayout.visibility = View.GONE
    }
}
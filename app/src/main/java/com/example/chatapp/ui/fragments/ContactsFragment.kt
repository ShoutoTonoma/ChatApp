package com.example.chatapp.ui.fragments

import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapp.MainActivity
import com.example.chatapp.R
import com.example.chatapp.models.CommonModel
import com.example.chatapp.utilits.APP_ACTIVITY
import com.firebase.ui.database.FirebaseRecyclerAdapter


class ContactsFragment : Fragment(R.layout.fragment_contacts) {

//    private lateinit var mRecyclerView: RecyclerView
//    private lateinit var mAdapter: FirebaseRecyclerAdapter<CommonModel, ContactsFragment>

    override fun onResume() {
        super.onResume()
        APP_ACTIVITY.mBinding.backArrowLayout.visibility = View.GONE
        APP_ACTIVITY.title = "Contacts"
    }
}
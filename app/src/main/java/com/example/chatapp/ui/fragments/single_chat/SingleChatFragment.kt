package com.example.chatapp.ui.fragments.single_chat

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.view.MotionEvent
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.AbsListView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.agrawalsuneet.dotsloader.loaders.LazyLoader
import com.example.chatapp.R
import com.example.chatapp.databinding.FragmentSingleChatBinding
import com.example.chatapp.models.CommonModel
import com.example.chatapp.models.UserModel
import com.example.chatapp.ui.fragments.base.BaseFragment
import com.example.chatapp.ui.fragments.ContactsFragment
import com.example.chatapp.utilits.APP_ACTIVITY
import com.example.chatapp.utilits.AppValueEventListener
import com.example.chatapp.database.CURRENT_UID
import com.example.chatapp.database.FOLDER_MESSAGE_IMAGE
import com.example.chatapp.database.NODE_MESSAGES
import com.example.chatapp.database.NODE_USERS
import com.example.chatapp.database.REF_DATABASE_ROOT
import com.example.chatapp.database.REF_STORAGE_ROOT
import com.example.chatapp.database.TYPE_TEXT
import com.example.chatapp.database.getUrlFromStorage
import com.example.chatapp.database.putImageToStorage
import com.example.chatapp.utilits.downloadAndSetImage
import com.example.chatapp.utilits.getCommonModel
import com.example.chatapp.utilits.getUserModel
import com.example.chatapp.utilits.replaceFragment
import com.example.chatapp.database.sendMessage
import com.example.chatapp.database.sendMessageAsImage
import com.example.chatapp.utilits.AppChildEventListener
import com.example.chatapp.utilits.AppTextWatcher
import com.example.chatapp.utilits.RECORD_AUDIO
import com.example.chatapp.utilits.checkPermissions
import com.example.chatapp.utilits.showToast
import com.google.firebase.database.DatabaseReference
import com.theartofdev.edmodo.cropper.CropImage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class SingleChatFragment(private val contact: CommonModel) :
    BaseFragment<FragmentSingleChatBinding>(
        FragmentSingleChatBinding::inflate
    ) {

    private lateinit var mListenerInfoToolbar: AppValueEventListener
    private lateinit var mReceivingUser: UserModel
    private lateinit var mRefUsers: DatabaseReference
    private lateinit var mRefMessages: DatabaseReference
    private lateinit var mAdapter: SingleChatAdapter
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mMessageListener: AppChildEventListener
    private var mCountMessages = 10
    private var mIsScrolling = false
    private var mSmoothScrollToPosition = true
    private lateinit var mSwipeRefreshLayout: SwipeRefreshLayout
    private lateinit var mLayoutManager: LinearLayoutManager


    override fun onResume() {
        super.onResume()
        initFields()
        initToolbar()
        initRecyclerView()
//        initLazyLoadingAnim()
    }


    @SuppressLint("ClickableViewAccessibility")
    private fun initFields() {
        APP_ACTIVITY.mBinding.tvActionBar.visibility = View.GONE
        APP_ACTIVITY.mBinding.bottomNav.visibility = View.GONE
        binding.backArrowChat.setOnClickListener { replaceFragment(ContactsFragment()) }
        mSwipeRefreshLayout = binding.chatSwipeRefresh
        mLayoutManager = LinearLayoutManager(this.context)

        binding.chatInputMessage.addTextChangedListener( AppTextWatcher {
            val string = binding.chatInputMessage.text.toString()

            if (string.isEmpty() || string == "Recording") {
                binding.chatBtnSendMessage.visibility = View.GONE
                binding.chatBtnSendVoice.visibility = View.VISIBLE
//                binding.myLazyLoader.visibility = View.VISIBLE
            } else {
                binding.chatBtnSendMessage.visibility = View.VISIBLE
                binding.chatBtnSendVoice.visibility = View.GONE
//                binding.myLazyLoader.visibility = View.GONE
            }
        } )

        binding.chatBtnAttach.setOnClickListener { attachFile() }

        CoroutineScope(Dispatchers.IO).launch {
            binding.chatBtnSendVoice.setOnTouchListener { v, event ->
                if (checkPermissions(RECORD_AUDIO)) {
                    when (event.action) {
                        MotionEvent.ACTION_DOWN -> {
                            // TODO record
                            binding.chatInputMessage.setText("Recording")
                            binding.chatInputMessage.visibility = View.GONE
                            binding.myLazyLoader.visibility = View.VISIBLE

                        }
                        MotionEvent.ACTION_UP -> {
                            // TODO stop record
                            binding.chatInputMessage.setText("")
                            binding.chatInputMessage.visibility = View.VISIBLE
                            binding.myLazyLoader.visibility = View.GONE
                        }
                    }
                }
                true
            }
        }
    }

    private fun attachFile() {
        CropImage.activity()
            .setAspectRatio(1, 1)
            .setRequestedSize(250, 250)
            .start(APP_ACTIVITY, this)
    }

    private fun initRecyclerView() {
        mRecyclerView = binding.chatRecyclerView
        mAdapter = SingleChatAdapter()
        mRefMessages = REF_DATABASE_ROOT
            .child(NODE_MESSAGES)
            .child(CURRENT_UID)
            .child(contact.id)
        mRecyclerView.adapter = mAdapter
        mRecyclerView.setHasFixedSize(true)
        mRecyclerView.isNestedScrollingEnabled = false
        mRecyclerView.layoutManager = mLayoutManager

        mMessageListener = AppChildEventListener {
            val message = it.getCommonModel()

            if (mSmoothScrollToPosition) {
                mAdapter.addItemToBottom(message) {
                    mRecyclerView.smoothScrollToPosition(mAdapter.itemCount)
                }
            } else {
                mAdapter.addItemToTop(message) {
                    mSwipeRefreshLayout.isRefreshing = false
                }
            }
        }
        mRefMessages.limitToLast(mCountMessages).addChildEventListener(mMessageListener)

        mRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (mIsScrolling && dy < 0 && mLayoutManager.findFirstVisibleItemPosition() <= 3) {
                    updateData()
                }
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    mIsScrolling = true
                }
            }
        })

        mSwipeRefreshLayout.setOnRefreshListener { updateData() }
    }

    private fun updateData() {
        mSmoothScrollToPosition = false
        mIsScrolling = false
        mCountMessages += 10
        mRefMessages.removeEventListener(mMessageListener)
        mRefMessages.limitToLast(mCountMessages).addChildEventListener(mMessageListener)
    }

    private fun initToolbar() {
        mListenerInfoToolbar = AppValueEventListener {
            mReceivingUser = it.getUserModel()
            initInfoToolbar()
        }
        mRefUsers = REF_DATABASE_ROOT.child(NODE_USERS).child(contact.id)
        mRefUsers.addValueEventListener(mListenerInfoToolbar)
        binding.chatBtnSendMessage.setOnClickListener {
            val message = binding.chatInputMessage.text.toString()
            mSmoothScrollToPosition = true
            if (message.isEmpty()) {
                showToast("Введите сообщение")
            } else sendMessage(message, contact.id, TYPE_TEXT) {
                binding.chatInputMessage.setText("")
            }
        }
    }

    private fun initInfoToolbar() {
        with(binding) {
            if (mReceivingUser.fullname.isEmpty()) {
                toolbarChatFullname.text = contact.fullname
            } else toolbarChatFullname.text = mReceivingUser.fullname

            toolbarChatPhoto.downloadAndSetImage(mReceivingUser.photoUrl)
            toolbarChatStatus.text = mReceivingUser.status
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE
            && resultCode == Activity.RESULT_OK && data != null
        ) {
            val uri = CropImage.getActivityResult(data).uri

            val messageKey = REF_DATABASE_ROOT.child(NODE_MESSAGES)
                .child(CURRENT_UID)
                .child(contact.id).push().key.toString()

            val path = REF_STORAGE_ROOT
                .child(FOLDER_MESSAGE_IMAGE)
                .child(messageKey)
            putImageToStorage(uri, path) {
                getUrlFromStorage(path) {
                    sendMessageAsImage(contact.id, it, messageKey)
                    mSmoothScrollToPosition = true
                }
            }
        }
    }


    override fun onPause() {
        super.onPause()
        mRefUsers.removeEventListener(mListenerInfoToolbar)
        mRefMessages.removeEventListener(mMessageListener)
    }
}
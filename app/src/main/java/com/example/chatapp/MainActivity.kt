package com.example.chatapp

import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import com.example.chatapp.databinding.ActivityMainBinding
import com.example.chatapp.ui.fragments.MainFragment
import com.example.chatapp.ui.fragments.ContactsFragment
import com.example.chatapp.ui.fragments.SettingsFragment
import com.example.chatapp.ui.fragments.register.LoginFragment
import com.example.chatapp.utilits.APP_ACTIVITY
import com.example.chatapp.database.AUTH
import com.example.chatapp.utilits.AppStates
import com.example.chatapp.utilits.READ_CONTACTS
import com.example.chatapp.utilits.initContacts
import com.example.chatapp.database.initFirebase
import com.example.chatapp.database.initUser
import com.example.chatapp.utilits.replaceFragment
import com.example.chatapp.utilits.showToast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    lateinit var mBinding: ActivityMainBinding
    private var currentPageId = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        APP_ACTIVITY = this
        initFirebase()
        initUser {
            CoroutineScope(Dispatchers.IO).launch {
                initContacts()
            }
            initFields()
            initFunc()
        }
    }


    private fun initFields() {
        APP_ACTIVITY.mBinding.bottomNav.visibility = View.VISIBLE
        replaceFragment(MainFragment())
    }


    private fun initFunc() {
        if(AUTH.currentUser != null) {
            replaceFragment(MainFragment())
        } else {
            replaceFragment(LoginFragment())
        }

        mBinding.bottomNav.setOnItemSelectedListener {
            if (currentPageId == it.itemId) {
                false
            } else {
                currentPageId = it.itemId
                when(it.itemId) {
                    R.id.bottom_chats -> {
                        mBinding.tvActionBar.text = "Messages"
                        replaceFragment(MainFragment())
                    }
                    R.id.bottom_contact -> {
                        mBinding.tvActionBar.text = "Contacts"
                        replaceFragment(ContactsFragment())
                    }
                    R.id.bottom_settings -> {
                        mBinding.tvActionBar.text = "Settings"
                        replaceFragment(SettingsFragment())
                    }
                }
                true
            }

        }
    }

    override fun onStart() {
        super.onStart()
        AppStates.updateStates(AppStates.ONLINE)
    }

    override fun onStop() {
        super.onStop()
        AppStates.updateStates(AppStates.OFFLINE)

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(ContextCompat.checkSelfPermission(APP_ACTIVITY, READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            initContacts()
        }
    }
}
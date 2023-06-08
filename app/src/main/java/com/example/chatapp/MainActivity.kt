package com.example.chatapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.chatapp.activities.RegisterActivity
import com.example.chatapp.databinding.ActivityMainBinding
import com.example.chatapp.ui.fragments.ChatsFragment
import com.example.chatapp.ui.fragments.ContactsFragment
import com.example.chatapp.ui.fragments.SettingsFragment
import com.example.chatapp.utilits.APP_ACTIVITY
import com.example.chatapp.utilits.AUTH
import com.example.chatapp.utilits.READ_CONTACTS
import com.example.chatapp.utilits.checkPermissions
import com.example.chatapp.utilits.initFirebase
import com.example.chatapp.utilits.initUser
import com.example.chatapp.utilits.replaceActivity
import com.example.chatapp.utilits.setCurrentFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    lateinit var mBinding: ActivityMainBinding

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

    private fun initContacts() {
        if (checkPermissions(READ_CONTACTS)) {
            val array = arrayOfNulls<Int>(900000)
            array.forEach { println(it) }
        }
    }


    private fun initFields() {
        setCurrentFragment(ChatsFragment())
    }


    private fun initFunc() {
        mBinding.bottomNav.setOnItemSelectedListener {
            when(it.itemId) {
                R.id.bottom_chats -> {
                    mBinding.tvActionBar.text = "Messages"
                    mBinding.imageBtnSearch.visibility = View.VISIBLE
                    setCurrentFragment(ChatsFragment())
                }
                R.id.bottom_contact -> {
                    mBinding.tvActionBar.text = "Contacts"
                    mBinding.imageBtnSearch.visibility = View.VISIBLE
                    setCurrentFragment(ContactsFragment())
                }
                R.id.bottom_settings -> {
                    mBinding.tvActionBar.text = "Settings"
                    mBinding.imageBtnSearch.visibility = View.GONE
                    setCurrentFragment(SettingsFragment())
                }
            }
            true
        }

        if(AUTH.currentUser == null) {
            replaceActivity(RegisterActivity())
        }
    }
}
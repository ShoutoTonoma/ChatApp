package com.example.chatapp.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.chatapp.databinding.ActivityRegisterBinding
import com.example.chatapp.ui.fragments.LoginFragment
import com.example.chatapp.utilits.initFirebase
import com.example.chatapp.utilits.replaceFragment

class RegisterActivity : AppCompatActivity() {

    lateinit var mBinding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        initFirebase()
    }

    override fun onStart() {
        super.onStart()
        replaceFragment(LoginFragment(), false)
    }
}
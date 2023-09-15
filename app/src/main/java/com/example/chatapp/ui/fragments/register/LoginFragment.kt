package com.example.chatapp.ui.fragments.register

import com.example.chatapp.R
import com.example.chatapp.databinding.FragmentLoginBinding
import com.example.chatapp.ui.fragments.base.BaseRegisterFragment
import com.example.chatapp.utilits.APP_ACTIVITY
import com.example.chatapp.database.AUTH
import com.example.chatapp.utilits.checkEmptyText
import com.example.chatapp.utilits.replaceFragment
import com.example.chatapp.utilits.restartActivity
import com.example.chatapp.utilits.showToast


class LoginFragment : BaseRegisterFragment<FragmentLoginBinding>(
    FragmentLoginBinding::inflate
) {

    override fun onStart() {
        super.onStart()
        APP_ACTIVITY.mBinding.tvActionBar.text = getString(R.string.toolbar_login)
        binding.signUpLink.setOnClickListener {
            replaceFragment(RegisterFragment())
        }
        userLogin()
    }

    private fun userLogin() {
        binding.btnSignIn.setOnClickListener {
            when {
                checkEmptyText(binding.emailInput) -> {
                    showToast("Please enter email")
                }

                checkEmptyText(binding.passwordInput) -> {
                    showToast("Please enter password")
                } else -> {
                val email: String = binding.emailInput.text.toString().trim { it <= ' ' }
                val password: String = binding.passwordInput.text.toString().trim { it <= ' ' }

                AUTH.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if(task.isSuccessful) {
                            restartActivity()
                            showToast("You are logged in successfully")
                        } else {
                            showToast(task.exception!!.message.toString())
                        }
                    }
            }
            }
        }
    }
}
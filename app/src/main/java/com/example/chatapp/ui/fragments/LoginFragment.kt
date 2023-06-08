package com.example.chatapp.ui.fragments

import com.example.chatapp.MainActivity
import com.example.chatapp.R
import com.example.chatapp.activities.RegisterActivity
import com.example.chatapp.databinding.FragmentLoginBinding
import com.example.chatapp.utilits.AUTH
import com.example.chatapp.utilits.checkEmptyText
import com.example.chatapp.utilits.replaceActivity
import com.example.chatapp.utilits.replaceFragment
import com.example.chatapp.utilits.showToast


class LoginFragment : BaseFragment<FragmentLoginBinding>(
    FragmentLoginBinding::inflate
) {

    override fun onStart() {
        super.onStart()
        (activity as RegisterActivity).mBinding.tvLogin.text = getString(R.string.toolbar_login)
        binding.signUpLink.setOnClickListener {
            replaceFragment(RegisterFragment(), false)
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
                            (activity as RegisterActivity).replaceActivity(MainActivity())
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
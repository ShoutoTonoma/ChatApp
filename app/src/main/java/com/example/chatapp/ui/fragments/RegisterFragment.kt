package com.example.chatapp.ui.fragments

import com.example.chatapp.R
import com.example.chatapp.activities.RegisterActivity
import com.example.chatapp.databinding.FragmentRegisterBinding
import com.example.chatapp.utilits.AUTH
import com.example.chatapp.utilits.CHILD_EMAIL
import com.example.chatapp.utilits.CHILD_FULLNAME
import com.example.chatapp.utilits.CHILD_ID
import com.example.chatapp.utilits.NODE_USERS
import com.example.chatapp.utilits.REF_DATABASE_ROOT
import com.example.chatapp.utilits.checkEmptyText
import com.example.chatapp.utilits.replaceFragment
import com.example.chatapp.utilits.showToast


class RegisterFragment : BaseFragment<FragmentRegisterBinding>(
    FragmentRegisterBinding::inflate
) {

    override fun onStart() {
        super.onStart()
        (activity as RegisterActivity).mBinding.tvLogin.text = getString(R.string.toolbar_registration)
        binding.signInLink.setOnClickListener {
            replaceFragment(LoginFragment(), false)
        }
        userRegister()
    }

    private fun userRegister() {
        binding.btnSignUp.setOnClickListener {
            when {
                checkEmptyText(binding.nameInput) -> {
                    showToast("Please enter name")
                }

                checkEmptyText(binding.emailInput) -> {
                    showToast("Please enter email")
                }

                checkEmptyText(binding.passwordInput) -> {
                    showToast("Please enter password")
                } else -> {
                    val name: String = binding.nameInput.text.toString().trim { it <= ' ' }
                    val email: String = binding.emailInput.text.toString().trim { it <= ' ' }
                    val password: String = binding.passwordInput.text.toString().trim { it <= ' ' }
                    val dateMap = mutableMapOf<String, Any>()

                    dateMap[CHILD_FULLNAME] = name
                    dateMap[CHILD_EMAIL] = email

                    AUTH.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val uid = AUTH.currentUser?.uid.toString()
                                dateMap[CHILD_ID] = uid
                                REF_DATABASE_ROOT.child(NODE_USERS).child(uid)
                                    .updateChildren(dateMap)
                                    .addOnCompleteListener { task2 ->
                                        if (task2.isSuccessful) {
                                            AUTH.signOut()
                                            replaceFragment(LoginFragment(), false)
                                            showToast("You were registered successfully")
                                        } else showToast(task2.exception!!.message.toString())
                                    }

                            } else {
                                showToast(task.exception!!.message.toString())
                            }
                        }
            }
            }
        }
    }
}
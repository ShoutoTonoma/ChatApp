package com.example.chatapp.ui.fragments.register

import com.example.chatapp.R
import com.example.chatapp.databinding.FragmentRegisterBinding
import com.example.chatapp.ui.fragments.base.BaseRegisterFragment
import com.example.chatapp.utilits.APP_ACTIVITY
import com.example.chatapp.database.AUTH
import com.example.chatapp.database.CHILD_EMAIL
import com.example.chatapp.database.CHILD_FULLNAME
import com.example.chatapp.database.CHILD_ID
import com.example.chatapp.database.CHILD_PHONE
import com.example.chatapp.database.NODE_PHONES
import com.example.chatapp.database.NODE_USERS
import com.example.chatapp.database.REF_DATABASE_ROOT
import com.example.chatapp.utilits.checkEmptyText
import com.example.chatapp.utilits.replaceFragment
import com.example.chatapp.utilits.showToast


class RegisterFragment : BaseRegisterFragment<FragmentRegisterBinding>(
    FragmentRegisterBinding::inflate
) {

    override fun onStart() {
        super.onStart()
        APP_ACTIVITY.mBinding.tvActionBar.text =
            getString(R.string.toolbar_registration)
        binding.signInLink.setOnClickListener {
            replaceFragment(LoginFragment())
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
                }

                else -> {
                    val name: String = binding.nameInput.text.toString().trim { it <= ' ' }
                    val email: String = binding.emailInput.text.toString().trim { it <= ' ' }
                    val phone: String = binding.phoneNumberInput.text.toString().trim { it <= ' ' }
                    val password: String = binding.passwordInput.text.toString().trim { it <= ' ' }
                    val dateMap = mutableMapOf<String, Any>()

                    dateMap[CHILD_FULLNAME] = name
                    dateMap[CHILD_EMAIL] = email
                    dateMap[CHILD_PHONE] = phone


                    AUTH.createUserWithEmailAndPassword(email, password)
                        .addOnSuccessListener {
                            val uid = AUTH.currentUser?.uid.toString()
                            dateMap[CHILD_ID] = uid
                            REF_DATABASE_ROOT.child(NODE_USERS).child(uid)
                                .updateChildren(dateMap)
//                                val uid = AUTH.currentUser?.uid.toString()
                            dateMap[CHILD_ID] = uid

                            REF_DATABASE_ROOT.child(NODE_PHONES).child(phone).setValue(uid)
                                .addOnFailureListener { showToast(it.message.toString()) }
                                .addOnSuccessListener {
                                    REF_DATABASE_ROOT.child(NODE_USERS).child(uid)
                                        .updateChildren(dateMap)
                                        .addOnSuccessListener {
                                            AUTH.signOut()
                                            replaceFragment(LoginFragment())
                                            showToast("You were registered successfully")
                                        }
                                }
                                .addOnFailureListener { showToast(it.message.toString()) }
                        }.addOnFailureListener { showToast(it.message.toString()) }
                }
            }
        }
    }
}


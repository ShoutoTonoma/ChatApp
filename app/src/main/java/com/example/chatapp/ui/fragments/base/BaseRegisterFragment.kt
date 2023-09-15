package com.example.chatapp.ui.fragments.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.example.chatapp.utilits.APP_ACTIVITY

typealias InflateRegisterFragment<T> = (LayoutInflater, ViewGroup?, Boolean) -> T

abstract class BaseRegisterFragment<VB: ViewBinding>(
    private val inflate: InflateRegisterFragment<VB>
): Fragment() {

    private var mBinding: VB? = null

    val binding get() = mBinding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = inflate.invoke(inflater, container, false)
        return binding.root

    }

    override fun onStart() {
        super.onStart()
        APP_ACTIVITY.mBinding.bottomNav.visibility = View.GONE
    }


    override fun onDestroy() {
        super.onDestroy()
        mBinding = null
    }

}
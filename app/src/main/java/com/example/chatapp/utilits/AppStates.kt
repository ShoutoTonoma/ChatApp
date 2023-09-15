package com.example.chatapp.utilits

import com.example.chatapp.database.AUTH
import com.example.chatapp.database.CHILD_STATUS
import com.example.chatapp.database.CURRENT_UID
import com.example.chatapp.database.NODE_USERS
import com.example.chatapp.database.REF_DATABASE_ROOT
import com.example.chatapp.database.USER

enum class AppStates(val status: String) {
    ONLINE("online"),
    OFFLINE("last seen recently"),
    TYPING("typing");

    companion object {
        fun updateStates(appStates: AppStates) {
            if(AUTH.currentUser != null) {
                REF_DATABASE_ROOT.child(NODE_USERS).child(CURRENT_UID).child(CHILD_STATUS)
                    .setValue(appStates.status)
                    .addOnSuccessListener { USER.status = appStates.status }
                    .addOnFailureListener { showToast(it.message.toString()) }
            }
        }
    }
}
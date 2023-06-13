package com.example.chatapp.utilits

enum class AppStates(val status: String) {
    ONLINE("в сети"),
    OFFLINE("был недавно"),
    TYPING("печатает");

    companion object {
        fun updateStates(appStates: AppStates) {
            REF_DATABASE_ROOT.child(NODE_USERS).child(CURRENT_UID).child(CHILD_STATUS)
                .setValue(appStates.status)
                .addOnSuccessListener { USER.status = appStates.status }
                .addOnFailureListener { showToast(it.message.toString()) }
        }
    }
}
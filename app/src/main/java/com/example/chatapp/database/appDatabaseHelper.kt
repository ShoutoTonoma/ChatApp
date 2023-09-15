package com.example.chatapp.database

import android.net.Uri
import android.util.Log
import com.example.chatapp.R
import com.example.chatapp.models.CommonModel
import com.example.chatapp.models.UserModel
import com.example.chatapp.ui.fragments.SettingsFragment
import com.example.chatapp.utilits.APP_ACTIVITY
import com.example.chatapp.utilits.AppValueEventListener
import com.example.chatapp.utilits.TYPE_MESSAGE_IMAGE
import com.example.chatapp.utilits.replaceFragment
import com.example.chatapp.utilits.showToast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

lateinit var AUTH: FirebaseAuth
lateinit var CURRENT_UID: String
lateinit var REF_DATABASE_ROOT: DatabaseReference
lateinit var REF_STORAGE_ROOT: StorageReference
lateinit var USER: UserModel

const val TYPE_TEXT = "text"

const val NODE_USERS = "users"
const val NODE_MESSAGES = "messages"
const val NODE_PHONES = "phones"
const val NODE_PHONES_CONTACTS = "phones_contacts"
const val FOLDER_PROFILE_IMAGE = "profile_image"
const val FOLDER_MESSAGE_IMAGE = "message_image"

const val CHILD_ID = "id"

const val CHILD_EMAIL = "username"
const val CHILD_FULLNAME = "fullname"
const val CHILD_PHONE = "phone"
const val CHILD_REGION = "region"
const val CHILD_STATUS = "status"
const val CHILD_BIO = "bio"
const val CHILD_PHOTO_URL = "photoUrl"
const val CHILD_TEXT = "text"
const val CHILD_FROM = "from"
const val CHILD_TYPE = "type"
const val CHILD_TIMESTAMP = "timeStamp"
const val CHILD_IMAGE_URL = "imageUrl"




fun initFirebase() {
    AUTH = FirebaseAuth.getInstance()
    REF_DATABASE_ROOT = FirebaseDatabase.getInstance().reference
    USER = UserModel()
    CURRENT_UID = AUTH.currentUser?.uid.toString()
    REF_STORAGE_ROOT = FirebaseStorage.getInstance().reference
}

inline fun initUser(crossinline function: () -> Unit) {
    REF_DATABASE_ROOT.child(NODE_USERS).child(CURRENT_UID)
        .addListenerForSingleValueEvent(AppValueEventListener {
            USER = it.getValue(UserModel::class.java) ?: UserModel()
            if(USER.fullname.isEmpty()) {
                USER.fullname = CURRENT_UID
            }
            function()
        })
}

inline fun putUrlToDatabase(url: String, crossinline function: () -> Unit) {
    REF_DATABASE_ROOT.child(NODE_USERS).child(CURRENT_UID)
        .child(CHILD_PHOTO_URL).setValue(url)
        .addOnSuccessListener { function() }
        .addOnFailureListener { showToast(it.message.toString()) }
}

inline fun getUrlFromStorage(path: StorageReference, crossinline function: (url: String) -> Unit) {
    path.downloadUrl
        .addOnSuccessListener { function(it.toString()) }
        .addOnFailureListener { showToast(it.message.toString()) }
}

inline fun putImageToStorage(uri: Uri, path: StorageReference, crossinline function: () -> Unit) {
    path.putFile(uri)
        .addOnSuccessListener { function() }
        .addOnFailureListener { showToast(it.message.toString()) }

}

fun updatesPhonesToDatabase(arrayContacts: ArrayList<CommonModel>) {
    if(AUTH.currentUser != null) {
        REF_DATABASE_ROOT.child(NODE_PHONES).addListenerForSingleValueEvent(AppValueEventListener {
            it.children.forEach { snapshot ->
                arrayContacts.forEach { contact ->
                    if (snapshot.key == contact.phone) {
                        REF_DATABASE_ROOT.child(NODE_PHONES_CONTACTS).child(CURRENT_UID)
                            .child(snapshot.value.toString()).child(CHILD_ID)
                            .setValue(snapshot.value.toString())
                            .addOnFailureListener{ showToast(it.message.toString()) }

                        REF_DATABASE_ROOT.child(NODE_PHONES_CONTACTS).child(CURRENT_UID)
                            .child(snapshot.value.toString()).child(CHILD_FULLNAME)
                            .setValue(contact.fullname)
                            .addOnFailureListener{ showToast(it.message.toString()) }
                    }
                }
            }
        })
    }
}

fun sendMessage(message: String, receivingUserID: String, typeText: String, function: () -> Unit) {
    val refDialogUser = "$NODE_MESSAGES/$CURRENT_UID/$receivingUserID"
    Log.d("myLog", "refDialogUser - $refDialogUser")
    val refDialogReceivingUser = "$NODE_MESSAGES/$receivingUserID/$CURRENT_UID"
    Log.d("myLog", "refDialogReceiverUser - $refDialogReceivingUser")
    val messageKey = REF_DATABASE_ROOT.child(refDialogUser).push().key
    Log.d("myLog", "messageKey - $messageKey")

    val mapMessage = hashMapOf<String, Any>()
    mapMessage[CHILD_FROM] = CURRENT_UID
    mapMessage[CHILD_TYPE] = typeText
    mapMessage[CHILD_TEXT] = message
    mapMessage[CHILD_ID] = messageKey.toString()
    mapMessage[CHILD_TIMESTAMP] = ServerValue.TIMESTAMP

    val mapDialog = hashMapOf<String, Any>()
    mapDialog["$refDialogUser/$messageKey"] = mapMessage
    Log.d("myLog", "refDialogUser/messageKey - $$refDialogUser/$messageKey")
    mapDialog["$refDialogReceivingUser/$messageKey"] = mapMessage
    Log.d("myLog", "refDialogReceivingUser/messageKey - $$refDialogReceivingUser/$messageKey")

    REF_DATABASE_ROOT
        .updateChildren(mapDialog)
        .addOnSuccessListener { function() }
        .addOnFailureListener { showToast(it.message.toString()) }
}

fun sendMessageAsImage(receivingUserID: String, imageUrl: String, messageKey: String) {
    val refDialogUser = "$NODE_MESSAGES/$CURRENT_UID/$receivingUserID"
    val refDialogReceivingUser = "$NODE_MESSAGES/$receivingUserID/$CURRENT_UID"

    val mapMessage = hashMapOf<String, Any>()
    mapMessage[CHILD_FROM] = CURRENT_UID
    mapMessage[CHILD_TYPE] = TYPE_MESSAGE_IMAGE
    mapMessage[CHILD_ID] = messageKey
    mapMessage[CHILD_TIMESTAMP] = ServerValue.TIMESTAMP
    mapMessage[CHILD_IMAGE_URL] = imageUrl

    val mapDialog = hashMapOf<String, Any>()
    mapDialog["$refDialogUser/$messageKey"] = mapMessage
    mapDialog["$refDialogReceivingUser/$messageKey"] = mapMessage

    REF_DATABASE_ROOT
        .updateChildren(mapDialog)
        .addOnFailureListener { showToast(it.message.toString()) }
}

fun setValuesToDatabase(fullName: String, region: String) {
    if (fullName.isEmpty() && region.isEmpty()) {
        showToast("Заполните все данные")
    } else {
        REF_DATABASE_ROOT.child(NODE_USERS).child(CURRENT_UID).child(CHILD_FULLNAME)
            .setValue(fullName)
            .addOnSuccessListener {
                USER.fullname = fullName
                showToast(APP_ACTIVITY.getString(R.string.toast_data_update))
                replaceFragment(SettingsFragment())
            }.addOnFailureListener { showToast(it.message.toString()) }

        REF_DATABASE_ROOT.child(NODE_USERS).child(CURRENT_UID).child(CHILD_REGION)
            .setValue(region)
            .addOnSuccessListener {
                USER.region = region
            }.addOnFailureListener { showToast(it.message.toString()) }
    }
}

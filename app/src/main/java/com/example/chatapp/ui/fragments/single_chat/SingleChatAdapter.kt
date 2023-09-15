package com.example.chatapp.ui.fragments.single_chat

import android.app.AlertDialog
import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapp.R
import com.example.chatapp.models.CommonModel
import com.example.chatapp.database.CURRENT_UID
import com.example.chatapp.utilits.APP_ACTIVITY
import com.example.chatapp.utilits.TYPE_MESSAGE_IMAGE
import com.example.chatapp.utilits.TYPE_MESSAGE_TEXT
import com.example.chatapp.utilits.asTime
import com.example.chatapp.utilits.downloadAndSetImage

class SingleChatAdapter: RecyclerView.Adapter<SingleChatAdapter.SingleChatHolder>() {

    private var mListMessageCache = mutableListOf<CommonModel>()

    class SingleChatHolder(view: View): RecyclerView.ViewHolder(view)  {
        // Text
        val blockUserMessage: ConstraintLayout = view.findViewById(R.id.block_user_message)
        val chatUserMessage: TextView = view.findViewById(R.id.chat_user_message)
        val chatUserMessageTime: TextView = view.findViewById(R.id.chat_user_message_time)

        val blockReceiverMessage: ConstraintLayout = view.findViewById(R.id.block_received_message)
        val chatReceiverMessage: TextView = view.findViewById(R.id.chat_received_message)
        val chatReceiverMessageTime: TextView = view.findViewById(R.id.chat_received_message_time)

        // Image
        val blockReceivedImageMessage: ConstraintLayout = view.findViewById(R.id.block_received_image_message)
        val blockUserImageMessage: ConstraintLayout = view.findViewById(R.id.block_user_image_message)
        val chatUserImage: ImageView = view.findViewById(R.id.chat_user_image)
        val chatReceivedImage: ImageView = view.findViewById(R.id.chat_received_image)
        val chatUserImageMessageTime: TextView = view.findViewById(R.id.chat_user_image_message_time)
        val chatReceivedImageMessageTime: TextView = view.findViewById(R.id.chat_received_image_message_time)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SingleChatHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.message_item, parent, false)
        return SingleChatHolder(view)
    }

    override fun getItemCount(): Int = mListMessageCache.size

    override fun onBindViewHolder(holder: SingleChatHolder, position: Int) {
        when(mListMessageCache[position].type) {
            TYPE_MESSAGE_TEXT -> drawMessageText(holder, position)
            TYPE_MESSAGE_IMAGE -> drawMessageImage(holder, position)
        }
    }



    private fun drawMessageText(holder: SingleChatHolder, position: Int) {
        holder.blockReceivedImageMessage.visibility = View.GONE
        holder.blockUserImageMessage.visibility = View.GONE
        if(mListMessageCache[position].from == CURRENT_UID) {
            holder.blockUserMessage.visibility = View.VISIBLE
            holder.blockReceiverMessage.visibility = View.GONE
            holder.chatUserMessage.text = mListMessageCache[position].text
            holder.chatUserMessageTime.text = mListMessageCache[position].timeStamp.toString().asTime()
        } else {
            holder.blockUserMessage.visibility = View.GONE
            holder.blockReceiverMessage.visibility = View.VISIBLE
            holder.chatReceiverMessage.text = mListMessageCache[position].text
            holder.chatReceiverMessageTime.text = mListMessageCache[position].timeStamp.toString().asTime()
        }
    }

    private fun drawMessageImage(holder: SingleChatHolder, position: Int) {
        holder.blockUserMessage.visibility = View.GONE
        holder.blockReceiverMessage.visibility = View.GONE
        if(mListMessageCache[position].from == CURRENT_UID) {
            holder.blockReceivedImageMessage.visibility = View.GONE
            holder.blockUserImageMessage.visibility = View.VISIBLE
            holder.chatUserImage.downloadAndSetImage(mListMessageCache[position].imageUrl)
            holder.chatUserImageMessageTime.text =
                mListMessageCache[position].timeStamp.toString().asTime()
        } else {
            holder.blockReceivedImageMessage.visibility = View.VISIBLE
            holder.blockUserImageMessage.visibility = View.GONE
            holder.chatReceivedImage.downloadAndSetImage(mListMessageCache[position].imageUrl)
            holder.chatReceivedImageMessageTime.text =
                mListMessageCache[position].timeStamp.toString().asTime()
        }

        holder.itemView.setOnClickListener {
            showFullImageMessage(mListMessageCache[position].imageUrl)
        }
    }

    private fun showFullImageMessage(chatUserImage: String) {
        val image: ImageView = ImageView(APP_ACTIVITY)
        image.downloadAndSetImage(chatUserImage)
        image.layoutParams?.width = 100
        image.layoutParams?.height = 100
        image.requestLayout()

        val alertDialog = AlertDialog.Builder(APP_ACTIVITY)
            .setView(image)
            .create()
        alertDialog.show()
    }

    fun addItemToBottom(
        item: CommonModel,
        onSuccess: () -> Unit
    ) {
        if (!mListMessageCache.contains(item)) {
            mListMessageCache.add(item)
            notifyItemInserted(mListMessageCache.size)
        }
        onSuccess()
    }

    fun addItemToTop(
        item: CommonModel,
        onSuccess: () -> Unit
    ) {
        if (!mListMessageCache.contains(item)) {
            mListMessageCache.add(item)
            mListMessageCache.sortBy { it.timeStamp.toString() }
            notifyItemInserted(0)
        }
        onSuccess()
    }
}
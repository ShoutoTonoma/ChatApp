package com.example.chatapp.ui.fragments


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapp.R
import com.example.chatapp.databinding.FragmentContactsBinding
import com.example.chatapp.models.CommonModel
import com.example.chatapp.ui.fragments.base.BaseFragment
import com.example.chatapp.ui.fragments.single_chat.SingleChatFragment
import com.example.chatapp.utilits.APP_ACTIVITY
import com.example.chatapp.utilits.AppValueEventListener
import com.example.chatapp.database.CURRENT_UID
import com.example.chatapp.database.NODE_PHONES_CONTACTS
import com.example.chatapp.database.NODE_USERS
import com.example.chatapp.database.REF_DATABASE_ROOT
import com.example.chatapp.utilits.downloadAndSetImage
import com.example.chatapp.utilits.getCommonModel
import com.example.chatapp.utilits.replaceFragment
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.DatabaseReference
import de.hdodenhof.circleimageview.CircleImageView


class ContactsFragment : BaseFragment<FragmentContactsBinding>(
    FragmentContactsBinding::inflate) {

    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mAdapter: FirebaseRecyclerAdapter<CommonModel, ContactsHolder>
    private lateinit var mRefContacts: DatabaseReference
    private lateinit var mRefUsers: DatabaseReference
    private lateinit var mRefUsersListeners: AppValueEventListener
    private var mapListeners = hashMapOf<DatabaseReference, AppValueEventListener>()




    override fun onResume() {
        super.onResume()
        APP_ACTIVITY.mBinding.backArrowLayout.visibility = View.GONE
        APP_ACTIVITY.mBinding.tvActionBar.visibility = View.VISIBLE
        APP_ACTIVITY.mBinding.tvActionBar.text = "Contacts"
        APP_ACTIVITY.mBinding.bottomNav.visibility = View.VISIBLE
        initRecyclerView()
    }

    private fun initRecyclerView() {
        mRecyclerView = binding.contactsRecycleView
        mRefContacts = REF_DATABASE_ROOT.child(NODE_PHONES_CONTACTS).child(CURRENT_UID)


        val options = FirebaseRecyclerOptions.Builder<CommonModel>()
            .setQuery(mRefContacts, CommonModel::class.java)
            .build()


        mAdapter = object: FirebaseRecyclerAdapter<CommonModel, ContactsHolder>(options) {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactsHolder {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.contact_item, parent, false)
                return ContactsHolder(view)
            }

            override fun onBindViewHolder(
                holder: ContactsHolder,
                position: Int,
                model: CommonModel
            ) {
                mRefUsers = REF_DATABASE_ROOT.child(NODE_USERS).child(model.id)

                mRefUsersListeners = AppValueEventListener {
                    val contact = it.getCommonModel()

                    if(contact.fullname.isEmpty()) {
                        holder.name.text = model.fullname
                    } else holder.name.text = contact.fullname

                    holder.status.text = contact.status
                    holder.photo.downloadAndSetImage(contact.photoUrl)
                    holder.itemView.setOnClickListener { replaceFragment(
                        SingleChatFragment(model), true
                    ) }
                }

                mRefUsers.addValueEventListener(mRefUsersListeners)
                mapListeners[mRefUsers] = mRefUsersListeners
            }
        }

        mRecyclerView.adapter = mAdapter
        mAdapter.startListening()
    }

    class ContactsHolder(view: View): RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.contact_fullname)
        val status: TextView = view.findViewById(R.id.contact_status)
        val photo: CircleImageView = view.findViewById(R.id.contact_photo)
    }

    override fun onPause() {
        super.onPause()
        mAdapter.stopListening()
        mapListeners.forEach {
            it.key.removeEventListener(it.value)
        }
    }
}
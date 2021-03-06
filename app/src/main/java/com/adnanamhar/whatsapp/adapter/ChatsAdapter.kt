package com.adnanamhar.whatsapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.adnanamhar.whatsapp.R
import com.adnanamhar.whatsapp.listener.ChatsClickListener
import com.adnanamhar.whatsapp.util.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_chats.*

class ChatsAdapter(val chats: ArrayList<String>):
    RecyclerView.Adapter<ChatsAdapter.ChatsViewHolder>() {

    private var chatsClickListener: ChatsClickListener? = null

    class ChatsViewHolder(override val containerView: View) :
        RecyclerView.ViewHolder(containerView), LayoutContainer {

        private val firebaseDb = FirebaseFirestore.getInstance()
        private val userId = FirebaseAuth.getInstance().currentUser?.uid
        private var partnerId: String? = null
        private var chatName: String? = null
        private var chatImage: String? = null

        fun bindItem(chatId: String, listener: ChatsClickListener?) {
            progress_layout_chats.visibility = View.VISIBLE
            progress_layout_chats.setOnTouchListener { v, event -> true }

            firebaseDb.collection(DATA_CHATS)
                .document(chatId)
                .get()
                .addOnSuccessListener {
                    val chatParticipants = it[DATA_CHATS_PARTICIPANTS]
                    if (chatParticipants != null) {
                        for (participant in chatParticipants as ArrayList<String>) {
                            if (participant != null && !participant.equals(userId)) {
                                partnerId = participant
                                firebaseDb.collection(DATA_USERS).document(partnerId!!).get()
                                    .addOnSuccessListener {
                                        val user = it.toObject(User::class.java)
                                        chatImage = user?.imageUrl
                                        chatName = user?.name
                                        txt_chats.text = user?.name
                                        populateImage(
                                            img_chats.context,
                                            user?.imageUrl,
                                            img_chats,
                                            R.drawable.ic_user
                                        )
                                        progress_layout_chats.visibility = View.GONE
                                    }
                                    .addOnFailureListener {
                                        it.printStackTrace()
                                        progress_layout_chats.visibility = View.GONE
                                    }
                            }
                        }
                    }

                    progress_layout_chats.visibility = View.GONE
                }
                .addOnFailureListener {
                    it.printStackTrace()
                    progress_layout_chats.visibility = View.GONE
                }
            itemView.setOnClickListener {
                listener?.onChatClicked(chatId, userId, chatImage, chatName)
            }

        }

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ChatsViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_chats, parent, false)
    )

    override fun getItemCount() = chats.size

    override fun onBindViewHolder(holder: ChatsViewHolder, position: Int) {
        holder.bindItem(chats[position], chatsClickListener)
    }

    fun setOnItemClickListener(listener: ChatsClickListener) {
        chatsClickListener = listener
        notifyDataSetChanged()
    }

    fun UpdateChats(updateChats: ArrayList<String>) {
        chats.clear()
        chats.addAll(updateChats)
        notifyDataSetChanged()
    }
}
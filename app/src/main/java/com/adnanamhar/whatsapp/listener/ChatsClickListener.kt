package com.adnanamhar.whatsapp.listener

interface ChatsClickListener {
    fun onChatClicked(
        chatId: String?,
        otheruserId: String?,
        chatsImageUrl: String?,
        chatsName: String?
    )
}
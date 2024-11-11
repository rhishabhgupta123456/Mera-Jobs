package tech.merajobs.dataModel

data class ChatMessage(
    val data: ArrayList<ChatList>,
)

data class ChatList(
    val id: Int,
    val sender_id: Int,
    val channel_id: Int,
    val message: String?,
    val attachment: String?,
    val read_status: Int,
    val from_name: String,
    val to_name: String,
    var deleteShow : Int = 0
)

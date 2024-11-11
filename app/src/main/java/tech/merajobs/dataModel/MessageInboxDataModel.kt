package tech.merajobs.dataModel

data class MessageInboxDataModel(
    var data: ArrayList<MessageInboxList>,
)

data class MessageInboxList(
    val id: String,
    val to_id: String,
    val to_name: String?,
    val to_photo: String?,
    val last_message: String?,
    val to_online_status: Int,
    val last_message_seen_status: Int,
    val last_time_online: String,
    var isSelected: Boolean = false,
)


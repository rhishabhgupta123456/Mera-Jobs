package tech.merajobs.dataModel

data class OnlinePresenceDataModel(
    val success: String,
    val data: ArrayList<OnlinePresenceList>? = null,
)

data class OnlinePresenceList(
    val id : String,
    val account_type_label : String,
    val account_type : String,
    val url : String,
    val description : String,
)

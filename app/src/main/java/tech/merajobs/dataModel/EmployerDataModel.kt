package tech.merajobs.dataModel

data class EmployerDataModel (
    val success: String,
    val data: ArrayList<EmployerList>? = null,
)

data class EmployerList(
    val id : String,
    val photo : String?,
    val name : String?,
    val mobile : String?,
    val whatsapp_notification_enabled : String?,
    val email : String?,
    val email_verified : String?,
    val mobile_verified : String?,
    val country_id : String?,
    val linkedin_profile : String?,
    val company_name : String?,
    val company_logo : String?,
    val company_website : String?,
    var is_blocked : Int,
)
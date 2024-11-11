package tech.merajobs.dataModel

data class BlockCompanyList(
    val success: String,
    val data: ArrayList<BlockList>? = null,
)

data class BlockList(
    val id : String,
    val company_id : String,
    val job_seeker_id : String,
    val company_name : String? = null,
    val company_logo : String? = null,
    val company_website : String? = null,
)

package tech.merajobs.dataModel

data class BlockedCompanyDataModel(
    val id : Int,
    val company_name : String?,
    val company_logo : String?,
    val company_address : String?,
    val blocked : Boolean,
)

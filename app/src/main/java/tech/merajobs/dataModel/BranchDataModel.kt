package tech.merajobs.dataModel

data class BranchDataModel(
    val data : ArrayList<BranchList>?,
 )

data class BranchList(
    val id: String,
    val branch_name: String,
    val zip: String,
    val address: String,
    val city_name: String,
    val state_name: String,
    val country_name: String,
)
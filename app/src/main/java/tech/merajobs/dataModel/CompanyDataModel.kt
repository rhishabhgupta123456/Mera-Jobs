package tech.merajobs.dataModel

data class CompanyDataModel(
    var data: ArrayList<CompanyList>,
)

data class CompanyList(
    val id: String,
    val name: String,
    val company_name: String ="",
    val company_logo: String ="",
    val job_count: String ="",
)
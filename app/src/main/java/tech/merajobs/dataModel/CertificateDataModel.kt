package tech.merajobs.dataModel

data class CertificateDataModel(
    val success: String,
    val data: ArrayList<CertificateList>? = null,
)

data class CertificateList(
    val id : String,
    val certificate : String,
    val institute : String,
    val valid_till : String,
    val certificate_no : String,
    val job_seeker_id : String,
)

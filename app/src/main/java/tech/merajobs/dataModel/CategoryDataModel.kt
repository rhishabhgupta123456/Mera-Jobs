package tech.merajobs.dataModel

data class CategoryDataModel(
    val data : ArrayList<CategoryList>,
 )

data class CategoryList(
    val categoryImage : String? = null,
    val role_id : String,
    val role : String,
    val job_count : String
)
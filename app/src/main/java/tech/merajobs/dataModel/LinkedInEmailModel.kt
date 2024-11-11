package tech.merajobs.dataModel



data class LinkedInEmailModel(
    val elements: List<ElementEmail>
)

data class ElementEmail(
    val elementHandle: Handle,
    val handle: String
)

data class Handle(
    val emailAddress: String
)
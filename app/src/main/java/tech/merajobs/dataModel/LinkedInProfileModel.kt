package tech.merajobs.dataModel


data class LinkedInProfileModel (
    val firstName: StName,
    val lastName: StName,
    val profilePicture: ProfilePicture,
    val id: String
)


data class StName (
    val localized: Localized
)

data class Localized (
    val en_US: String
)
data class ProfilePicture (

    val displayImage : DisplayImage
)
data class DisplayImage (
    val elements: List<Element>
)

data class Element (
    val identifiers: List<Identifier>
)

data class Identifier (
    val identifier: String
)
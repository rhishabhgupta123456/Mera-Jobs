package tech.merajobs.utility

import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.util.UUID

object LinkedInConstants {
    val CLIENT_ID = "77yb6y3ffhtx4t"
    val CLIENT_SECRET = "6F8FlOIy1Z3HNwIF"
    val REDIRECT_URI = "https://merajobs.tech/social-login/callback"
    val SCOPE = "email"
    val AUTHURL = "https://www.linkedin.com/oauth/v2/authorization"
    val TOKENURL = "https://www.linkedin.com/oauth/v2/accessToken"
    val state = "r_liteprofile r_emailaddress"

    fun buildLinkedInAuthUrl(): String {
        val authUrl = AUTHURL
        val clientId = CLIENT_ID
        val scope = URLEncoder.encode(SCOPE, StandardCharsets.UTF_8.toString())
        val redirectUri = URLEncoder.encode(REDIRECT_URI, StandardCharsets.UTF_8.toString())

        return buildString {
            append(authUrl)
            append("?response_type=code")
            append("&client_id=").append(clientId)
            append("&scope=").append(scope)
            append("&state=").append(state)
            append("&redirect_uri=").append(redirectUri)
        }
    }



    data class LinkedInProfileModel(
        val id: String,
        val firstName: Name,
        val lastName: Name,
        val profilePicture: ProfilePicture
    )

    data class Name(
        val localized: Map<String, String>
    )

    data class ProfilePicture(
        val displayImage: DisplayImage
    )

    data class DisplayImage(
        val elements: List<Element>
    )

    data class Element(
        val identifiers: List<Identifier>
    )

    data class Identifier(
        val identifier: String
    )

    data class LinkedInEmailModel(
        val elements: List<ElementHandle>
    )

    data class ElementHandle(
        val handle: Handle
    )

    data class Handle(
        val emailAddress: String
    )
}
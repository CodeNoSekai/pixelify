package iiec.ditzdev.pixelify.models

data class Contributor(
    val name: String,
    val role: String,
    val avatarUrl: String? = null,
    val githubUrl: String? = null
)
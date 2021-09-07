package nosorae.changed_name.p20_git.data.response

data class GithubAccessTokenResponse(
    val accessToken: String,
    val scope: String,
    val tokenType: String
)
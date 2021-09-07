package nosorae.changed_name.p20_git.data.response

import nosorae.changed_name.p20_git.data.entity.GithubRepository

data class GithubRepoSearchResponse(
    val totalCount: Int,
    val items: List<GithubRepository>
)

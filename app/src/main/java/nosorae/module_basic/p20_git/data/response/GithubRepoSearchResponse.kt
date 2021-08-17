package nosorae.module_basic.p20_git.data.response

import nosorae.module_basic.p20_git.data.entity.GithubRepository

data class GithubRepoSearchResponse(
    val totalCount: Int,
    val items: List<GithubRepository>
)

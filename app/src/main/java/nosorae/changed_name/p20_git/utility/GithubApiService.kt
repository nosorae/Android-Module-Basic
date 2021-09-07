package nosorae.changed_name.p20_git.utility

import nosorae.changed_name.p20_git.data.entity.GithubRepository
import nosorae.changed_name.p20_git.data.response.GithubRepoSearchResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface GithubApiService {

    @GET("search/repositories")
    suspend fun searchRepositories(@Query("q") query: String): Response<GithubRepoSearchResponse>

    @GET("repos/{owner}/{name}")
    suspend fun getRepository(
        @Path("owner") ownerLogin: String,
        @Path("name") repoName: String
    ): Response<GithubRepository>
}
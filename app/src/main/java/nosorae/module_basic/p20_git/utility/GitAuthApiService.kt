package nosorae.module_basic.p20_git.utility

import nosorae.module_basic.p20_git.data.response.GithubAccessTokenResponse
import nosorae.module_basic.p20_git.data.response.GithubRepoSearchResponse
import retrofit2.Response
import retrofit2.http.*

interface GitAuthApiService {

    @FormUrlEncoded
    @POST("login/oauth/access_token")
    @Headers("Accept: application/json")
    suspend fun getAccessToken(
        @Field("client_id") clientId: String,
        @Field("client_secret") clientSecret: String,
        @Field("code") code: String
    ): Response<GithubAccessTokenResponse>


    @GET("search/repositories")
    suspend fun searchRepositories(@Query("q") query: String): Response<GithubRepoSearchResponse>

}
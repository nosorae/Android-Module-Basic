package nosorae.changed_name.p20_git.utility

import nosorae.changed_name.p20_git.data.response.GithubAccessTokenResponse
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




}
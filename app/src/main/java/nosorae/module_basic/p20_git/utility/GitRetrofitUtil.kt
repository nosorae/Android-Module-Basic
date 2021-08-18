package nosorae.module_basic.p20_git.utility

import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import nosorae.module_basic.BuildConfig
import nosorae.module_basic.p20_git.data.GitUrl
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object GitRetrofitUtil {

    val authApiService: GitAuthApiService by lazy {
        getGithubAuthRetrofit().create(GitAuthApiService::class.java)
    }

    private fun getGithubAuthRetrofit(): Retrofit =
        Retrofit.Builder()
            .baseUrl(GitUrl.GITHUB_URL)
            .addConverterFactory(
                GsonConverterFactory.create(
                    GsonBuilder()
                        .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                        .create()
                )
            )
            .client(buildOkHttpClient())
            .build()

    val githubApiService: GithubApiService by lazy { getGithubApiService().create(GithubApiService::class.java) }

    private fun getGithubApiService(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(GitUrl.GITHUB_API_URL)
            .addConverterFactory(
                GsonConverterFactory.create(
                    GsonBuilder()
                        .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                        .create()
                )
            )
            .client(buildOkHttpClient())
            .build()
    }




    private fun buildOkHttpClient(): OkHttpClient {
        val interceptor = HttpLoggingInterceptor()
        if (BuildConfig.DEBUG) {
            interceptor.level = HttpLoggingInterceptor.Level.BODY
        } else {
            interceptor.level = HttpLoggingInterceptor.Level.NONE
        }
        return OkHttpClient.Builder()
            .connectTimeout(5, TimeUnit.SECONDS)
            .addInterceptor(interceptor)
            .build()
    }

}
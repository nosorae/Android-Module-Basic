package nosorae.module_basic.p20_git

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.exoplayer2.database.DatabaseProvider
import kotlinx.coroutines.*
import nosorae.module_basic.databinding.ActivityGitBinding
import nosorae.module_basic.p20_git.data.GitDatabaseProvider
import nosorae.module_basic.p20_git.data.entity.GithubOwner
import nosorae.module_basic.p20_git.data.entity.GithubRepository
import nosorae.module_basic.p20_git.utility.GitRetrofitUtil
import java.util.*
import kotlin.coroutines.CoroutineContext

class GitActivity: AppCompatActivity(), CoroutineScope {
    private lateinit var binding: ActivityGitBinding
    private val job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    private val repositoryDao by lazy {
        GitDatabaseProvider.provideDB(applicationContext).repositoryDao()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGitBinding.inflate(layoutInflater)
        setContentView(binding.root)

        launch {
            val dataJob = addMockData()
            val githubRepositories = loadGithubRepository()
            withContext(coroutineContext) {
                Log.e("repositories",  githubRepositories.toString())
            }
        }
    }

    private fun initViews() = with(binding) {
        gitFloatActionButtonSearch.setOnClickListener {
            startActivity(
                Intent(this@GitActivity, GitSearchActivity::class.java)
            )

        }

    }

    private suspend fun addMockData() = withContext(Dispatchers.IO) {
        val mockData = (0 until 10).map {
            GithubRepository(
                name = "repo $it",
                fullName = "name $it",
                owner = GithubOwner(
                    "login",
                    "avartarUrl"
                ),
                description = null,
                language = null,
                updatedAt = Date().toString(),
                stargazersCount = it
            )
        }
        repositoryDao.insertAll(mockData)
    }
    private suspend fun loadGithubRepository() = withContext(Dispatchers.IO) {
        val repositories = repositoryDao.getHistory()
        return@withContext repositories
    }

    private fun searchKeyword(keywordString: String) {
        launch(coroutineContext) {
            try {
                withContext(Dispatchers.IO) {
                    val response = GitRetrofitUtil.authApiService.searchRepositories(
                        query = keywordString
                    )
                    if (response.isSuccessful) {
                        val body = response.body()
                        withContext(Dispatchers.Main) {
                            Log.e("response", body.toString())
                            body?.let { searchResponse ->
                                //setData(searchResponse.items)
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this@GitActivity, "검색하는 과정에서 에러가 발생했습니다.", Toast.LENGTH_SHORT).show()
            }
        }

    }

}
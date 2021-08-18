package nosorae.module_basic.p20_git

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import kotlinx.coroutines.*
import nosorae.module_basic.R
import nosorae.module_basic.databinding.ActivityGitRepositoryBinding
import nosorae.module_basic.p20_git.adatper.GitSearchRecyclerAdapter
import nosorae.module_basic.p20_git.data.GitDatabaseProvider
import nosorae.module_basic.p20_git.data.entity.GithubRepository
import nosorae.module_basic.p20_git.extensions.loadCenterInside
import nosorae.module_basic.p20_git.utility.GitRetrofitUtil
import kotlin.coroutines.CoroutineContext

class GitRepositoryActivity : AppCompatActivity(), CoroutineScope {
    private lateinit var binding: ActivityGitRepositoryBinding
    private val job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job


    private val repositoryDao by lazy {
        GitDatabaseProvider.provideDB(applicationContext).repositoryDao()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGitRepositoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val repositoryOwner = intent.getStringExtra(REPOSITORY_OWNER_KEY) ?: kotlin.run {
            toast("Repository Owner 이름이 없습니다.")
            finish()
            return
        }
        val repositoryName = intent.getStringExtra(NAME_KEY) ?: kotlin.run {
            toast("Repository 이름이 없습니다.")
            finish()
            return
        }

        launch {
            loadRepository(repositoryOwner, repositoryName)?.let {
                setData(it)
            } ?: run {
                toast("Repository 정보가 없습니다.")
            }
        }

        showLoading(true)
    }


    private suspend fun loadRepository(repositoryOwner: String, repositoryName: String): GithubRepository? =
        withContext(coroutineContext) {
            var repositoryEntity: GithubRepository? = null
            withContext(Dispatchers.IO) {
                val response = GitRetrofitUtil.githubApiService.getRepository(
                    ownerLogin = repositoryOwner,
                    repoName = repositoryName
                )

                if (response.isSuccessful) {
                    val body = response.body()
                    withContext(Dispatchers.Main) {
                        Log.e("repository", body.toString())
                        body?.let { repo ->
                            repositoryEntity = repo
                        }
                    }
                }
            }
            repositoryEntity
        }

    private fun setData(githubRepo: GithubRepository) = with(binding) {
        showLoading(false)
        gitRepoImageOwnerProfile.loadCenterInside(githubRepo.owner.avatarUrl, 42f)
        gitRepoTextOwnerNameAndRepoName.text = "${githubRepo.owner.login}/${githubRepo.name}"
        gitRepoTextStargazersCount.text = githubRepo.stargazersCount.toString()
        githubRepo.language?.let {
            gitRepoTextLanguage.isGone = false
            gitRepoTextLanguage.text = it
        } ?: kotlin.run {
            gitRepoTextLanguage.isGone = true
            gitRepoTextLanguage.text = ""
        }
        gitRepoTextDescription.text = githubRepo.description
        gitRepoTextUpdateTime.text = githubRepo.updatedAt

        setLikeState(githubRepo)
    }

    private fun setLikeState(githubRepository: GithubRepository) = launch {
        withContext(Dispatchers.IO) {
            val repository = repositoryDao.getRepository(githubRepository.fullName)
            val isLike = repository != null
            withContext(Dispatchers.Main) {
                setLikeImage(isLike)
                binding.gitRepoButtonLike.setOnClickListener {
                    likeGithubRepo(githubRepository, isLike)
                }

            }
        }
    }

    private fun setLikeImage(isLike: Boolean) {
        binding.gitRepoButtonLike.setImageDrawable(ContextCompat.getDrawable(this,
            if (isLike) {
                R.drawable.ic_favorite
            } else {
                R.drawable.ic_favorite_boarder
            }
        ))
    }

    private fun likeGithubRepo(githubRepository: GithubRepository, isLike: Boolean) = launch {
        withContext(Dispatchers.IO) {
            // 이미 좋아요한 레포면 좋아요에서 삭제하고 아니면 좋아요에 추가
            if (isLike) {
                repositoryDao.remove(githubRepository.fullName)
            } else {
                repositoryDao.insert(githubRepository)
            }
            withContext(Dispatchers.Main) {
                setLikeImage((isLike.not()))
            }

        }


    }

    companion object {
        const val REPOSITORY_OWNER_KEY = "REPOSITORY_OWNER_KEY"
        const val NAME_KEY = "NAME_KEY"
    }

    private fun Context.toast(message: String) =
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

    private fun showLoading(isShow: Boolean) {
        binding.gitRepoProgressBar.isGone = isShow.not()
    }
}
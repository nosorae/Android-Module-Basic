package nosorae.module_basic.p20_git

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isGone
import com.google.android.exoplayer2.database.DatabaseProvider
import kotlinx.coroutines.*
import nosorae.module_basic.databinding.ActivityGitBinding
import nosorae.module_basic.p20_git.adatper.GitSearchRecyclerAdapter
import nosorae.module_basic.p20_git.data.GitDatabaseProvider
import nosorae.module_basic.p20_git.data.entity.GithubOwner
import nosorae.module_basic.p20_git.data.entity.GithubRepository
import nosorae.module_basic.p20_git.utility.GitRetrofitUtil
import java.util.*
import kotlin.coroutines.CoroutineContext

class GitActivity : AppCompatActivity(), CoroutineScope {
    private lateinit var binding: ActivityGitBinding
    private val job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    private val repositoryDao by lazy {
        GitDatabaseProvider.provideDB(applicationContext).repositoryDao()
    }

    private lateinit var adapter: GitSearchRecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGitBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initAdapter()
        initViews()


    }

    private fun initAdapter() {
        adapter = GitSearchRecyclerAdapter { repo ->
            startActivity(
                Intent(
                    this,
                    GitRepositoryActivity::class.java
                ).apply {
                    putExtra(GitRepositoryActivity.REPOSITORY_OWNER_KEY, repo.owner.login)
                    putExtra(GitRepositoryActivity.NAME_KEY, repo.name)
                }
            )
        }



    }

    private fun initViews() = with(binding) {
        binding.gitRecyclerView.adapter = adapter

        gitFloatActionButtonSearch.setOnClickListener {
            startActivity(
                Intent(this@GitActivity, GitSearchActivity::class.java)
            )

        }

    }

    override fun onResume() {
        super.onResume()
        launch(coroutineContext) {
            loadLikedRepositoryList()
        }

    }

    private suspend fun loadLikedRepositoryList() = withContext(Dispatchers.IO) {
        val repoList = repositoryDao.getHistory()
        withContext(Dispatchers.Main) {
            setData(repoList)
        }

    }

    private fun setData(repoList: List<GithubRepository>) = with(binding) {
        if (repoList.isEmpty()) {
            gitTextEmptyResult.isGone = false
            gitRecyclerView.isGone = true
        } else {
            gitTextEmptyResult.isGone = true
            gitRecyclerView.isGone = false
            adapter.submitList(repoList)
        }

    }


}
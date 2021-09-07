package nosorae.changed_name.p20_git

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.view.isGone
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.*
import nosorae.changed_name.databinding.ActivityGitSearchBinding
import nosorae.changed_name.p20_git.adatper.GitSearchRecyclerAdapter
import nosorae.changed_name.p20_git.data.entity.GithubRepository
import nosorae.changed_name.p20_git.utility.GitRetrofitUtil
import kotlin.coroutines.CoroutineContext

class GitSearchActivity : AppCompatActivity(), CoroutineScope {
    private lateinit var binding: ActivityGitSearchBinding


    private lateinit var adapter: GitSearchRecyclerAdapter


    private val job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGitSearchBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initAdapter()
        initViews()
        bindViews()
    }

    private fun initAdapter() = with(binding) {
        adapter = GitSearchRecyclerAdapter {
            startActivity(
                Intent(this@GitSearchActivity, GitRepositoryActivity::class.java).apply {
                    putExtra(GitRepositoryActivity.REPOSITORY_OWNER_KEY, it.owner.login)
                    putExtra(GitRepositoryActivity.NAME_KEY, it.name)
                }
            )
        }
        gitSearchRecyclerView.adapter = adapter
        gitSearchRecyclerView.layoutManager = LinearLayoutManager(this@GitSearchActivity)
    }
    private fun initViews() = with(binding) {
        gitSearchTextEmptyResult.isGone = true
        gitSearchRecyclerView.adapter = adapter

    }

    private fun bindViews() = with(binding) {
        gitSearchButton.setOnClickListener {
            searchKeyword(gitSearchEditText.text.toString())
        }
    }

    private fun searchKeyword(keywordString: String) = launch {
        withContext(Dispatchers.IO) {
            val response = GitRetrofitUtil.githubApiService.searchRepositories(keywordString)
            if (response.isSuccessful) {
                val body = response.body()
                withContext(Dispatchers.Main) {
                    Log.e("response", body.toString())
                    body?.let {
                        setData(items = it.items)
                    }
                }
            }
        }

    }

    private fun setData(items: List<GithubRepository>) {
        adapter.submitList(items)
    }
}
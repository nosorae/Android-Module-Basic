package nosorae.module_basic.p20_git

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import nosorae.module_basic.databinding.ActivityGitSearchBinding
import kotlin.coroutines.CoroutineContext

class GitSearchActivity : AppCompatActivity(), CoroutineScope {
    private lateinit var binding: ActivityGitSearchBinding
    private val job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGitSearchBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    private fun initAdapter() = with(binding) {

    }
    private fun initViews() = with(binding) {

    }

    private fun bindViews() = with(binding) {

    }
}
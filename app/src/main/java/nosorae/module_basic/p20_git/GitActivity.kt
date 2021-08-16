package nosorae.module_basic.p20_git

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.browser.customtabs.CustomTabsIntent
import kotlinx.coroutines.*
import nosorae.module_basic.BuildConfig
import nosorae.module_basic.databinding.ActivityGitBinding
import nosorae.module_basic.p20_git.utility.GitRetrofitUtil
import kotlin.coroutines.CoroutineContext

/**
 * 코루틴
 *      앱 실행에는 최소 하나 이상의 프로세스가 실행되어 프로세스 컨테이너가 존재한다.
 *      그리고 하나의 앱에는 하나의 힙이 할당된다.
 *      하나의 힙 메모리 안쪽에는 흐름들이 있는데 그것이 스레드들이다.
 *      예를 들어 UI 변경을 위해서 UI 스레드가 존재한다.
 *      하드웨어 스펙에 코어 개념이 있는데 하나의 코어에 두개의 스레드를 사용할 수 있게 존재한다.
 *      이 스레드에는 스택이 존재한다.
 *      스레드는 병렬성, 코루틴은 동시성이라는 특징을 가지고 있다.
 *      스레드풀의 개수가 작은데 여러개를 사용하면 블로킹의 문제?
 *      이런 문제를 해결해주는 것이 코루틴의 동시성. 빠르게 전환되어 병렬성처럼 보이게 하여 경량스레드라고 하기도 한다.
 *      스레드는 Task 단위가 스레드이고 자체 Stack 을 가지며, JVM Stack(자바, 코틀린의 경우) 영역을 차지한다.
 *      Context Switching 존재 즉 다른 스레드의 결과를 기다려야하는 스레드가 존재하면 Blocking 되어 사용되지 못하고 있을 수 있다.
 *      코루틴은 Task 단위가 Object(Coroutine) 이다. Coroutine 은 객체를 담는 JVM Heap 에 적재된다.
 *      No Context Switching, 코드를 통해 Switching 시점을 보장할 수 있다.
 *      다른 코루틴의 결과를 기다려야하는 코루틴이 존재하면 Suspend 되긴 하지만 그 코루틴을 수행하던 스레드는 유효하므로
 *      두 코루틴을 동일한 스레드에서 실행할 수 있다.
 *      코루틴의 장점은 코드의 간결성이다. 보통 비동기코드를 만드려고 하면 콜백을 이용하거나, RxJava 의 경우는 스트림을 코드로 구현 했었다.
 *      코루틴은 비동기적인 코드를 동기적으로 볼 수 있게 해준다.
 *
 *      suspend 키워드가 붙은 함수는 '일시중단함수' 라고 부른다.
 *      runBlocking(컨텍스트가 메인스레드일 때 블락킹을 넣게되면 ANR 발생할 수 있는 문제때문에 실제로는 유닛테스트같은 비동기 테스트를 목적으로 사용, 코루틴 끝날 때까지 현재스레드를 차단),
 *      async(결과로 Defered 를 반환, await 으로 꺼내올 수 있음?, 비동기적으로 값을 가져올 수 있다.),
 *      launch(결과 반환 x, 코루틴 실행 취소를 위한 Job 반환)
 *      같은 Coroutine Builder, 람다블록 이용해서 이 스코프에서 다른 일시 중단 함수를 호출
 *      Coroutine Dispatcher 는 코루틴을 시작하거나 재개할 스레드를 결정하기 위한 도구이다. 모든 Dispatcher 는 CoroutineDispatcher 인터페이스를 구현해야한다.
 *
 *
 * github api 사용
 *      Settings - Developer Settings - OAuth Apps - New OAuth App
 *      id, secrets 을 gradle.properties 에 저장
 *      defaultConfig 에 아래 두 개 추가가
*      buildConfigField "String", "GITHUB_CLIENT_ID", project.properties["GITHUB_CLIENT_ID"]
 *      buildConfigField "String", "GITHUB_CLIENT_SECRET", project.properties["GITHUB_CLIENT_SECRET"]
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 */
class GitActivity : AppCompatActivity(), CoroutineScope {
    private lateinit var binding: ActivityGitBinding

    val job: Job = Job()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job // context 로 job 을 추가해주는 것이다. 종료된 이후에도 스레드에서 api 콜을 하고 있는 것을 방지하기 위함?


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGitBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initViews()

    }

    private fun initViews() = with(binding) {
        gitButtonLogin.setOnClickListener {
            loginGithub()
        }
    }
    // todo https://github.com/login/oauth/authorize?client_id=...
    private fun loginGithub() {
        val loginUri = Uri.Builder().scheme("https").authority("github.com")
            .appendPath("login")
            .appendPath("oauth")
            .appendPath("authorize")
            .appendQueryParameter("client_id", BuildConfig.GITHUB_CLIENT_ID)
            .build()

        // 브라우저 라이브러리를 기반으로 불러오기,
        // CustomTabsIntent.Builder().build() 로 인텐트 객체 만들고, launchUrl 함수 호출하면 현재화면에서 커스텀탭으로 이동할 수 있는 인텐트를 실행하게된다.
        // 이거하려고 인텐트 필터 추가하고 그래들에도 의존성 추가한 것이다.?
        CustomTabsIntent.Builder().build().also {
            it.launchUrl(this, loginUri)
        }


    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)

        intent?.data?.getQueryParameter("code")?.let {
            launch(coroutineContext) {
                getAccessToken(it)

            }

        }
    }
    private suspend fun getAccessToken(code: String) = withContext(Dispatchers.IO) {

        val response = GitRetrofitUtil.authApiService.getAccessToken(
            clientId = BuildConfig.GITHUB_CLIENT_ID,
            clientSecret = BuildConfig.GITHUB_CLIENT_SECRET,
            code = code
        )
        if (response.isSuccessful) {
            val accessToken = response.body()?.accessToken ?: "accessToken 이 null 입니다."
            Log.d("accessToken", accessToken )
        } else {
            Log.d("accessToken",  "body 가 null 입니다.")
        }



    }


}
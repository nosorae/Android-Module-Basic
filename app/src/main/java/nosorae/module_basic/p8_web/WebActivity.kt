package nosorae.module_basic.p8_web

import android.annotation.SuppressLint
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.webkit.URLUtil
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import androidx.core.widget.ContentLoadingProgressBar
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import nosorae.module_basic.R

/**
 * WebView 사용
 * -> WebView 에서 loadUrl 할 때 디폴트 웹브라우져 앱이 실행되게 되는데 이를 막고 내 앱 내에서 보여주려면 webView.webViewClient = WebViewClient()
 * -> 로드된 앱 내의 웹페이지의 버튼이 제대로 동작하려면 자바스크립트를 허용해야한다. 보안상의 이유로 막아놓은 것이다. @SuppressLint("SetJavaScriptEnabled")
 * -> goBack canGoBack goForward reload ...  webViewClient settings.javaScriptEnabled 이런 함수와 옵션들을 사용했다.
 * -> 매번 http:// 를 입력하지 않게 하는 법 : 없는지 확인하고 붙여주면 되는 거 아님?
 * -> WebViewClient vs WebChromeClient
 * -> WebViewClient 는 주로 컨텐츠로딩 관련된 이벤트들 onPageStarted onPageFinished
 * -> WebChromeClient 는 좀 더 브라우저 차원으로, 자바스크립트 알럿 이벤트 발생, 웹 컨텐츠 타이틀 정보 받아오거나 ... onProgressChanged
 *
 * EditText 사용
 * -> EditText 의 Input Type 설명은 TODO https://developer.android.com/training/keyboard-input/style?hl=ko 참고
 * -> imeOption 으로 키보드 옵션을 넣을 수 있다. TODO https://developer.android.com/training/keyboard-input/style?hl=ko 참고
 * 여기서의 네비게이션은 앞으로 가기 뒤로 가기 홈으로 가기가 있다.
 * -> 포커싱을 잃었다가 다시 받았을 때 텍스트 전체 선택해주는 옵션
 *
 *
 * android:layout_height="?attr/actionBarSize" height 를 현재 프로젝트의 테마 속성에 있는 값에 접근해서 사용
 * -> actionBarSize 는 기본적으로 테마에 포함되어있음
 * android:importantForAutofill="no" 라고 쓰면 Autofill 속성을 주지 않아도 경고가 뜨지 않는다.
 * EditText 의 tools:ignore="LabelFor" 는 ImageButton 의 tools:ignore="ContentDescription" 와 유사한 의미이다.
 * ERR_CLEARTEXT_NOT_PERMITTED 는 암호화되지 않은 웹 주소다 하며 보안관련된 얘기를 하는 것이다. https 를 사용하지 않아서 발생
 * -> 안드로이드 9 부터 디폴트로 http 를 지원하지 않게 되었다. 여기서는 보안 생각안하고 http 도 지원하는 옵션을 주어 해결함
 * -> webView.webViewClient = WebViewClient()
 * -> webView.settings.javaScriptEnabled = true\
 *
 * background 속성을 안주면 사실 투명한 거고 windowBackground 색깔이 보이는 것이다.
 * -> 그래서 background 속성을 안주면 elevation 속성을 사용해도 아무런 효과가 나타나지 않았던 것이다.
 * android:background="?attr/selectableItemBackground" 이런식으로 리플 이펙트를 줄 수도 있다.
 * -> 이미지 버튼에는 기본 백그라운드 크기가 있는데 저렇게 백그라운드 주면
 * app:layout_constraintDimensionRatio="1:1"
 * -> 세로로 아래 위 연결된 상태에서 1:1 주면 세로 기준으로 1대 1 잡히는 거라, float 값으로 줄 수도 있다.
 * android:paddingHorizontal="16dp" 이렇게 한번에 양쪽만 줄 수 있구나, EditText 커서를 안으로 집어 넣으려고 사용
 *
 * pull to refresh ( swipe refresh layout )라는 키워드 -> build.gradle 에 의존성 추가
 * -> 스크롤 가능한 영역을 감싸서 구현하는데 webView 는 기본적으로 스크롤이 가능한가봐..?
 * -> 동그라미 회전 ui 가 사라지지 않는 이슈는 isRefreshing? 을 false 로 바꿔줘야하는데,
 * -> 페이지 로딩 끝나고 해야하니까 WebViewClient 상속해서 onPageFinished
 * -> 이 때 inner class 를 사용해서 상위 클래스의 private 변수들에 접근할 수 있다.
 * -> TODO https://developer.android.com/jetpack/androidx/releases/swiperefreshlayout?hl=ko#groovy 참고
 *
 * Content Loading ProgressBar
 *
 *
 * goBackButton.isEnabled = webView.canGoBack() 센스 ㄷㄷㄷ
 *
 * <item name="android:windowLightStatusBar">true</item> 쓰면 스테이터스 바의 아이콘이  어둡게 변한다?
 *
*/

class WebActivity : AppCompatActivity() {
    private val webView: WebView by lazy {
        findViewById(R.id.web_web_view)
    }
    private val addressBar: EditText by lazy {
        findViewById(R.id.web_button_address_bar)
    }
    private val goHomeButton: ImageButton by lazy {
        findViewById(R.id.web_button_home)
    }
    private val goBackButton: ImageButton by lazy {
        findViewById(R.id.web_button_back)
    }
    private val goForwardButton: ImageButton by lazy {
        findViewById(R.id.web_button_forward)
    }
    private val refreshLayout: SwipeRefreshLayout by lazy {
        findViewById(R.id.web_refresh_layout)
    }
    private val progressBar: ContentLoadingProgressBar by lazy {
        findViewById(R.id.web_progress_bar)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web)
        initViews()
        bindViews()
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun initViews() {
        webView.apply {
            webViewClient = WebViewClient()
            webChromeClient = WebChromeClient()
            settings.javaScriptEnabled = true
            loadUrl(DEFAULT_URL)
        }
        // 아래코드와 달리 위 코드는 webView 를 한번만 썼다. apply 활용법
//        webView.webViewClient = WebViewClient()
//        webView.settings.javaScriptEnabled = true
//        webView.loadUrl("http://www.google.com")


    }
    private fun bindViews() {
        addressBar.setOnEditorActionListener { v, actionId, event ->
            // xml 파일에서 EditText 의 imeOption 을 actionDone 으로 주었다.
            if(actionId == EditorInfo.IME_ACTION_DONE) {
                val loadingUrl = v.text.toString()
                if (URLUtil.isNetworkUrl(loadingUrl)) {
                    webView.loadUrl(loadingUrl)
                } else {
                    // 기본적으로 https 를 지원하는 모든 웹브라우저는 http 로 접근 했을 때 https 로 리다이렉션 해주는 게 일반적
                    webView.loadUrl("http://${loadingUrl}")
                }

            }
            // true 반환 하면 여기서 다 소비한 것으로 간주하여 키보드가 닫히지 않는 이슈 발생생
            return@setOnEditorActionListener false
        }

        goHomeButton.setOnClickListener {
            webView.loadUrl(DEFAULT_URL)
        }

        goForwardButton.setOnClickListener {
            webView.goForward()
        }

        goBackButton.setOnClickListener {
            webView.goBack()
        }

        refreshLayout.setOnRefreshListener {
            webView.reload()
        }
   }

    // Back 버튼 눌렀을 때 호출되는 함수
    override fun onBackPressed() {
        if(webView.canGoBack()) {
            webView.goBack()
        } else {
            super.onBackPressed() // 이게 백버튼 눌렀을 때 앱이 종료되는 경우
        }

    }

    inner class WebViewClient: android.webkit.WebViewClient() {

        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
            super.onPageStarted(view, url, favicon)

            progressBar.show()
        }

        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)

            refreshLayout.isRefreshing = false
            progressBar.hide()
            goBackButton.isEnabled = webView.canGoBack()
            goForwardButton.isEnabled = webView.canGoForward()
            addressBar.setText(url)
        }

    }
    inner class WebChromeClient: android.webkit.WebChromeClient() {
        override fun onProgressChanged(view: WebView?, newProgress: Int) {
            super.onProgressChanged(view, newProgress)
            // 0~100 사이 정수
            progressBar.progress = newProgress
        }
    }


    companion object {
        private const val DEFAULT_URL = "http://www.google.com"
    }
}
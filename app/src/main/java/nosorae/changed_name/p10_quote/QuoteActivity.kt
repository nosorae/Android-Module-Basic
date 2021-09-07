package nosorae.changed_name.p10_quote

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import androidx.viewpager2.widget.ViewPager2
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import nosorae.changed_name.R
import nosorae.changed_name.p10_quote.pager_adapter.QuotesPagerAdapter
import org.json.JSONArray
import org.json.JSONObject
import kotlin.math.absoluteValue

/**
 * firebase 의 remote config 활용해서 코드 수정 - 배포 없이 앱 내용 (동작과 모양, 옵션, 데이터) 변경하는 기능
 * -> TODO https://firebase.google.com/docs/remote-config 참고
 * -> TODO https://firebase.google.com/docs/remote-config/loading 참고
 * -> TODO https://firebase.google.com/docs/remote-config/use-config-android 참고
 * -> 1. firebase remote-config 창에 이름-값 쌍을 넣고 퍼블리싱한다.
 * -> 2. Firebase.remoteConfig 로 가져와서 .getString("key 값") 으로 값을 가져온다.
 * ViewPager2 사용 (실무에서는 아직 ViewPager 를 더 많이 사용한다고 함, 곧바로 ViewPager2 로 업데이트 하기 문제가 있다고 함, but 지속 업데이트로 어떻게 될지 모르니 둘 다 알자)
 * -> TODO https://developer.android.com/training/animation/vp2-migration?hl=ko#vertical-support 참고
 * -> ViewPager2 는 리사이클러뷰와 구현방식이 유사하다.
 * -> ViewPager2 무한 스크롤링 효과 : 여러가지 방법 있지만, 어댑터를 속이는 방식이다.
 * -> getItemCount 에 큰 값을 주고, onBindViewHolder 에 전달되는 position 을 리스트 사이즈로 나머지 연산해준다.
 * -> 그리고 큰 값의 절반을 시작지점으로 주면 무한 스크롤링을 구현할 수 있다. 처음을 지정하고 싶다면 큰 값 절반을 리스트 사이즈로 % 연산한 값을 빼주면 된다.
 * JSON : Javascripts Object Notation 의 약자로, 정형화된 데이터 교환 방식
 * PageTransform A PageTransformer is invoked whenever a visible/attached page is scrolled.
 * -> 페이지 전환효과
 * -> TODO https://developer.android.com/reference/androidx/viewpager/widget/ViewPager.PageTransformer 참고
 *
 * GONE 과 INVISIBLE 의 차이?
 * 로딩 다이얼로그 사용
 *
 *
 */
class QuoteActivity : AppCompatActivity() {
    private val viewPager: ViewPager2 by lazy {
        findViewById(R.id.quote_view_pager)
    }
    private val progressBar: ProgressBar by lazy {
        findViewById(R.id.quote_progress_bar)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quote)
        initViews()
        initData()
    }


    private fun initViews() {
        viewPager.setPageTransformer { page, position ->
            // scale, translation, alpha 이런 것들을 조정할 수 있는데 여기서는 alpha 값 가운데있다가 스와이프 할 때마다 alpha 값이 0에 수렴
            when {
                position.absoluteValue >= 0.5F -> {
                    page.alpha = 0F
                }
                position == 0F -> {
                    page.alpha = 1F
                }
                else -> {
                    page.alpha = 1F - 2*position.absoluteValue
                }
            }


        }
    }

    private fun initData() {
        val remoteConfig = Firebase.remoteConfig
        remoteConfig.setConfigSettingsAsync(
            remoteConfigSettings {
                // 앱을 들어올 때마다 fetch 할 수 있게 0을 넣겠다.
                minimumFetchIntervalInSeconds = 0
            }
        )
        remoteConfig.fetchAndActivate().addOnCompleteListener {
            progressBar.visibility = View.GONE
            if (it.isSuccessful) {
                val quotes = parseQuotesJson(remoteConfig.getString("quotes"))
                val isNameRevealed = remoteConfig.getBoolean("is_name_revealed")
                displayQuotesPager(quotes, isNameRevealed)
            }
        }

    }
    private fun displayQuotesPager(quotes: List<Quote>, isNameRevealed: Boolean) {
        val adapter = QuotesPagerAdapter(
            quotes,
            isNameRevealed
        )
        viewPager.adapter = adapter
        // 시작지점을 이렇게 잡는다. 스크롤 되는 느낌 안주고 바로 잡히게 하기 위해서 smoothScroll 값은 false 로 전달한다.
        viewPager.setCurrentItem((Int.MAX_VALUE/2) - (Int.MAX_VALUE/2 % quotes.size), false)

    }
    private fun parseQuotesJson(json: String): List<Quote> {
        val jsonArray = JSONArray(json)
        var jsonList = emptyList<JSONObject>()
        for(index in 0 until jsonArray.length()) {
            val jsonObject = jsonArray.getJSONObject(index)
            jsonObject?.let {
                jsonList = jsonList + it
            }
        }
        // JSONObject 를 담는 리스트를 Quote 를 담는 리스트로 변경하는 코드
        return jsonList.map {
            Quote(
                it.getString("quote"),
                it.getString("name")
            )
        }
    }
}
package nosorae.module_basic.p14_market

import android.os.Bundle
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import nosorae.module_basic.R
import nosorae.module_basic.databinding.ActivityMarketBinding
import nosorae.module_basic.p14_market.chatList.MarketChatListFragment
import nosorae.module_basic.p14_market.home.MarketHomeFragment
import nosorae.module_basic.p14_market.myPage.MarketMyPageFragment

/**
 * - Bottom NavigationVeiw 사용
 *      menu 는 버튼들 관리하는 리스트 resource 폴더로 menu 를 만들고
 *      selector 로 누를 때 누르지 않았을 때 선택이 됐을 때의 상황에서 어떤 색깔을 넣을지 결정할 수 있다. icon 과 text 를 따로 색깔설정할 수 있다.
 *      app:menu="@menu/market_bottom_navigation_menu"
 *      app:itemIconTint="@drawable/selector_market_menu_color"
 *      app:itemTextColor="@color/black"
 *      app:itemRippleColor="@null" // 리플 효과를 없애는 방법
 *      .setOnNavigationItemSelectedListener 를 붙여서
 *
 * - Fragment 사용
 *      Activity 와 다르게 앱에 재사용가능 한 부분을 잘라서, 독립적인 수명주기를 가진 레이아웃을 말한다.
 *      자체 입력 이벤트를 처리할 수 있다. 대신 프래그먼트 혼자 존재 x 고 Activity 상에 존재 o
 *      비슷하게 생긴 화면을 커스텀 뷰로 만들었던 것처럼 비슷한 역할하는 액티비티나 뷰모음을 프래그먼트로 만들어서 좀 더 편하게 다른 곳에서 재사용할 수 있다.
 *      그래서 ui 의 모듈성이 올라간다.
 *      액티비티에는 support fragment manager 라는 게 있다. attach 된 fragment 들을 관리하는 친구로 transaction 으로 작업 시작한다고 알리고 commit 으로 끝난다.
 *      Fragment 의 생명주기 공부 TODO https://developer.android.com/guide/components/fragments?hl=ko 참고 // 코드로도 찍어볼 것
 *      액티비티 자체가 컨택스트였는데 프래그먼트는 컨텍스트가 아니므로 context (getContext) 변수로 따로 사용한다.
 *
 *
 *
 *
 * - Firebase Storage 활용
 *       storage.reference.child("market/article/photo").child(fileName).putFile(uri).addOnCompleteListener {}
 *       {} 안에  storage.reference.child("market/article/photo").child(fileName).downloadUrl.addOnSuccessListener { uri ->
 *       uri 를 database 에 저장해놓고 Glide 로 불러오면 좋겠다.
 *
 *
 * - Firebase Realtime Database ( p13 Tinder 참고 )
 *      articleDB.removeEventListener(listener) 로 리스너 달았던 것을 제거할 수 있다. 프래그먼트는 계속 재사용되니 데이터 중복해서 들어가는 것을 방지?
 *
 *
 * - Date 와 SimpleDateFormat 을 사용한 날짜 표현
 *      Long 타입으로 받아온 현재시간을 Date 타입으로 바꿔서 SimpleDataFormat 에 넣어줌
 *
 * - 상품 업로드와 채팅기능 구현
 *
 * - 시스템에 정의된 ActionBarSize 를 들고 오는 방법 android:layout_height="?attr/actionBarSize"
 *
 * - RecyclerView Adapter 를 ListAdapter 로 구현할 때 주의할 점 : androidx 것을 import 한다. import androidx.recyclerview.widget.ListAdapter
 *
 * - Material 계 View 는 background 로 색상이 먹지 않으니 backgroundTint 로 색상을 준다.
 *
 *
 *
 *
 *
 */
class MarketActivity: AppCompatActivity() {

    private lateinit var binding: ActivityMarketBinding

    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var frameLayout: FrameLayout
    private lateinit var homeFragment: MarketHomeFragment
    private lateinit var chatListFragment: MarketChatListFragment
    private lateinit var myPageFragment: MarketMyPageFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMarketBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initVariables()
        replaceFragment(homeFragment)
        initBottomNavigationView()

    }
    private fun initVariables() {
        bottomNavigationView = binding.marketBottomNavigationView
        frameLayout = binding.marketFrameLayout
        homeFragment = MarketHomeFragment()
        chatListFragment = MarketChatListFragment()
        myPageFragment = MarketMyPageFragment()
    }

    private fun initBottomNavigationView() {
        bottomNavigationView.setOnNavigationItemSelectedListener {
            // 여기서 MenuItem.itemId 는 resource - menu 에 만들었던 <item> 들의 id 를 의미한다.
            when (it.itemId) {
                R.id.market_bottom_navigation_menu_home -> replaceFragment(homeFragment)
                R.id.market_bottom_navigation_menu_chat_list -> replaceFragment(chatListFragment)
                R.id.market_bottom_navigation_menu_my_page -> replaceFragment(myPageFragment)
            }
            true
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .apply {
                replace(R.id.market_frame_layout, fragment)
                commit()
            }

    }
}
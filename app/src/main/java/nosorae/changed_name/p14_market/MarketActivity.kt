package nosorae.changed_name.p14_market

import android.os.Bundle
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import nosorae.changed_name.R
import nosorae.changed_name.databinding.ActivityMarketBinding
import nosorae.changed_name.p14_market.chatList.MarketChatListFragment
import nosorae.changed_name.p14_market.home.MarketHomeFragment
import nosorae.changed_name.p14_market.myPage.MarketMyPageFragment

/**
 * - Bottom NavigationVeiw 사용
 *      menu 는 버튼들 관리하는 리스트 resource 폴더로 menu 를 만들고
 *      selector 로 누를 때 누르지 않았을 때 선택이 됐을 때의 상황에서 어떤 색깔을 넣을지 결정할 수 있다. icon 과 text 를 따로 색깔설정할 수 있다.
 *      app:menu="@menu/market_bottom_navigation_menu"
 *      app:itemIconTint="@drawable/selector_market_menu_color"
 *      app:itemTextColor="@color/black"
 *      app:itemRippleColor="@null" // 리플 효과를 없애는 방법
 *      .setOnNavigationItemSelectedListener 를 붙여서 각각에 대해
 *       supportFragmentManager.beginTransaction().apply {
 *          replace(R.id.market_frame_layout, fragment)
 *          commit()
 *       }
 *       selector ( Bottom Navigation View 아니어도 다른 버튼에서도 사용할 수 있다. ) 활용
 *
 * - Fragment 사용
 *      Activity 와 다르게 앱에 재사용가능 한 부분을 잘라서, 독립적인 수명주기를 가진 레이아웃을 말한다.
 *      자체 입력 이벤트를 처리할 수 있다. 대신 프래그먼트 혼자 존재 x 고 Activity 상에 존재 o
 *      비슷하게 생긴 화면을 커스텀 뷰로 만들었던 것처럼 비슷한 역할하는 액티비티나 뷰모음을 프래그먼트로 만들어서 좀 더 편하게 다른 곳에서 재사용할 수 있다.
 *      그래서 ui 의 모듈성이 올라간다.
 *      액티비티에는 support fragment manager 라는 게 있다. attach 된 fragment 들을 관리하는 친구로 transaction 으로 작업 시작한다고 알리고 commit 으로 끝난다.
 *      Fragment 의 생명주기 공부 TODO https://developer.android.com/guide/components/fragments?hl=ko 참고 // 코드로도 찍어볼 것
 *      액티비티 자체가 컨택스트였는데 프래그먼트는 컨텍스트가 아니므로 context (getContext) 변수로 따로 사용한다.
 *      activity 가 필요한데 nullable 이다? 그러면 requireActivity 사용, 대신 requireActivity 는 activity 가 null 일때 Exception 을 발생시켜서 앱이 죽는다.
 *      그래서 이 프래그먼트는 무조건 액티비티에서 떨어지지 않아 그럴 땐 사용해도 되겠지만 activity?.let{} 이런식으로 null 처리를 하고 들어오는 게 바람직하다. 더 좋은 코드
 *
 *
 * - Firebase Storage 활용
 *       사진 업로드용으로 활용하였다. 테스트할 때는 권한을 if true 로 풀어준다.
 *       storage.reference.child("market/article/photo").child(fileName).putFile(uri).addOnCompleteListener {}
 *       putFile 에서 uri 대신 데이터스트림 자체를 넣을 수도 있다. 여기서의 addOnCompleteListener 는 업로드 완료 콜백이다.
 *       다운로드 url 받아오기 성공 리스너는 아래와같다.
 *       {} 안에  storage.reference.child("market/article/photo").child(fileName).downloadUrl.addOnSuccessListener { uri ->
 *       uri 를 database 에 저장해놓고 Glide 로 불러오면 좋겠다.
 *
 * - Firebase Authentication
 *      if (auth.currentUser == null) 로 현재 로그인 여부 확인인
 *
 *
* - Firebase Realtime Database ( p13 Tinder 참고 )
 *      articleDB.removeEventListener(listener) 로 리스너 달았던 것을 제거할 수 있다. 프래그먼트는 계속 재사용되니 데이터 중복해서 들어가는 것을 방지?
 *      data class 객체를 통째로 업로드하는 방법 push().setValue(데이터 객체) 이다.
 *      constructor(): this("", "", "", 0) 이런식으로 빈생성자를 꼭 만들어야한다.
 *      userDB.child(auth.currentUser?.uid.orEmpty()).child(CHAT).push().setValue(chatRoom)
 *      여기서 push() 가 Create a reference to an auto-generated child location. 하고 그 방에 setValue 를 하게된다.
 *
 *      .addListenerForSingleValueEvent 와 snapshot.children.forEach 로 하나하나 들고 올 수 있다.
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
 * - Article 업로드시 EditText 가 비어있는 경우를 예외처리 해주지 않았다. showProgress 동안에는 뒷배경은 클릭안되게도 해봐
 *
 * - RecyclerView 사용시 item 의 height width 를 잘 생각하자 왜냐하면 화면이 꽉차면 다음 것이 안보일 수 있기때문
 *
 * -
 *
 * - 채팅은 firebase realtime database 가 아니었다면 웹 소켓이나 레트로핏 풀링방식? 으로 구현할 수 있다.
 *
 * - 추가1 채팅방에 마지막 메세지, 프로필사진, 닉네임, 나 오른쪽 상대 왼쪽, 색깔
 * - 추가2 TODO 채팅방 중복 방지는 어떻게 할까? articleId 와 senderId buyerId 세개 묶어서 키값으로 만들면 될 듯 한데
 * - 추가3 아이템 상세창
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
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.market_frame_layout, fragment)
            commit()
        }

    }
}
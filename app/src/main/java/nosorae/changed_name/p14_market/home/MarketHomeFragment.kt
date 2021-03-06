package nosorae.changed_name.p14_market.home

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import nosorae.changed_name.R
import nosorae.changed_name.databinding.FragmentMarketHomeBinding
import nosorae.changed_name.p14_market.MarketDBKey.Companion.ARTICLES
import nosorae.changed_name.p14_market.MarketDBKey.Companion.CHAT
import nosorae.changed_name.p14_market.MarketDBKey.Companion.DB_MARKET
import nosorae.changed_name.p14_market.MarketDBKey.Companion.MARKET_USERS
import nosorae.changed_name.p14_market.chatList.MarketChatListItem


// Fragment 의 인자에 layout id 를 넣어주면 자동으로 뷰 어태치가 된다고 한다.
class MarketHomeFragment : Fragment(R.layout.fragment_market_home) {
    private lateinit var binding: FragmentMarketHomeBinding

    private lateinit var articleDB: DatabaseReference
    private lateinit var userDB: DatabaseReference
    private lateinit var articleAdapter: ArticleAdapter
    private val articleList = mutableListOf<ArticleModel>()

    private val listener = object : ChildEventListener {
        override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
            // 스냅샷 하나하나가 아티클모델이니 리스트에 넣어서 submitList 하면 onCreateView 마다 리스트를 업데이트 할 수 있겠다.
            // 데이터 객체 통째로 저장하고 불러올 계획 getValue 에 ::class.java 를 넘겨줌
            val articleModel = snapshot.getValue(ArticleModel::class.java)
            // 위 코드에서 맵핑에 실패하면 articleModel 가 null
            articleModel ?: return
            articleList.add(articleModel)
            articleAdapter.submitList(articleList)
        }
        override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
        override fun onChildRemoved(snapshot: DataSnapshot) {}
        override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
        override fun onCancelled(error: DatabaseError) {}
    }

    private val auth: FirebaseAuth by lazy {
        Firebase.auth
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val fragmentHomeBinding = FragmentMarketHomeBinding.bind(view)
        binding = fragmentHomeBinding




        articleList.clear()
        articleDB = Firebase.database.reference.child(DB_MARKET).child(ARTICLES)
        userDB = Firebase.database.reference.child(DB_MARKET).child(MARKET_USERS)

        // 액티비티는 액티비티 종료하면 이벤트들이나 뷰가 다 디스트로이돼서 상관없는데 이 탭구조는 프래그먼트가 재사용되므로
        // onViewCreated 마다 리스너를 붙이면 중복으로 붙이게 될 수 있다.
        // 그래서 이벤트리스너를 전역으로 정의하고 onViewCreated 될 때마다 어태치하고 onDestroyView 마다 리무브해주는 방식으로 구현, onResume 에서는 데이터를 갱신해준다.
        articleDB.addChildEventListener(listener)

        initRecyclerView(view)
        initAddArticleButton(view)




    }

    private fun initAddArticleButton(view: View) {
        binding.marketHomeFloatingButtonAdd.setOnClickListener {
            // Fragment 의 context 는 nullable 이라서 null 제거하고 사용 가능 또는 requireContext 메서드 사용
            // 로그인 되어있어야만 상품을 등록할 수 있다고 가정한다.
            context?.let {
                if (auth.currentUser != null) {
                    startActivity(Intent(it, MarketAddArticleActivity::class.java))
                } else {
                    Snackbar.make(view, "로그인 후 사용해주세요", Snackbar.LENGTH_LONG).show()
                }
            }

        }
    }
    private fun initRecyclerView(view: View) {
        articleAdapter = ArticleAdapter(onItemClicked = { articleModel ->
            if (auth.currentUser != null) {
                // 내가 아닌 다른 유저의 상품을 누르면 채팅방을 만들어준다.
                if (auth.currentUser?.uid != articleModel.sellerId) {
                    val chatRoom = MarketChatListItem(
                        buyerId = auth.currentUser?.uid.orEmpty(),
                        sellerId = articleModel.sellerId,
                        itemTitle = articleModel.title,
                        key = System.currentTimeMillis()
                    )
                    userDB
                        .child(auth.currentUser?.uid.orEmpty())
                        .child(CHAT)
                        .push()
                        .setValue(chatRoom)

                    userDB.child(articleModel.sellerId)
                        .child(CHAT)
                        .push() // 이거 하고 안하고 차이가 임의의 키값 넣어주고 안넣어주고 차이였네, 채팅방 역할을 하겠다. 실수로 안넣고 실행해봐서 알게됨
                        .setValue(chatRoom)

                    Snackbar.make(view, "채팅방이 생성되었습니다. 채팅탭에서 확인해주세요.", Snackbar.LENGTH_LONG).show()


                } else {
                    // 내가 올린 아이템인 경우
                    Snackbar.make(view, "내가 올린 아이템입니다.", Snackbar.LENGTH_LONG).show()

                }
            } else {
                // 로그인 안 한 상태
                Snackbar.make(view, "로그인 후 사용해주세요", Snackbar.LENGTH_LONG).show()
            }

        })
        binding.fragmentMarketHomeRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.fragmentMarketHomeRecyclerView.adapter = articleAdapter

    }

    override fun onResume() {
        super.onResume()
        articleAdapter.notifyDataSetChanged()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        articleDB.removeEventListener(listener)

    }


}
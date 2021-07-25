package nosorae.module_basic.p13_tinder

import android.content.Intent
import android.os.Bundle
import android.renderscript.Sampler
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.setPadding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.yuyakaido.android.cardstackview.CardStackLayoutManager
import com.yuyakaido.android.cardstackview.CardStackListener
import com.yuyakaido.android.cardstackview.CardStackView
import com.yuyakaido.android.cardstackview.Direction
import nosorae.module_basic.R
import nosorae.module_basic.databinding.ActivityTinderLikeBinding
import nosorae.module_basic.p13_tinder.DBKey.Companion.DISLIKE
import nosorae.module_basic.p13_tinder.DBKey.Companion.LIKE
import nosorae.module_basic.p13_tinder.DBKey.Companion.LIKED_BY
import nosorae.module_basic.p13_tinder.DBKey.Companion.MATCHED
import nosorae.module_basic.p13_tinder.DBKey.Companion.NAME
import nosorae.module_basic.p13_tinder.DBKey.Companion.USERS
import nosorae.module_basic.p13_tinder.DBKey.Companion.USER_ID
import nosorae.module_basic.p13_tinder.model.CardItem

class TinderLikeActivity: AppCompatActivity(), CardStackListener {


    private lateinit var binding: ActivityTinderLikeBinding
    private var auth: FirebaseAuth = FirebaseAuth.getInstance()
    private lateinit var userDB: DatabaseReference

    private val adapter = CardItemAdapter()
    private val cardItems = mutableListOf<CardItem>()
    private val cardStackLayoutManager by lazy {
        CardStackLayoutManager(this, this)
    }

    private lateinit var cardStackView: CardStackView
    private lateinit var logoutButton: Button
    private lateinit var matchListButton: Button
    private lateinit var myName: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTinderLikeBinding.inflate(layoutInflater)
        setContentView(binding.root)


        initViews()

        userDB = Firebase.database.reference.child(USERS)
        getCurrentUser()
        initCardStackView()
        initLogoutButton()
        initMatchListButton()

    }

    private fun initViews() {
        cardStackView = binding.tinderLikeCardStackView
        logoutButton = binding.tinderLikeButtonLogout
        matchListButton = binding.tinderLikeButtonMatchList
        myName = binding.tinderLikeTextViewMyName
    }



    private fun getCurrentUser() {
        val currentUserDB = userDB.child(getCurrentUserId())
        currentUserDB.addListenerForSingleValueEvent(object: ValueEventListener {
            // 이 클래스의 onDataChange() 메서드는 리스너가 연결될 때 한 번 트리거된 후 하위 항목을 포함하여 데이터가 변경될 때마다 다시 트리거됩니다.
            override fun onDataChange(snapshot: DataSnapshot) {
                // 처음에 리스너 달았을 때는 리스너가 처음엔 리스너가 없었기 떄문에, 데이터가 존재하면 여기로 온다.
                // 그리고 수정되었을 떄도 여기로 온다.
                if (snapshot.child(NAME).value == null) {
                    showNameInputPopup()
                    return
                } else {
                    // 선택하지 않은 다른 유저정보 가져와서 리사클러뷰 갱신
                    myName.text = snapshot.child(NAME).value.toString()
                    getUnselectedUsers()
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    private fun getCurrentUserId(): String {
        // 로그인이 풀려서 currentUser 가 null 일 수도 있으니 null 체크 로그인 안돼있으면 MainActivity (여기서는 TinderActivity) 로 돌아가서 다시 로그인화면 등장
        if (auth.currentUser == null) {
            Toast.makeText(this, "로그인이 되어있지 않습니다.", Toast.LENGTH_SHORT).show()
            finish()
        }
        return auth.currentUser?.uid.orEmpty()
    }

    private fun showNameInputPopup() {
        // EditText 를 동적으로 만들어서  AlertDialog 에 할당
        val editText = EditText(this)
        editText.setPadding(10)
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.tinder_write_name))
            .setView(editText)
            .setPositiveButton("저장") {_,_ ->
                if (editText.text.isEmpty()) {
                    showNameInputPopup() // 다시팝업을 띄우면 되는데 재귀함수로 다시 띄우는 센스
                } else {
                    saveUserName(editText.text.toString())
                }
            }
            .setCancelable(false) // 바깥 눌러서 취소 못하게 막는 함수
            .show()
    }

    private fun saveUserName(name: String) {
        val userId = getCurrentUserId() // 사실상 null 처리 해줘서 null 일 일은 없다.
        val currentUserDB = userDB.child(userId)
        val user = mutableMapOf<String, Any>()
        user[USER_ID] = userId
        user[NAME] = name
        currentUserDB.updateChildren(user)
        myName.text = name

        // 유저 정보를 가져와서서
       getUnselectedUsers()
    }

    private fun initCardStackView() {
        // CardStackLayoutManager 의 두번째 인자인 인터페이스의 함수가 많으니 액티비티 클래스에 인터페이스를 implement 해서 구현
        cardStackView.layoutManager = cardStackLayoutManager
        cardStackView.adapter = adapter

    }

    private fun initLogoutButton() {

        logoutButton.setOnClickListener {
            auth.signOut()
            startActivity(Intent(this, TinderActivity::class.java))
            finish()
        }


    }

    private fun initMatchListButton() {

        matchListButton.setOnClickListener {
            startActivity(Intent(this, TinderMatchedUserActivity::class.java))
        }

    }

    private fun getUnselectedUsers() {
        // 선택되지 않은 유저들의 정보만 보려는 함수

        userDB.addChildEventListener(object: ChildEventListener {
            // onChildAdded 는 기존의 하위 요소별로 한 번씩 트리거된 후 지정된 경로에 하위 요소가 새로 추가될 때마다 다시 트리거됩니다.
            // 리스너에 전달되는 DataSnapshot에는 새 하위 데이터가 포함됩니다.
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
               // 초기에 데이터 불러오는 과정이나, 새로운 유저 등록되는 경우 이 이벤트로 온다.
                // 현재 이 앱으로 접속한 아이디가 나와 같지 않고, 상대방의  likedBy 에 내가 없다는 유저라면
                if (snapshot.child(USER_ID).value != getCurrentUserId() &&
                        snapshot.child(LIKED_BY).child(LIKE).hasChild(getCurrentUserId()).not() &&
                        snapshot.child(LIKED_BY).child(DISLIKE).hasChild(getCurrentUserId()).not()) {
                    val userId = snapshot.child(USER_ID).value.toString()
                    var name = "undecided"
                    if (snapshot.child(NAME).value != null) {
                        name = snapshot.child(NAME).value.toString()
                    }

                    cardItems.add(CardItem(userId, name))
                    adapter.submitList(cardItems)
                    adapter.notifyDataSetChanged()

                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                // 이름이 바뀌거나 라이크했을 때 이 이벤트로 온다.
                cardItems.find { it.userId == snapshot.key }?.let {
                    it.name = snapshot.child(NAME).value.toString()
                }
                adapter.submitList(cardItems) // 리스트 갱신할 때 이렇게 또 다시 넣어줘야하는구나?
                adapter.notifyDataSetChanged()
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {}

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    override fun onCardDragging(direction: Direction?, ratio: Float) {}

    override fun onCardSwiped(direction: Direction?) {
        when (direction) {
            Direction.Left -> dislike()
            Direction.Right -> like()
            else -> {

            }
        }
    }

    private fun dislike() {
        val card = cardItems[cardStackLayoutManager.topPosition-1]
        cardItems.removeFirst() // 이렇게 하면 topPosition 은 항상 0일 것 같다.
        // 상대방의 userId 로 들어간다.
        val currentUserDB = userDB.child(card.userId)
            .child(LIKED_BY)
            .child(DISLIKE)
            .child(getCurrentUserId()) // key 저장
            .setValue(true) // key 의 하위값에 true 저장

        // 매칭이 된 시점을 봐야한다.


        Toast.makeText(this, "${card.name} 님을 Dislike 하셨습니다.", Toast.LENGTH_SHORT).show()

    }
    private fun like() {
        val card = cardItems[cardStackLayoutManager.topPosition-1]
        cardItems.removeFirst() // 이렇게 하면 topPosition 은 항상 0일 것 같다.
        // 상대방의 userId 로 들어간다.
        val currentUserDB = userDB.child(card.userId)
            .child(LIKED_BY)
            .child(LIKE)
            .child(getCurrentUserId()) // key 저장
            .setValue(true) // key 의 하위값에 true 저장

        // 매칭이 된 시점을 봐야한다.
        saveMatchIfOtherUserLikedMe(card.userId)


        Toast.makeText(this, "${card.name} 님을 Like 하셨습니다.", Toast.LENGTH_SHORT).show()



    }
    private fun saveMatchIfOtherUserLikedMe(otherUserId: String) {
        // 이게 true 면 상대방도 좋아요 누른 것이고
        val otherUserDB = userDB.child(getCurrentUserId())
            .child(LIKED_BY)
            .child(LIKE)
            .child(otherUserId)
        otherUserDB.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.value == true) {
                    // 여기 오면 매치 된 것이다.
                    userDB.child(getCurrentUserId())
                        .child(LIKED_BY)
                        .child(MATCHED)
                        .child(otherUserId)
                        .setValue(true)

                    userDB.child(otherUserId)
                        .child(LIKED_BY)
                        .child(MATCHED)
                        .child(getCurrentUserId())
                        .setValue(true)
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })

    }

    override fun onCardRewound() {}

    override fun onCardCanceled() {}

    override fun onCardAppeared(view: View?, position: Int) {}

    override fun onCardDisappeared(view: View?, position: Int) {}
}
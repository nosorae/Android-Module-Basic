package nosorae.module_basic.p13_tinder

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import nosorae.module_basic.databinding.ActivityTinderMatchBinding
import nosorae.module_basic.p13_tinder.TinderDBKey.Companion.LIKED_BY
import nosorae.module_basic.p13_tinder.TinderDBKey.Companion.MATCHED
import nosorae.module_basic.p13_tinder.TinderDBKey.Companion.NAME
import nosorae.module_basic.p13_tinder.TinderDBKey.Companion.USERS
import nosorae.module_basic.p13_tinder.adapter.MatchedUserAdapter
import nosorae.module_basic.p13_tinder.model.CardItem

class TinderMatchedUserActivity: AppCompatActivity() {
    private lateinit var binding: ActivityTinderMatchBinding
    private var auth: FirebaseAuth = FirebaseAuth.getInstance()
    private lateinit var userDB: DatabaseReference

    private lateinit var recyclerView: RecyclerView
    private val adapter = MatchedUserAdapter()
    private val cardItems = mutableListOf<CardItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTinderMatchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userDB = Firebase.database.reference.child(USERS)
        initViews()
        initMatchedUserRecyclerView()
        getMatchUsers()

    }

    private fun initViews() {
        recyclerView = binding.tinderMatchRecyclerView
    }

    private fun initMatchedUserRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }

    private fun getMatchUsers() {

        val matchedDB = userDB.child(getCurrentUserId())
            .child(LIKED_BY)
            .child(MATCHED)

        writeLog(matchedDB.toString())
        matchedDB.addChildEventListener(object : ChildEventListener {

            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                // 여기서 key 는 userID 가 되겠다?
                if (snapshot.key?.isNotEmpty() == true) {
                    // id 를 이용해서 이름을 가져온다.
                    getUserByKey(snapshot.key.orEmpty())
                    writeLog(snapshot.key.toString())
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}

            override fun onChildRemoved(snapshot: DataSnapshot) {}

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}

            override fun onCancelled(error: DatabaseError) {}
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

    private fun getUserByKey(userId: String) {
        // 이번엔 한번만 가져오면 되니까 addListenerForSingleValueEvent 사용
        writeLog(userId)
        userDB.child(userId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                cardItems.add(CardItem(userId, snapshot.child(NAME).value.toString()))
                adapter.submitList(cardItems)
                adapter.notifyDataSetChanged() // 이거 안써서 삽질했다.
                writeLog("매치 리스트 업데이트 완료 ${snapshot.child(NAME).value.toString()}")
            }

            override fun onCancelled(error: DatabaseError) {}

        })
    }

    private fun writeLog(text: String) {
        Log.d("match", text)
    }
}
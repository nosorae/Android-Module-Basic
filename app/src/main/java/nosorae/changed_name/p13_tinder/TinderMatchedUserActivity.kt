package nosorae.changed_name.p13_tinder

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
import nosorae.changed_name.databinding.ActivityTinderMatchBinding
import nosorae.changed_name.p13_tinder.TinderDBKey.Companion.LIKED_BY
import nosorae.changed_name.p13_tinder.TinderDBKey.Companion.MATCHED
import nosorae.changed_name.p13_tinder.TinderDBKey.Companion.NAME
import nosorae.changed_name.p13_tinder.TinderDBKey.Companion.USERS
import nosorae.changed_name.p13_tinder.adapter.MatchedUserAdapter
import nosorae.changed_name.p13_tinder.model.CardItem

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
                // ????????? key ??? userID ??? ??????????
                if (snapshot.key?.isNotEmpty() == true) {
                    // id ??? ???????????? ????????? ????????????.
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
        // ???????????? ????????? currentUser ??? null ??? ?????? ????????? null ?????? ????????? ??????????????? MainActivity (???????????? TinderActivity) ??? ???????????? ?????? ??????????????? ??????
        if (auth.currentUser == null) {
            Toast.makeText(this, "???????????? ???????????? ????????????.", Toast.LENGTH_SHORT).show()
            finish()
        }
        return auth.currentUser?.uid.orEmpty()
    }

    private fun getUserByKey(userId: String) {
        // ????????? ????????? ???????????? ????????? addListenerForSingleValueEvent ??????
        writeLog(userId)
        userDB.child(userId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                cardItems.add(CardItem(userId, snapshot.child(NAME).value.toString()))
                adapter.submitList(cardItems)
                adapter.notifyDataSetChanged() // ?????? ????????? ????????????.
                writeLog("?????? ????????? ???????????? ?????? ${snapshot.child(NAME).value.toString()}")
            }

            override fun onCancelled(error: DatabaseError) {}

        })
    }

    private fun writeLog(text: String) {
        Log.d("match", text)
    }
}
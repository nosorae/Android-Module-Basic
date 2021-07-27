package nosorae.module_basic.p14_market.chatDetail

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import nosorae.module_basic.databinding.ActivityMarketChatRoomBinding
import nosorae.module_basic.p14_market.MarketDBKey.Companion.CHATS
import nosorae.module_basic.p14_market.MarketDBKey.Companion.DB_MARKET

class MarketChatRoomActivity: AppCompatActivity() {
    private lateinit var binding: ActivityMarketChatRoomBinding
    private val auth: FirebaseAuth by lazy {
        Firebase.auth
    }

    private val chatList = mutableListOf<MarketChatItem>()
    private val adapter = MarketChatItemAdapter()

    private var chatDB: DatabaseReference? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMarketChatRoomBinding.inflate(layoutInflater)
        setContentView(binding.root)




        setAllChat()
        initRecyclerView()
        initSendButton()

    }

    private fun initRecyclerView() {
        binding.marketChatRoomRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.marketChatRoomRecyclerView.adapter = adapter
    }
    private fun initSendButton() {
        binding.marketChatRoomButtonSend.setOnClickListener {
            val chatItem = MarketChatItem(
                senderId = auth.currentUser?.uid.orEmpty(),
                message =  binding.marketChatRoomEditTextMessage.text.toString()
            )
            chatDB?.push()?.setValue(chatItem)

        }

    }
    private fun setAllChat() {
        val chatKey = intent.getLongExtra("chatKey", -1)
        chatDB = Firebase.database.reference.child(DB_MARKET).child(CHATS).child("$chatKey")

        chatDB?.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val chatItem = snapshot.getValue(MarketChatItem::class.java)
                chatItem ?: return

                chatList.add(chatItem)
                adapter.submitList(chatList)
                // 키가 없어서 DiffUtil 이 제대로 동작 안 할 수도 있어서 바로 notify 해준다.
                adapter.notifyDataSetChanged()

            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}

            override fun onChildRemoved(snapshot: DataSnapshot) {}

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}

            override fun onCancelled(error: DatabaseError) {}
        })
    }

}
package nosorae.module_basic.p14_market.chatList

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import nosorae.module_basic.R
import nosorae.module_basic.databinding.FragmentMarketChatListBinding
import nosorae.module_basic.p14_market.MarketDBKey.Companion.CHAT
import nosorae.module_basic.p14_market.MarketDBKey.Companion.DB_MARKET
import nosorae.module_basic.p14_market.MarketDBKey.Companion.MARKET_USERS
import nosorae.module_basic.p14_market.chatDetail.MarketChatRoomActivity
import nosorae.module_basic.p14_market.home.ArticleAdapter

class MarketChatListFragment: Fragment(R.layout.fragment_market_chat_list) {

    private lateinit var binding: FragmentMarketChatListBinding
    private lateinit var chatListAdapter: MarketChatListAdapter
    private lateinit var chatDB: DatabaseReference
    private val chatRoomList = mutableListOf<MarketChatListItem>()
    private val auth: FirebaseAuth by lazy {
        Firebase.auth
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val fragmentMarketChatListBinding = FragmentMarketChatListBinding.bind(view)
        binding = fragmentMarketChatListBinding



        initRecyclerView(view)

        if (auth.currentUser == null) {
            return
        }

        initChatDB()






    }
    private fun initChatDB() {
        chatDB = Firebase.database.reference.child(DB_MARKET).child(MARKET_USERS).child(auth.currentUser?.uid.orEmpty()).child(CHAT)
        chatDB.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.forEach {

                    val model = it.getValue(MarketChatListItem::class.java)
                    Log.d("market", "chatting rooms : ${model.toString()}")
                    model ?: return
                    chatRoomList.add(model)
                    Log.d("market", "maked chatting rooms : ${chatRoomList.toString()}")
                }
                chatListAdapter.submitList(chatRoomList)
            }

            override fun onCancelled(error: DatabaseError) {}
        })
        chatListAdapter.notifyDataSetChanged()
    }
    private fun initRecyclerView(view: View) {
        chatRoomList.clear()
        chatListAdapter = MarketChatListAdapter(
            onItemClicked = { chatRoom ->
                // 채팅방으로 이동하는 코드 작성
                context?.let {
                    val intent = Intent(it, MarketChatRoomActivity::class.java)
                    intent.putExtra("chatKey", chatRoom.key)
                    startActivity(intent)
                }
            }
        )
        binding.fragmentMarketChatListRecyclerView.adapter = chatListAdapter
        binding.fragmentMarketChatListRecyclerView.layoutManager = LinearLayoutManager(context)



    }

    override fun onResume() {
        super.onResume()
        chatListAdapter.notifyDataSetChanged()
    }
}
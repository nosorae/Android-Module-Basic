package nosorae.changed_name.p12_book

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.MotionEvent
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import nosorae.changed_name.R
import nosorae.changed_name.databinding.ActivityBookBinding
import nosorae.changed_name.p12_book.adapter.BookRecyclerAdapter
import nosorae.changed_name.p12_book.adapter.BookSearchHistoryAdapter
import nosorae.changed_name.p12_book.api.BookService
import nosorae.changed_name.p12_book.model.BestSellerDTO
import nosorae.changed_name.p12_book.model.BookSearchHistory
import nosorae.changed_name.p12_book.model.SearchBookDTO
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * - RecyclerView 사용
 *  ItemBookBinding.inflate(LayoutInflater.from(parent.context), parent, false) 이렇게 ~Binding 클래스로 바로 inflate 할 수도 있구나
 *  위 코드의 반환은 ItemBookBinding 이다. inflate 해서 반환해주는 개념인가보다
 *  ListAdapter<Book, BookRecyclerAdapter.BookItemViewHolder>(diffUtil) 로 RecyclerView 의 Adapter 를 구현했다.
 *  adapter.submitList(it.books)
 *
 * - View Binding 사용
 *  TODO https://developer.android.com/topic/libraries/view-binding?hl=ko 참고
 *  .databinding 에 ~Binding 파일들이 자동으로 생성된다.
 *    viewBinding {
        enabled = true
      }
 *
 * - Retrofit 사용 (feat. http://book.interpark.com/blog/bookpinion/bookpinionOpenAPIInfo.rdo 의 Open API)
 *  TODO https://square.github.io/retrofit/ 참고
 *  A type-safe HTTP client for Android and Java
 *  Postman : api 를 실제로 요청할 때 반환되는 결과 값을 쉽게 확인할 수 있는 기능을 제공
 *  http 면 not permitted by network security policy 에러발생, 안드로이드에서 허용하지 않은 것이다.
 *  해결방법은 두 가지 이다. 1. 서버에 https 로 연결할 것을 요청하는 방법 2. http 허용하는 설정을 하는 방법
 *  여기서 사용하는 인터파크 api 는 https 로도 요청할 수 있으므로 1 로 해결한다.
 *  implementation 'com.squareup.retrofit2:converter-gson:2.9.0' 추가해서 response 를 내 @SerializedName(name) 사용한 데이터 클래스와 맵핑
 *
 * - Glide 사용
 *  url 을 이미지뷰에 로딩할 수 있다.
 *  implementation 'com.github.bumptech.glide:glide:4.12.0' // gradle 추가
 *  with -> load -> into
 *
 * - Android Room 사용 -> 최근 검색어 저장, 리뷰 저장 및 수정 삭제 (CRUD)
 *  p4_calculator 에서 사용했었다.
 *  kapt "androidx.room:room-compiler:2.3.0" // gradle 추가 잊지마
 *  @Database(entities = [~::class], version = 1) 어노테이션 -> @Entity 데이터 클래스, @Dao 인터페이스를 각각 만들어준다.
 *  : RoomDatabase 상속
 *  DAO란 Data Access Object의 약어로서 실질적으로 DB에 접근하는 객체를 말한다
 *  @Insert(onConflict = OnConflictStrategy.REPLACE) 로 만약에 이미 리뷰가 있다면, replace 하는 방식으로 삽입할 수도 있다.
 *  version 의 의미 : HistoryDao 통해서  이름 BookSearchDB 로 DB 의 파일이 이미 만들어졌는데, 수정작업 통해서 ReviewDao 테이블 추가했는데
 *  이 새로운 db 를 어떻게 처리할지 room 은 알 수 없다. 그래서 원래있던 db 를 지우고 새로 만들거나 version 2 로 올려서 어떤 작업을 통해서 변경해줘햐하는지 코드를 통해서 알려주는 방법이 있다.
 *  즉 db 가 이미 생성이 됐는데, 새로운 DB 룰이 들어왔을 때 마이그레이션이 안돼서 생기는 문제다. 여기서는 앱을 삭제했다가 다시 깔아서 해결했다.
 *  Migration 코드는 BookAppDatabase 클래스 참고 (데이터베이스에 직접 쿼리문을 작성해서 어떻게 어떤 새로운 데이터테이블을 바꿨는지 알려줘야한다.)
 *
 *  getOneReview 를 nullable 로 하지 않아서 리뷰가 써있지 않은 책의 id 로 BookReview 객체를 가져오려하면 앱이 터졌다.
 *  그러므로 DB 를 다룰 때는 쿼리 결과가 없을 때를 (null 일 경우를) 대비하자
 *
 * - ScrollView vs RecyclerView
 *  ScrollView 는 모든 뷰를 미리 다 그려놓고 있기 때문에 길어지면 느려지거나 죽을 수 있다.
 *  RecyclerView 몇개만 그리고 올라가면 위에 있던 뷰는 없애버리고 밑에 미리 그려진 뷰에 데이터만 할당하는 방식 위아래 두개정도만 미리 그려놓는 거
 *
 * - Text 계열에서 android:lines="1" 한줄로 고정
 * - val historyDeleteClickedListener: (String) -> Unit 람다를 인자로 받아버리기!
 *
 * - Book data class 를 intent 에 putString 하기 위해 직렬화 하는 작업
 *  id 'kotlin-parcelize' 을 gradle 에 추가한다.
 *  @Parcelize 를 Book data class 상단에 추가한다.
 *  android.os.Parcelable 상속하게 한다.
 *
 *
 *
 */

// A15295E2FAAE0DD7D49F1EBDF8EF44CF80BC397F403F276D0774FB2EF700E280
class BookActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBookBinding
    private lateinit var adapter: BookRecyclerAdapter
    private lateinit var historyAdapter: BookSearchHistoryAdapter
    private lateinit var bookService: BookService
    private lateinit var db: BookAppDatabase
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // setContentView 전에 ~Binding 으로 binding 객체?를 가져올 수 있다.
        // 액티비티 안에는 layoutInflater 가 이미 존재하니 바로 써준다.
        binding = ActivityBookBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initBookRecyclerView()
        initHistoryRecyclerView()

        // db 만들기
        db = getAppDatabase(this)

        // 레트로핏 구현
        val retrofit = Retrofit.Builder()
            .baseUrl("http://book.interpark.com")
            .addConverterFactory(GsonConverterFactory.create()) // 서버에서 가져온 String 형태의 json 을 gson 으로 변환하는 걸 도와주는 gson 라이브러리도 추가
            .build()

        // 서비스 구현
        bookService = retrofit.create(BookService::class.java)

        bookService.getBestSellerBooks(getString(R.string.interParkAPIKey))
            .enqueue(object : Callback<BestSellerDTO> {
                override fun onResponse(
                    call: Call<BestSellerDTO>,
                    response: Response<BestSellerDTO>
                ) {
                    if (response.isSuccessful.not()) {
                        Log.e(LOG_TAG, "not success")
                        return
                    }

                    // 바디가 없을 수도 있으니 ?.let 으로 꺼내본다.
                    response.body()?.let {
                        Log.d(LOG_TAG, it.toString())

                        it.books.forEach { book ->
                            Log.d(LOG_TAG, book.toString())
                        }

                        adapter.submitList(it.books)
                        //adapter.notifyDataSetChanged()
                    }

                }

                override fun onFailure(call: Call<BestSellerDTO>, t: Throwable) {
                    // Log.e 로 로그를 찍으면 Logcat 에서 글씨가 빨간색으로 뜬다?
                    Log.e(LOG_TAG, t.toString())

                }
            })


    }


    private fun search(keyWord: String) {
        bookService.getBookByName(getString(R.string.interParkAPIKey), keyWord)
            .enqueue(object : Callback<SearchBookDTO> {
                override fun onResponse(
                    call: Call<SearchBookDTO>,
                    response: Response<SearchBookDTO>
                ) {
                    if (response.isSuccessful.not()) {
                        Log.e(LOG_TAG, "not success search")
                        return
                    }
                    // 여기 왔다는 것은 일단 검색이 끝났다는 것이다. 그러니 히스토리 뷰는 숨기고, 서치 기록을 저장한다.
                    hideHistoryView()
                    saveSearchKeyword(keyWord)


                    // 바디가 없을 수도 있으니 ?. 위 bestSeller 요청할 때 코드보다 더 간결해졌다.
                    adapter.submitList(response.body()?.books.orEmpty())

                }

                override fun onFailure(call: Call<SearchBookDTO>, t: Throwable) {
                    hideHistoryView()
                    Log.e(LOG_TAG, t.toString())
                }
            })
    }

    private fun saveSearchKeyword(keyword: String) {
        Thread {
            db.historyDao().insertHistory(BookSearchHistory(null, keyword))
        }.start()
    }

    private fun deleteSearchKeyword(keyword: String) {
        Thread {
            // 해당 keyword 가 중복해서 들어 갔을 수도 있는데, 그렇다면 그 모든 데이터가 삭제
            db.historyDao().delete(keyword)
            // 뷰 갱신도 함께 일어나야함
            showHistoryView()
        }.start()
    }

    private fun searchByHistoryKeyword(keyword: String) {
        search(keyword)
    }

    private fun initSearchEditText() {
        // EditText 키 누를 때의 리스너, keyCode 관련 상수는 KeyEvent 에 있다.
        // evnet 는 keyup keydown 이 있는데, 간격을 보고 click 인지 long press 인지 알 수도 있음
        binding.bookEditTextSearch.setOnKeyListener { v, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == MotionEvent.ACTION_DOWN) {
                search(binding.bookEditTextSearch.text.toString())
                // 이벤트를 처리했음을 의미
                return@setOnKeyListener true
            }
            // 이벤트 처리 안끝났고, 키 이벤트가 처리 안됐고 시스템에서 정의한 다른 이벤트가 실행돼야한다는 의미
            false
        }
        binding.bookEditTextSearch.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                showHistoryView()
                //return@setOnTouchListener true
                // 이렇게 주면 액션다운이라는 버튼에 대해서 이벤트가 이미 처리되어 터치가 되는 과정이 실행되지 않는다.
                // 키패드가 정상적으로 올라와야할 것 아닌가!
            }
            false
        }
    }

    private fun initBookRecyclerView() {
        adapter = BookRecyclerAdapter(itemClickedListener = {
            val intent = Intent(this, BookDetailActivity::class.java)
            // Book 데이터 클래스를 직렬화시켜주는 작업이 필요하다.
            intent.putExtra("bookModel", it)
            startActivity(intent)
        })
        binding.bookRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.bookRecyclerView.adapter = adapter
    }

    private fun initHistoryRecyclerView() {
        historyAdapter = BookSearchHistoryAdapter(
            historyDeleteClickedListener = {
                deleteSearchKeyword(it)
            },
            historyClickedListener = {
                searchByHistoryKeyword(it)
            }
        )
        binding.bookRecyclerViewSearchHistory.layoutManager = LinearLayoutManager(this)
        binding.bookRecyclerViewSearchHistory.adapter = historyAdapter

        initSearchEditText()
    }

    private fun showHistoryView() {
        Thread {
            val keywords = db.historyDao().getAll().reversed()
            runOnUiThread {
                binding.bookRecyclerViewSearchHistory.isVisible = true
                historyAdapter.submitList(keywords.orEmpty())

            }
        }.start()
        //binding.bookRecyclerViewSearchHistory.isVisible = true

    }

    private fun hideHistoryView() {
        binding.bookRecyclerViewSearchHistory.isVisible = false

    }


    companion object {
        // 이렇게 companion object 로 뺄 수도 있지만 좀 더 전역적으로 쓰일 수 있는 이런 api key 는 xml file 로 빼는 게 더 좋다.
        //private const val API_KEY = "A15295E2FAAE0DD7D49F1EBDF8EF44CF80BC397F403F276D0774FB2EF700E280"
        private const val LOG_TAG = "Book"
    }
}
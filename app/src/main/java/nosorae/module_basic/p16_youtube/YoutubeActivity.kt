package nosorae.module_basic.p16_youtube

import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import nosorae.module_basic.R
import nosorae.module_basic.databinding.ActivityYoutubeBinding
import nosorae.module_basic.p16_youtube.adapter.YoutubeVideoAdapter
import nosorae.module_basic.p16_youtube.model.YoutubeVideoDTO
import nosorae.module_basic.p16_youtube.service.YoutubeVideoService
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory

/**
 * - MotionLayout
 *      ConstraintLayout 라이브러리에 내장이 되어있는 기능
 *      레이아웃 전환과 UI 이동, 크기 조절 및 애니메이션 사용
 *      OTT 앱에서 더 자세히 나옴
 *      TODO https://developer.android.com/training/constraint-layout/motionlayout/examples?hl=ko 참고
 *      뷰트리의 ConstraintLayout 에 우클릭해서 Convert to MotionLayout 클릭 design 영역에서 움직이는 과정을 작성하면 ~scene.xml 파일에 자동으로 기록이된다.
 *      start - key~ ... - key~ - end 마다 상태를 바꿔줘서 모션을 만들 수 있다.
 *
<MotionScene>
    <Transition>
        <KeyFrameSet>
            <KeyAttribute/>
            <KeyPosition/>
            <Key ~ />
            ...
        </KeyFrameSet>
        <OnSwipe/> // 단일 <Transition>에 여러 <onSwipe> 노드가 있을 수 있으며 각 <onSwipe>는 다양한 스와이프 방향을 지정하고 사용자가 스와이프를 할 때 실행할 다양한 작업을 지정합니다.
    </Transition>

    <ConstraintSet android:id="@+id/start">
        <Constraint/>
        <Constraint/>
        <Constraint/>
        ...
    </ConstraintSet>

    <ConstraintSet android:id="@+id/start">
        <Constraint/>
        <Constraint/>
        <Constraint/>
        ...
    </ConstraintSet>
</MotionScene>
 *
 *
 * - MotionLayout 을 커스텀클래스로 만들어서 FrameLayout 이 전체영역을 잡고 있어서 뒤에 있는 RecyclerView 에 터치 되지 않는 문제를 해결하였다.
 *
 *
 *
 * - ExoPlayer
 *      구글에서 만든 라이브러리 ( 보통 이러면 안드로이드 sdk 에 내장되어 사용가능한데, 이건 별도로 github 에 배포되고 있는 오픈소스 라이브러리이다. )
 *      유튜브앱에서 사용되는 유명한 라이브러리다.
 *      오디오 동영상 재생을 쉽고 편하게 관리할 수 있다.
 *      유튜브 영상을 가져오려면 유튜브 라이브러리를 써야한다. https://developers.google.com/youtube/android/player 참고
 *      근데 이거 최신버전이 너무 오래되서 사용하기 좀 그렇다. 그래서 https://github.com/PierfrancescoSoffritti/android-youtube-player 이거 참고 비교적 최신
 *      또는 Youtube Exo act ? 여기서는 url 로 재생 가능하다.
 *      TODO https://github.com/google/ExoPlayer, https://exoplayer.dev/hello-world.html 참고
 *
 *      player = SimpleExoPlayer.Builder(it).build() 의 player 를 PlayerView.player 에 넣어준다.
        val dataSourceFactory = DefaultDataSourceFactory(it)
        // 여러 미디어소스가 있는데 오늘은 mp4 만 다루니까 ProgressiveMediaSource 로 간다.
        val mediaSource = ProgressiveMediaSource.Factory(dataSourceFactory)
        .createMediaSource(MediaItem.fromUri(Uri.parse(url))) // url 을 uri 로 변환해서 미디어소스로 만든다.
        player?.setMediaSource(mediaSource) // 미디어 소스 연결
        player?.prepare() // 데이터를 가져오기 시작
        player?.play() // 영상이 플레이되기 시작
        생명주기 신경쓸것!
        player?.pause()  // 일시 정지
        player?.release() // 플레이어 자체를 소멸시키기
 *
 *
 *
 *
 * -
 *
 * - 의문점 왜 fragment 에서의 binding 은 nullable 하게 할당할까?
 *      아! onDestroy 에서 binding 값을 null 처리하기 위해서구나!
 *      Fragment에서 View Binding을 사용할 경우 Fragment는 View보다 오래 지속되어,
 *      Fragment의 Lifecycle로 인해 메모리 누수가 발생할 수 있기 때문입니다. https://yoon-dailylife.tistory.com/57
 *      구글은 Fragment의 재사용을 위해 View들을 메모리에 보관하도록 Fragment의 동작을 변경하였습니다.
 *      그래서 onDestroy() - onDestroyView()가 호출되고 나서도 View Binding에 대한 참조를 가비지 컬렉터에서 가비지 컬렉션을 명확하게 해 줄 필요가 있습니다.
 *
 * - 프래그먼트에서 attach 되어있는 Activity 에 리스너를 다는 법
 *
 * - android:maxLines="1" android:singleLine="true" 이거 두개 뭔 차이냐
 *
 * - RecyclerView 위에 FrameLayout 이 있어서 클릭을 가져가 버리는 경우
 *      터치된 영역이 현재 보이는 FrameLayout 이 아니라면 흘려보내기
 *      TODO CustomMotionLayout 참고해서 문서를 찾아서 학습하자
 *
 * - 아니 근데 override 할 때 파라미터 타입에 ? 붙어있는데 막 떼어줘도 되는거야?? CustomMotionLayout 의 onTouchEvent 에서 그랬다.
 *
 * - MotionLayout 을 아래서 위로 스크롤하는데 안에있는 RecyclerView 도 같이 스크롤되는 문제 해결
 *      android:nestedScrollingEnabled="false"
 *
 * - RecyclerView 에 padding 속성을 주는데 스크롤 위아래 차체에서도 패딩이 생겨버리는 문제 해결
 *      android:clipToPadding="true"
 *
 * - Fragment class 가져오는 법!
 *      supportFragmentManager.fragments.find { it is YoutubePlayerFragment }?.let
 *      여기서 fragments 는 이 액티비티에 어태치 되어있는 모든 프래그먼트의 리스트이다.
 *
 *
 * - 레트로핏 객체 빌드에서 .addConverterFactory(GsonConverterFactory.create()) 달아준다면 서버의 변수명과 데이터 클래스의 변수명을 일치시켜야한다??
 *      sourceUrl 이라고 했다가 서버랑 똑같이 sources 라고 바꿨더니 나왔다.
 *
 * - RecyclerView 의 Adapter 는 뷰가 같다면 재사용이 가능하다는 것을 깨달음
 *
 *
 */
class YoutubeActivity: AppCompatActivity() {
    private lateinit var binding: ActivityYoutubeBinding
    private lateinit var videoAdapter: YoutubeVideoAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityYoutubeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportFragmentManager.beginTransaction().replace(R.id.youtube_fragment_container, YoutubePlayerFragment()).commit()
        initRecyclerView()
        getVideoList()

    }
    private fun initRecyclerView() {
        videoAdapter = YoutubeVideoAdapter(callback = { url, title ->
            // 펼치고, 비디오플레이, 타이틀
            supportFragmentManager.fragments.find { it is YoutubePlayerFragment }?.let {
                (it as YoutubePlayerFragment).playVideo(url, title)
            }

        })
        binding.youtubeRecyclerView.apply {
            adapter = videoAdapter
            layoutManager = LinearLayoutManager(this@YoutubeActivity)
        }
    }

    private fun getVideoList() {
        Log.d(LOG_TAG, "getVideoList 호출")
        val retrofit = Retrofit.Builder()
            .baseUrl("https://run.mocky.io/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        retrofit.create(YoutubeVideoService::class.java).also {
            it.listVideo()
                .enqueue(object: Callback<YoutubeVideoDTO> {
                    override fun onResponse(call: Call<YoutubeVideoDTO>, response: Response<YoutubeVideoDTO>) {
                        if (response.isSuccessful.not()) {
                            Log.d(LOG_TAG, "response fail 입니다.")
                            return
                        }

                        response.body()?.let {
                            Log.d(LOG_TAG, it.toString())
                            videoAdapter.submitList(it.videos)
                        }

                    }

                    override fun onFailure(call: Call<YoutubeVideoDTO>, t: Throwable) {
                        Log.d(LOG_TAG, "onFailure $call")
                        Log.d(LOG_TAG, "onFailure $t")
                    }
                })

        }

    }


    companion object {
        private const val LOG_TAG = "youtube"
    }
}
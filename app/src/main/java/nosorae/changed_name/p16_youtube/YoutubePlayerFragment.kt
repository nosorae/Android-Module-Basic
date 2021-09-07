package nosorae.changed_name.p16_youtube

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import nosorae.changed_name.R
import nosorae.changed_name.databinding.FragmentYoutubePlayerBinding
import nosorae.changed_name.p16_youtube.adapter.YoutubeVideoAdapter
import nosorae.changed_name.p16_youtube.model.YoutubeVideoDTO
import nosorae.changed_name.p16_youtube.service.YoutubeVideoService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.math.abs

/**
 * 의문점 왜 fragment 에서의 binding 은 nullable 하게 할당할까?
 * 아! onDestroy 에서 binding 값을 null 처리하기 위해서구나!
 * Fragment에서 View Binding을 사용할 경우 Fragment는 View보다 오래 지속되어,
 * Fragment의 Lifecycle로 인해 메모리 누수가 발생할 수 있기 때문입니다. https://yoon-dailylife.tistory.com/57
 * Fragment 의 activity 변수는 Fragment 가 attach 되어있는 액티비티를 말한다. 근데 어디에 붙어있는지는 본인은 모른다.
 */
class YoutubePlayerFragment: Fragment(R.layout.fragment_youtube_player) {

    private var binding: FragmentYoutubePlayerBinding? = null
    private lateinit var videoAdapter: YoutubeVideoAdapter

    private var player: SimpleExoPlayer? = null


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val fragmentPlayerBinding = FragmentYoutubePlayerBinding.bind(view)
        binding = fragmentPlayerBinding
        initMotionLayout(fragmentPlayerBinding)
        initRecyclerView(fragmentPlayerBinding)
        initPlayer(fragmentPlayerBinding)
        getVideoList()



    }
    private fun initMotionLayout(fragmentPlayerBinding: FragmentYoutubePlayerBinding) {
        fragmentPlayerBinding.fragmentYoutubePlayerMotionLayout.setTransitionListener(
            object: MotionLayout.TransitionListener {
                override fun onTransitionStarted(p0: MotionLayout?, p1: Int, p2: Int) {}

                override fun onTransitionChange(motionLayout: MotionLayout?, start: Int, endId: Int, progress: Float) {
                    binding?.let {
                        // Fragment 의 activity 변수는 Fragment 가 attach 되어있는 액티비티를 말한다. 근데 어디에 붙어있는지는 본인은 모른다.
                        // 원래는 null 처리도 해줘야한다.
                        (activity as YoutubeActivity).also { youtubeActivity ->
                            youtubeActivity.findViewById<MotionLayout>(R.id.youtube_motion_layout).progress = abs(progress)
                        }
                    }
                }

                override fun onTransitionCompleted(p0: MotionLayout?, p1: Int) {}

                override fun onTransitionTrigger(p0: MotionLayout?, p1: Int, p2: Boolean, p3: Float) {}
            }
        )
    }
    private fun initRecyclerView(fragmentPlayerBinding: FragmentYoutubePlayerBinding) {
        videoAdapter = YoutubeVideoAdapter(callback = { url, title ->
            // 펼치고, 비디오플레이, 타이틀
            playVideo(url, title)
        })
        fragmentPlayerBinding.fragmentYoutubePlayerRecyclerView.apply {
            adapter = videoAdapter
            layoutManager = LinearLayoutManager(context)
        }

    }
    private fun initPlayer(fragmentPlayerBinding: FragmentYoutubePlayerBinding) {
        context?.let {
            player = SimpleExoPlayer.Builder(it).build()
        }
        fragmentPlayerBinding.fragmentYoutubePlayerPlayerView.player = player
        binding?.let {
            player?.addListener(object: Player.Listener {
                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    super.onIsPlayingChanged(isPlaying)
                    // 플레이 상태가 바뀌면 호출되는 함수 isPlaying 으로 플레이 상태인지 알 수 있다.
                    if (isPlaying) {
                        // 플레이 상태니까 일시정지 버튼으로 바꿔준다.
                        it.fragmentYoutubePlayerButtonBottomControl.setImageResource(R.drawable.ic_youtube_pause)
                    } else {
                        // 플레이상태가 아니니까 재생버튼으로 바꿔준다.
                        it.fragmentYoutubePlayerButtonBottomControl.setImageResource(R.drawable.ic_youtube_play)

                    }

                }

            })
        }

    }

    private fun initControlButton(fragmentPlayerBinding: FragmentYoutubePlayerBinding) {
        fragmentPlayerBinding.fragmentYoutubePlayerButtonBottomControl.setOnClickListener {
            val player = this.player ?: return@setOnClickListener
            // 이미지 리소스 바뀌는 것은 onIsPlayingChanged 에서 자동으로 처리된다.
            if (player.isPlaying) {
                player.pause()
            } else {
                player.play()
            }
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

    fun playVideo(url: String, title: String) {
        context?.let {

            //Prior to ExoPlayer 2.12, the player needed to be given a MediaSource rather than media items.
            // From 2.12 onwards, the player converts media items to the MediaSource instances that it needs internally.
            // 그래서 아래코드 주석하고 문서에 나와있는 대로 바로 MediaItem 을 주었다.
//            val dataSourceFactory = DefaultDataSourceFactory(it)
//            // 여러 미디어소스가 있는데 오늘은 mp4 만 다루니까 ProgressiveMediaSource 로 간다.
//            val mediaSource = ProgressiveMediaSource.Factory(dataSourceFactory)
//                .createMediaSource(MediaItem.fromUri(Uri.parse(url))) // url 을 uri 로 변환해서 미디어소스로 만든다.
            player?.setMediaItem(MediaItem.fromUri(Uri.parse(url))) // 미디어 소스 연결
            player?.prepare() // 데이터를 가져오기 시작
            player?.play() // 영상이 플레이되기 시작

        }
        // 펼치고, 비디오플레이, 타이틀
        binding?.let {
            it.fragmentYoutubePlayerMotionLayout.transitionToEnd() // 아이템 눌렀으니 촥 열리면서 end 상태로 만든다.
            it.fragmentYoutubePlayerTextViewBottomTitle.text = title

        }

    }

    override fun onStop() {
        super.onStop()
        player?.pause()  // 일시 정지
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
        player?.release() // 플레이어 자체를 소멸시키기
    }

    companion object {
        private const val LOG_TAG = "youtube"
    }
}
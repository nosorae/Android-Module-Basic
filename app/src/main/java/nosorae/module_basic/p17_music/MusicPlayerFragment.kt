package nosorae.module_basic.p17_music

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.SeekBar
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import nosorae.module_basic.R
import nosorae.module_basic.databinding.FragmentMusicPlayerBinding
import nosorae.module_basic.p17_music.adapter.MusicRecyclerAdapter
import nosorae.module_basic.p17_music.model.MusicDTO
import nosorae.module_basic.p17_music.model.MusicModel
import nosorae.module_basic.p17_music.model.mapper
import nosorae.module_basic.p17_music.service.MusicService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class MusicPlayerFragment : Fragment(R.layout.fragment_music_player) {

    private var model: MusicPlayerModel = MusicPlayerModel()
    private var binding: FragmentMusicPlayerBinding? = null
    private lateinit var musicAdapter: MusicRecyclerAdapter
    private var player: SimpleExoPlayer? = null

    private val updateSeekRunnable: Runnable = Runnable {
        updateSeek()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val fragmentPlayerBinding = FragmentMusicPlayerBinding.bind(view)
        binding = fragmentPlayerBinding
        initPlayView(fragmentPlayerBinding)
        initPlaylistButton(fragmentPlayerBinding)
        initPlayControlButtons(fragmentPlayerBinding)
        initSeekBar(fragmentPlayerBinding)
        initRecyclerView(fragmentPlayerBinding)
        getMusicListFromServer()

    }


    private fun initPlayView(fragmentPlayerBinding: FragmentMusicPlayerBinding) {
        context?.let {
            player = SimpleExoPlayer.Builder(it).build()
        }

        fragmentPlayerBinding.fragmentMusicPlayerExoPlayer.player = player
        binding?.let { binding ->
            player?.addListener(object : Player.Listener {
                // 재생상태가 바뀔 때 콜백으로 내려오는 함수
                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    super.onIsPlayingChanged(isPlaying)
                    if (isPlaying) {
                        binding.fragmentMusicPlayerImagePlayControl.setImageResource(R.drawable.ic_music_pause)
                    } else {
                        binding.fragmentMusicPlayerImagePlayControl.setImageResource(R.drawable.ic_music_play)
                    }
                }

                override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                    super.onMediaItemTransition(mediaItem, reason)
                    val newIndex = mediaItem?.mediaId
                        ?: return // mediaId 는 MusicModel 의 id 에 해당한다. // .setMediaId(musicModel.id.toString())
                    model.currentPosition = newIndex.toInt()
                    updatePlayerView(model.currentMusicModel())
                    musicAdapter.submitList(model.getAdapterModels())
                    // DiffUtil 을 통해서 UI 업데이트 기존 데이터클래스 copy 해서 isPlaying 값만 바꿔줬기 때문에 전체 리스트를 다시 그려주는 게 아니라 원래 isPlaying 이 바뀐 부분만 찾아서 리프레시 해줘서 비용이 적게든다.

                }

                // 재생, 재생끝, 버퍼링상태다 이런 상태가 내려올 때 seekbar 를 업데이트하겠다.
                override fun onPlaybackStateChanged(state: Int) {
                    super.onPlaybackStateChanged(state)
                    updateSeek()

                }
            })

        }
    }

    // TODO 잘 이해 해봐
    private fun updateSeek() {
        val player = this.player ?: return
        val duration = if (player.duration >= 0) player.duration else 0
        val position = player.currentPosition
        updateSeekUi(duration, position)

        val state = player.playbackState

        // updateSeekRunnable 를 통해서 들어온 게 아니라 onPlaybackStateChanged 이걸로 들어왔을 수도 있으니 1초대기하는 코드를 지우고 다시 처음부터 Runnable 을 시작해준다?
        view?.removeCallbacks(updateSeekRunnable)

        // STATE_IDLE 의 뜻은 The player does not have any media to play.
        if (state != Player.STATE_IDLE && state != Player.STATE_ENDED) {
            // 여기서 view 는  root view for the fragment's layout
            view?.postDelayed(updateSeekRunnable, 1000)
        }

    }

    private fun updateSeekUi(duration: Long, position: Long) {
        binding?.let { binding ->
            binding.fragmentMusicPlayerSeekBarPlayer.max = (duration / 1000).toInt()
            binding.fragmentMusicPlayerSeekBarPlayer.progress = (position / 1000).toInt()
            binding.fragmentMusicPlayerSeekBarPlaylist.max = (duration / 1000).toInt()
            binding.fragmentMusicPlayerSeekBarPlaylist.progress = (position / 1000).toInt()


            binding.fragmentMusicPlayerTextPlayTime.text = String.format(
                "%02d:$02d",
                TimeUnit.MINUTES.convert(position, TimeUnit.MILLISECONDS).toInt(),
                (position / 1000).toInt() % 60
            )
            binding.fragmentMusicPlayerTextTotalTime.text = String.format(
                "%02d:$02d",
                TimeUnit.MINUTES.convert(duration, TimeUnit.MILLISECONDS).toInt(),
                (duration / 1000).toInt() % 60
            )

        }
    }

    private fun updatePlayerView(currentMusicModel: MusicModel?) {
        currentMusicModel ?: return

        currentMusicModel.id
        binding?.let { binding ->
            binding.fragmentMusicPlayerTextMusicTitle.text = currentMusicModel.track
            binding.fragmentMusicPlayerTextSinger.text = currentMusicModel.artist
            Glide
                .with(binding.fragmentMusicPlayerImageCover.context)
                .load(currentMusicModel.coverUrl)
                .into(binding.fragmentMusicPlayerImageCover)
        }

    }

    private fun initPlaylistButton(playerBinding: FragmentMusicPlayerBinding) {
        playerBinding.fragmentMusicPlayerImagePlaylist.setOnClickListener {
            //  만약에 서버에서 데이터가 다 불려오지 않은 상태일 떄에 대한 전환하지 않는 예외처리
            if (model.currentPosition == -1) {
                return@setOnClickListener
            }

            playerBinding.fragmentMusicPlayerViewGroupPlayer.isVisible =
                model.isWatchingPlayListView
            playerBinding.fragmentMusicPlayerViewGroupPlaylist.isVisible =
                model.isWatchingPlayListView.not()

            model.isWatchingPlayListView = !(model.isWatchingPlayListView)


        }

    }

    private fun initPlayControlButtons(fragmentPlayerBinding: FragmentMusicPlayerBinding) {
        fragmentPlayerBinding.fragmentMusicPlayerImagePlayControl.setOnClickListener {
            val player = this.player ?: return@setOnClickListener
            if (player.isPlaying) {
                player.pause()
            } else {
                player.play()
            }
        }

        fragmentPlayerBinding.fragmentMusicPlayerImageSkipNext.setOnClickListener {
            val nextMusic = model.nextMusic() ?: return@setOnClickListener
            playMusic(nextMusic)

        }
        fragmentPlayerBinding.fragmentMusicPlayerImageSkipPrev.setOnClickListener {
            val prevMusic = model.prevMusic() ?: return@setOnClickListener
            playMusic(prevMusic)
        }


    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initSeekBar(fragmentPlayerBinding: FragmentMusicPlayerBinding) {
        // false 를 주어서 seekbar 에 대한 터치를 무시한다고??
        fragmentPlayerBinding.fragmentMusicPlayerSeekBarPlaylist.setOnTouchListener { _, _ ->  false }
        fragmentPlayerBinding.fragmentMusicPlayerSeekBarPlayer.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {}

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                player?.seekTo((seekBar.progress.toLong()) * 1000)

            }
        })
    }


    private fun initRecyclerView(playerBinding: FragmentMusicPlayerBinding) {
        musicAdapter = MusicRecyclerAdapter { musicModel ->
            // 음악 재생
            playMusic(musicModel)
        }
        playerBinding.fragmentMusicPlayerRecyclerView.apply {
            adapter = musicAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    private fun getMusicListFromServer() {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://run.mocky.io/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        retrofit.create(MusicService::class.java)
            .also {
                it.listMusics()
                    .enqueue(object : Callback<MusicDTO> {
                        override fun onResponse(
                            call: Call<MusicDTO>,
                            response: Response<MusicDTO>
                        ) {
                            if (response.isSuccessful.not()) {
                                Log.d(LOG_TAG, "response fail")
                                return
                            }
                            Log.d(LOG_TAG, "${response.body()}")
                            response.body()?.let { musicDTO ->
                                model = musicDTO.mapper()
                                setMusicList(model.getAdapterModels())
                                musicAdapter.submitList(model.getAdapterModels())
                            }
                        }

                        override fun onFailure(call: Call<MusicDTO>, t: Throwable) {
                            Log.d(LOG_TAG, "onFailure $t")
                        }
                    })
            }

    }

    private fun setMusicList(list: List<MusicModel>) {
        context?.let {
            // 플레이어에 음악 여러개 대기시켜놓기????
            player?.addMediaItems(list.map { musicModel ->
                //MediaItem.fromUri(Uri.parse(musicModel.streamUrl))
                // 미디어아이템에는 미디어아이디, 태그 두 가지를 기정할 수 있다.
                MediaItem.Builder()
                    .setMediaId(musicModel.id.toString())
                    .setUri(musicModel.streamUrl)
                    .build()
            })
            player?.prepare()

        }

    }

    private fun playMusic(musicModel: MusicModel) {
        // 플레이어는 MediaItem 형식으로 다 가지고 있고  model 은 MusicModel 형태로 다 가지고 있다. id 를 공유한다.
        model.updateCurrentPosition(musicModel)
        player?.seekTo(model.currentPosition, 0)
        player?.play()

    }

    override fun onStop() {
        super.onStop()
        player?.pause()
        view?.removeCallbacks(updateSeekRunnable)
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
        player?.release()
        view?.removeCallbacks(updateSeekRunnable)
    }

    companion object {
        // 이렇게 하는 이유는 액티비티에서 인자를 넘겨줄때 편하기 때문이다. .apply { arguments.apply{}} 로 접근할 수 있다
        fun newInstance(): MusicPlayerFragment {
            return MusicPlayerFragment()
        }

        private const val LOG_TAG = "music"
    }
}
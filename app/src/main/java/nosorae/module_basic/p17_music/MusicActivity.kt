package nosorae.module_basic.p17_music

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import nosorae.module_basic.R
import nosorae.module_basic.databinding.ActivityMusicBinding


/**
 * - Exoplayer 오디오재생 및 custom controller
 *      player?.addMediaItems( MediaItem 리스트 ) 로 넣어주고
 *      player?.seekTo(model.currentPosition, 0) 로 원하는 MediaItem 설정
 *      player?.seekTo(seekBar.progress.toLong() * 1000) 으로 원하는 플레이 위치 설정
 *      player?.duration 현재 MediaItem 의 전체길이 밀리세컨드
 *      player?.currentPosition 현재 재생중인 포지션 밀리세컨드
 *      player?.playbackState
 *      https://exoplayer.dev/hello-world.html 여기에서 더 많은 기능을 찾을 수 있다.
 *      여기서는 exoplayer 의 alpha 값을 0으로 두고 레이아웃을 직접 구현했는데
 *      사실 useController? 를 true 로 두고 레이아웃파일만 다른걸로 교체해서 만드는 방법도 있다고 한다. 그럼 좀 더 쉽고 정확하게 구현할 수 있다.
 *
 * - TimeUtil 사용해서 밀리세컨드
 *
 * - TODO SeekBar 업데이트 부분 postDelay 와 Runnable 을 활용한 재귀 복습습
 *
 * - DiffUtil 을 이 코스에서 최초로 재대로 사용해보았다.
 *      이때까지는 원래 리스트에서 값만 바꿔주었지 그러면 같은 주소값을 가지고 있으니 areContentsTheSame 에서 같은 아이템으로 매치되어 true 로 반환되고 업데이트할 게 없다고 판한한다.
 *      그래서 이번에든 copy 를 통해서 새로운 아이템을 넣어주어서 다르다고 인식하고 업데이트를 해주었다.
 *
 * - Seekbar custom
 *       동그리미를 thumb 라고 하는데 android:clickable="false" 만으로도 없앨 수 있다고 한다.
 *
 * - androidx.constraintlayout.widget.Group
 *       app:constraint_referenced_ids="fragment_music_player_text_music_title, fragment_music_player_text_singer,
        fragment_music_player_card_view, fragment_music_player_bottom_background, fragment_music_player_seek_bar_player,
        fragment_music_player_text_play_time, fragment_music_player_text_total_time"
        이런식으로 reference_ids 로 묶인 모든 뷰의 속성을 한번에 줄 수 있다. 여기서는 visibility 를 단체로 줄 수 있었다.
 *
 *
 *
 *
 * -  android:translationY="50dp" 뷰를 원위치에 그리고 아래로 당기는 속성
 *      주의할 점은 다른 뷰를 저 속성 쓴 뷰에 제약을 걸면 저 translationY 를 먹이지 않은 원래위치에 제약이 먹는다는 것이다.
 *
 * - app:use_controller="false" 플레이어뷰가 나오더라도 컨트롤러 뷰는 사용하기 싫을 때 사용
 *
 * - android:maxHeight="4dp"  android:minHeight="4dp" 늘어나지도 줄어들지도 않게 만들기 근데 이거 height 바꾸는 거랑 무슨 차이??
 *
 *
 * - SerializedName("") 에 적힌 것이 api 에 써있는 변수명이다.
 *      https://run.mocky.io/v3/60eae652-2471-4e03-a243-59b788968d7a
 *
 * - 내가 만든 클래스에 확장함수로 다른 모델클래스로 맵핑해주는 기능을 구현하였다. MusicModelMapper 참고
 *
 * - DiffUtil 을 통해서 UI 업데이트 기존 데이터클래스 copy 해서 isPlaying 값만 바꿔줬기 때문에 전체 리스트를 다시 그려주는 게 아니라 원래 isPlaying 이 바뀐 부분만 찾아서 리프레시 해줘서 비용이 적게든다.
 *
 * - 생명주기 신경쓰는 거 잊지마라
 */

class MusicActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMusicBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMusicBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportFragmentManager.beginTransaction()
            .replace(R.id.music_fragment_container, MusicPlayerFragment.newInstance()).commit()


    }
}











package nosorae.module_basic.p6_pomodoro

import android.annotation.SuppressLint
import android.media.SoundPool
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import nosorae.module_basic.R

/**
 * CountDownTimer 라는 안드로이드 내장함수로 타이머 구현
 * -> TODO https://developer.android.com/reference/android/os/CountDownTimer 참고
 * -> object : CountDownTimer(30000, 1000) {} 이렇게 생성한다. 그리고 {}안에 onTick 과 onFinish 함수를 구현한다. 그리고 start 를 호출한다. cancel 도 있다.
 * Sound Pool 로 효과음 삽입 (오디오 사운드를 재생하고 관리하는 클래스)
 * -> 미디어 파일들은 보통 메모리 비용이 많이 든다. 따라서 사용하지 않는 타이밍에 해제를 해주어야한다. soundPool.release()
 * -> AlertDialog 와 마찬가지로 Builder 패턴으로 만들 수 있다.
 * -> 오디오 파일을 메모리에 로드하고 비교적 빠르게 재생할 수 있게 도와준다. 대신 짧은 영상만 재생할 수 있게 제약이 걸려있다.
 * -> TODO https://developer.android.com/reference/android/media/SoundPool 참고
 * SeekBar 사용 : PrograssBar 에 draggable thumb 인 extension 이다.
 * Constraint Layout 에서 Chain 의 헤드는 가로기준 맨 왼쪽 그리고 세로기준 맨 위쪽에 위치한다.
 * -> 디폴트 속성은 chain spread
 * "%02d".format(progress) 간단하게 0을 채우는 방법
 * 여러 함수가 받는 값은 단위를 통일시켜놓는 게 가독성을 높인다.
 * seekBar ?: 이것의 의미는 좌측의 값이 null 이면 우측의 값을 리턴한다는 말이다. 우측에는 return 도 써줄 수 있다. 코틀린에서는 expression 도 리턴할 수 있기 때문이다.
 *
 * let 을 이용한 null 체크크 *
 * 기존의 윈도우가 하얀색이라 배경 빨간색이어도 하얀색 나왔다가 빨간색이 나온다. 그래서 윈도우 색을 바꿔야한다.
 * -> <item name="android:windowBackground">@color/pomodoro_red</item>
 * 그리고 상태바도 흰색이라 배경과 맞춰주었다.
 * <item name="android:statusBarColor">@color/pomodoro_red</item>
 * app:layout_constraintBaseline_toBaselineOf="@+id/pomodoro_text_view_minutes" 로 숫자 위치를 맞춰주었다.
 * 벡터를 쓰는 기기마다 해상도가 다르다. 즉 다중밀도에 대비하기 위해서 다양한 이미지셋을 준비해서 프로젝트에 넣어야했다.
 * -> 그러다보니 이미지 공수비용도 높고 apk 파일 용량도 커진다. 그것을 해소하기 위해서 벡터 그래픽이 나온 것이다.
 * -> 실제로는 코드가 들어가있다.
 * -> 하지만 실제로 그리는데에는 복잡하다면 높은 cpu 비용이 들 수 있기 때문에 간단한 이미지를 쓰는 것을 추천한다.
 * -> 그래서 최대 200*200 이하로 벡터 드로어블을 사용할 것을 권장하고 있다. 그 이상이라면 차라리 기존 여러 에셋을 가져오는 방식을 채택
 * SeekBar 처음 써본 속성 세가지
 * ->android:tickMark="" // 정수구간으로 나뉘는 부분마다 주어진 드로어블을 그린다.
 * -> android:progressDrawable="@color/transparent" // 진행도를 투명색으로 주어서 보이지 않게 하였다.
 * -> android:thumb="@drawable/ic_pomodoro_thumb" //
 * .svg 파일 가져오는 법 File - New - Vector Asset - Local file
 * 이미지 파일 접두어는 회사마다 다르지만 보통 크면 img~ 작으면 ic~ 라고 한다.
 * Missing contentDescription attribute on image 여기서 contentDescription
 * -> contentDescription 은 평소에는 쓸 일이 잘 없는데, 접근성, 화면을 읽어주는 기능인 토크백, 그 때
 * -> contentDescription 통해서 이미지를 설명해주는데 그러기 위해 필요한 속성 구글에서는 이를 넣는 것을 권장한다.
 *
 * TODO thumb 가 프로그래스바의 범위를 벗어난 곳에 있다.. (처음 시작할 때 왼쪽으로 그렇다.)
 */
class PomodoroActivity: AppCompatActivity() {

    private val remainMinutesTextView: TextView by lazy {
        findViewById<TextView>(R.id.pomodoro_text_view_minutes)
    }
    private val seekBar: SeekBar by lazy {
        findViewById<SeekBar>(R.id.pomodoro_seek_bar)
    }
    private val remainSecondsTextView: TextView by lazy {
        findViewById<TextView>(R.id.pomodoro_text_view_seconds)
    }

    private var currentCountDownTimer: CountDownTimer? = null

    private val soundPool = SoundPool.Builder().build()
    private var tickingSoundId: Int? = null
    private var bellSoundId: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pomodoro)

        bindViews()
        initSounds()
    }

    override fun onResume() {
        super.onResume()
        soundPool.autoResume()
    }

    override fun onPause() {
        super.onPause()
        // pause 함수에 id 인자 넣으면 그 하나만 pause 되는데, autoPause 하면 모드 pause 된다.
        soundPool.autoPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        soundPool.release()
    }

    private fun initSounds() {
        // 함수 찾아가서 읽어보는 습관을 들이자
        tickingSoundId = soundPool.load(this, R.raw.timer_ticking, 1)
        bellSoundId = soundPool.load(this, R.raw.timer_bell, 1)

    }
    private fun bindViews() {
        seekBar.setOnSeekBarChangeListener(
            object :SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                    if (fromUser) {
                        updateRemainingTimes(progress * 60 * 1000L)
                    }
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {
                    stopCountDown()
                }

                override fun onStopTrackingTouch(seekBar: SeekBar?) {
                    // seekBar 가 null 인 경우는 거의 없겠지만 그렇다면 실행하지 않는 게 맞기 때문에 예외처리 해준다.
                    seekBar ?: return

                    if (seekBar.progress == 0) {
                        stopCountDown()
                    } else {
                        startCountDown()
                    }
                }
            }
        )
    }
    private fun startCountDown() {
        currentCountDownTimer = createCountDownTimer(seekBar.progress * 60 * 1000L)
        // nullable 로 만들었고 코드 중간에 언제든 null 이 될 수 있으니 ? 처리 해줘!
        currentCountDownTimer?.start()

        // nullable 한 변수를 이렇게 null 체크 하는구나
        tickingSoundId?.let { soundId ->
            // 화면 밖으로 나가도 계속 play 되므로 생명주기를 이용해서 껐다가 다시 플레이하는 과정이 필요하다.
            soundPool.play(soundId, 1f, 1f, 0, -1, 1f)
        }
    }

    private fun createCountDownTimer(initialMillis: Long) =
        object : CountDownTimer(initialMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                // 1초마다 한번씩 호출되게된다. 인자는 남은 시간을 밀리세컨드로 준다.
                updateRemainingTimes(millisUntilFinished)
                updateSeekBar(millisUntilFinished)
            }

            override fun onFinish() {
                completeCountDown()

            }
        }

    private fun completeCountDown() {
        // 0으로 되는 건 onTick 만으로도 만족되는 기능아닌가?
        updateRemainingTimes(0)
        updateSeekBar(0)

        soundPool.autoPause()
        bellSoundId?.let { soundId ->
            soundPool.play(soundId, 1f, 1f, 0, 0, 1f)
        }
    }

    private fun stopCountDown() {
        currentCountDownTimer?.cancel()
        currentCountDownTimer = null // 의미상 더 깔끔하게 만드는 코드
        soundPool.autoPause()
    }

    @SuppressLint("SetTextI18n")
    private fun updateRemainingTimes(remainMillis: Long) {
        val remainSeconds = remainMillis / 1000
        //Uses this string as a format string and returns a string obtained by substituting the specified arguments, using the default locale.
        remainMinutesTextView.text = "%02d`".format(remainSeconds / 60)
        remainSecondsTextView.text = "%02d".format(remainSeconds % 60)
    }

    private fun updateSeekBar(remainMillis: Long) {
        // 이 때도 onProgressChanged 함수가 호출된다! 그래서 fromUser 변수를 활용해야한다.
        seekBar.progress = (remainMillis / 1000 / 60).toInt()
    }
}
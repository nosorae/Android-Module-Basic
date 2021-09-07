package nosorae.changed_name.p7_recorder

import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import nosorae.changed_name.R

/**
 * 마이크 사용해서 녹음
 * -> permission 필요, MediaRecorder 사용
 * -> TODO https://developer.android.com/training/permissions/requesting#normal-dangerous 참고
 * -> TODO https://developer.android.com/guide/topics/media/mediarecorder 참고
 * -> TODO https://developer.android.com/reference/android/media/MediaRecorder 참고
 * -> TODO 미디어 플레이어는 https://developer.android.com/reference/android/media/MediaPlayer 참고
 * -> 코덱이란 인코더와 디코더를 합친 말, 음성파일이 크니까 작게 만드는 게 인코딩, 여기서는 ANR_NB 방식으로 인코딩
 * -> 포맷이란 압축된 데이터를 차곡차곡 정리하는 컨테이너 역할 이걸 디코더가 꺼내서 다시 해석해서 오디오로 재생하는 과정
 * -> OutputFormat 과 AudioEncoder 는 서로 호환 되는 것이 있으니 Audio support 를 보고 두 개를 설정해준다.
 * -> 녹음 전 -> 녹음 중 -> 녹음 후 -> 재생 중
 * -> 녹음 후나 재생 중 상태에서는 리셋버튼으로 녹음 전 상태로 돌아갈 수 있고, 재생중에 일시정지버튼으로 녹음 후 상태로 돌아갈 수 있다.
 * -> externalCacheDir?.absolutePath 변수로 외부 스토리지 캐시 디렉토리에 접근한다.
 * 녹음한 음성 재생
 * 음성을 시각적으로 확인한다.
 * -> CustomView 필요
 * 상태에 따라 버튼 상태가 달라야해서 enum class 로 상태값을 지정해놓았다.
 *  뷰를 상속아서 커스텀뷰 만들기 나만의 Button, TextView, View 등을 만들어서 클래스로 관리한다?
 * -> TODO https://developer.android.com/training/custom-views/create-view 참고
 * -> TODO https://developer.android.com/training/custom-views/custom-drawing 참고
 * View Class 만들기 View 상속 받아서 그림 그리기
 * -> Paint? 개체를 미리 만드는 것은 중요한 최적화 작업입니다. 뷰를 다시 그리는 경우는 매우 자주 발생하며 다수의 그리기 객체는 많은 비용을 들여 초기화해야 합니다.
 * -> onDraw() 메서드 내에서 그리기 객체를 만들면 성능이 크게 저하되고 UI가 느리게 표시될 수 있습니다.
 * -> 사이즈를 정확히 구해주는 게 중요하다. onSizeChanged
 * AppCompat 은 이전 안드로이드 버전에 대한 호환성을 위해서 AppComapt 으로 기존 클래스를 래핑한 클래스이다.
 * 그럼 xml 에서는 왜 AppCompat 을 안쓰는가?
 * ->  xml 내부에 정의된 ~View 를 자동으로 AppCompat 에 매핑할 수 있는 클래스가 존재할 경우 자동으로 매핑 시켜주는 기능이 프로젝트에 설정되어있기 때문이다.
 *
 * 하나의 String 을 + 연산자로 여러 자료형을 합쳐서 만들기 보다 ${} 를 사용해서 "" 안에 넣어주는 게 더 코틀린스러운 코드이다.
 * resetButton.isEnabled = value == RecorderState.AFTER_RECORDING || value == RecorderState.ON_PLAYING 센스
 * var drawingAmplitudes: List<Int> = (0..10).map { Random.nextInt(Short.MAX_VALUE.toInt())} 초기화 코드 센스
 * fps frame per second
 *
 * var onRequestCurrentAmplitude: (() -> Int)? = null 스레드로 일정시간마다 onRequestCurrentAmplitude?.invoke() 를 해주면
 * -> 다른 함수에 onRequestCurrentAmplitude 를 정의해둔 함수를 호출해서 값을 받아올 수 있다.
 * 레코드 버튼의 재사용성을 높이는 방법을 소개하며 코드로 xml 이 아닌 소스코드에서 background 속성을 주었다.
 *
 */
class RecorderActivity : AppCompatActivity() {

    private val recordTimeTextView: CountUpTextView by lazy {
        findViewById(R.id.recorder_text_view_record_time)
    }
    private val soundVisualizerView: SoundVisualizerView by lazy {
        findViewById(R.id.recorder_sound_visualizer_view)
    }
    private val recorderButton: RecorderButton by lazy {
        findViewById(R.id.recorder_button)
    }
    private val resetButton: Button by lazy {
        findViewById(R.id.recorder_button_reset)
    }

    private val requiredPermissions = arrayOf(android.Manifest.permission.RECORD_AUDIO)

    private var state = RecorderState.BEFORE_RECORDING
        set(value) {
            // setter? 라고 부른다. State 를 새로 할당할 때마다  이 블록이 호출된다. value 가 새로 할당된 값이다.
            field = value
            // 녹음을 끝내고 플레이 대기 상터거나, 플레이중일 때만 리셋버튼을 허용하겠다는 말이다.
            resetButton.isEnabled = (value == RecorderState.AFTER_RECORDING) ||
                    (value == RecorderState.ON_PLAYING)

            recorderButton.updateIconWithState(value)
        }

    // 미디어 관련은 메모리 코스트가 크므로 사용하지 않을 때는 null 로 사용 해제해주는 것을 잊지 말자
    private var recorder: MediaRecorder? = null
    private val recordingFilePath: String by lazy {
        "${externalCacheDir?.absolutePath}/recording.3gp"
    }

    private var player: MediaPlayer? = null




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recorder)
        // 앱을 시작하자마자 권한을 요청한다.
        requestAudioPermission()

        initViews()
        bindViews()
        initVariables()

    }
    private fun bindViews() {
        // 여기서 maxAmplitude 를 전달해준다.
        soundVisualizerView.onRequestCurrentAmplitude = {
            recorder?.maxAmplitude ?: 0
        }

        recorderButton.setOnClickListener {
            when(state) {
                RecorderState.BEFORE_RECORDING -> {
                    startRecording()
                }
                RecorderState.ON_RECORDING -> {
                    stopRecording()
                }
                RecorderState.AFTER_RECORDING -> {
                    startPlaying()
                }
                RecorderState.ON_PLAYING -> {
                    stopPlaying()
                }
            }
        }

        resetButton.setOnClickListener {
            stopPlaying()
            soundVisualizerView.clearVisualization()
            recordTimeTextView.clearCountTime()
            state = RecorderState.BEFORE_RECORDING
        }
    }

    // 처음에 리셋버튼 활성화를 막기 위해
    private fun initVariables() {
        state = RecorderState.BEFORE_RECORDING
    }

    private fun requestAudioPermission() {
        requestPermissions(requiredPermissions, REQUEST_RECORD_AUDIO_PERMISSION)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        val audioRecordPermissionGranted = requestCode == REQUEST_RECORD_AUDIO_PERMISSION &&
                // firstOrNull 은 첫번째 원소를 가져오거나 없으면 null 을 가져오는 함수, 여기서는 권한 하나니까 이걸 사용
                grantResults.firstOrNull() == PackageManager.PERMISSION_GRANTED
        if (audioRecordPermissionGranted.not()) {
            // 거절시 교육용 메세지를 띄울 수도 있지만 여기서는 앱을 종료시킨다.
            finish()
        }

    }

    private fun initViews() {
        recorderButton.updateIconWithState(state)
    }

    private fun startRecording() {
        recorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
            // 따로 저장하지 않을거라서 캐시에 저장
            // 나중에 다른 곳에 저장할 때는 내부 저장소는 녹음 파일이 얼마나 커질지 모르니 충분한 공간을 제공하지 못할 수 있음에 주의
            // 그러니 여기서도 외부 저장소의 캐시 디렉토리에 접근해서 임시적으로 녹음파일을 저장하되 이 앱이 지워지거나
            // 안드로이드 기기 내에서 용량 확보할 때쯤에는 캐시 디렉토리에 있는 파일은 쉽게 날라갈 수 있기때문에 일단 거기에 쓰는 걸로 진행한다.
            setOutputFile(recordingFilePath)
            prepare()
        }
        recorder?.start()

        soundVisualizerView.startVisualizing(false)
        recordTimeTextView.startCountUp()
        state = RecorderState.ON_RECORDING
    }

    private fun stopRecording() {
        recorder?.run {
            stop()
            release()
        }
        recorder = null
        soundVisualizerView.stopVisualizing()
        recordTimeTextView.stopCountUp()
        state = RecorderState.AFTER_RECORDING
    }

    private fun startPlaying() {
        player = MediaPlayer().apply {
            setDataSource(recordingFilePath)
            // 스트리밍은 네트워크로 받아와야해서 UI 블로킹을 하지 않기 위해서 prepareAsync() 를 사용한다.
            prepare()
        }
        player?.setOnCompletionListener {
            stopPlaying()
            state = RecorderState.AFTER_RECORDING
        }
        player?.start()
        soundVisualizerView.startVisualizing(true)
        recordTimeTextView.startCountUp()
        state = RecorderState.ON_PLAYING
    }

    private fun stopPlaying() {
        // 문서를 보면 stop 없이 release 만 해줘도 End 상태에 도달할 수 있다.
        player?.release()
        soundVisualizerView.stopVisualizing()
        state = RecorderState.AFTER_RECORDING
    }


    companion object {
        private const val REQUEST_RECORD_AUDIO_PERMISSION = 201
    }
}
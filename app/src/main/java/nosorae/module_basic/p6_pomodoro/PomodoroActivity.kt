package nosorae.module_basic.p6_pomodoro

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import nosorae.module_basic.R

/**
 * CountDownTimer 라는 안드로이드 내장함수로 타이머 구현
 * Sound Pool 로 효과음 삽입
 * SeekBar 사용 : PrograssBar 에 draggable thumb 인 extension 이다.
 * Constraint Layout 에서 Chain 의 헤드는 가로기준 맨 왼쪽 그리고 세로기준 맨 위쪽에 위치한다.
 * -> 디폴트 속성은 chain spread
 * "%02d".format(progress) 간단하게 0을 채우는 방법
 */
class PomodoroActivity: AppCompatActivity() {

    private val remainMinutesTextView: TextView by lazy {
        findViewById<TextView>(R.id.pomodoro_text_view_minutes)
    }
    private val seekBar: SeekBar by lazy {
        findViewById<SeekBar>(R.id.pomodoro_seek_bar)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pomodoro)

        bindViews()

    }
    private fun bindViews() {
        seekBar.setOnSeekBarChangeListener(
            object :SeekBar.OnSeekBarChangeListener {
                @SuppressLint("SetTextI18n")
                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                    //Uses this string as a format string and returns a string obtained by substituting the specified arguments, using the default locale.
                    remainMinutesTextView.text = "%02d".format(progress)

                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {

                }

                override fun onStopTrackingTouch(seekBar: SeekBar?) {

                }
            }
        )
    }
}
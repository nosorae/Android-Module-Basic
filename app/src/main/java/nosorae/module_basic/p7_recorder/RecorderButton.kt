package nosorae.module_basic.p7_recorder

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageButton
import nosorae.module_basic.R

// 커스텀 뷰를 클래스로 관리하기
class RecorderButton(
    context: Context,
    attrs: AttributeSet
    ): AppCompatImageButton(context, attrs) {

    init {
        setBackgroundResource(R.drawable.recorder_shape_oval_button)
    }
        fun updateIconWithState(state: RecorderState) {
            when(state) {
                RecorderState.BEFORE_RECORDING -> {
                    setImageResource(R.drawable.ic_recorder_record)
                }
                RecorderState.ON_RECORDING -> {
                    setImageResource(R.drawable.ic_recorder_stop)
                }
                RecorderState.AFTER_RECORDING -> {
                    setImageResource(R.drawable.ic_recorder_play)
                }
                RecorderState.ON_PLAYING -> {
                    setImageResource(R.drawable.ic_recorder_stop)
                }
            }
        }
}
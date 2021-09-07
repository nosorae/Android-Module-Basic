package nosorae.changed_name.p16_youtube

import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import androidx.constraintlayout.motion.widget.MotionLayout
import nosorae.changed_name.R

// 생성자로 컨텍스트만 받는 경우도 있고 AttributeSet 이 nullable 로 받는 경우도 있으므로 attrSet: AttributeSet? = null
class CustomMotionLayout(context: Context, attrSet: AttributeSet? = null): MotionLayout(context, attrSet) {

    // 이 값이 true 일 때만 터치가 되게 할 것이다.
    private var motionTouchStarted = false
    // 위치만 알면돼서 View 로 받는 센스
    private val mainContainerView: View by lazy {
        findViewById<View>(R.id.fragment_youtube_player_main_container)
    }

    private val hitRect = Rect()

    init {
        setTransitionListener(object : TransitionListener {
            override fun onTransitionStarted(p0: MotionLayout?, p1: Int, p2: Int) {}

            override fun onTransitionChange(p0: MotionLayout?, p1: Int, p2: Int, p3: Float) {}

            override fun onTransitionCompleted(p0: MotionLayout?, p1: Int) {
                // 트랜젝션이 컴플릿 되면 애니메이션이 끝난 것으로 간주
                motionTouchStarted = false
            }

            override fun onTransitionTrigger(p0: MotionLayout?, p1: Int, p2: Boolean, p3: Float) {}
        })
    }


    // 아니 근데 override 할 때 파라미터 타입에 ? 붙어있는데 막 떼어줘도 되는거야??

    override fun onTouchEvent(event: MotionEvent): Boolean {
        // 이 프레임레이아웃 ( 프래그먼트 ) 에? 들어오는 터치이벤트를 수신해서
        // 손가락 떼는 동작이면 그대로 리턴하고, 그 외 나머지 동작에서 아래 뷰에 터치가 일어났는지 체크해서 반환한다.

        when (event.actionMasked) {
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                motionTouchStarted = false
                return super.onTouchEvent(event) // 커스텀하지 않고 기존에 있는 터치이벤트를 리턴하겠다는 의미
            }
        }
        if (motionTouchStarted.not()) {
            // mainContainerView 에 클릭이 일어난 것인지 알아보는 것이 목적
            mainContainerView.getHitRect(hitRect) // Rect 값을 넣으면 거기에 값을 저장해서 반환해줌
            motionTouchStarted = hitRect.contains(event.x.toInt(), event.y.toInt()) //

        }

        return return super.onTouchEvent(event) && motionTouchStarted // 커스텀한 터치가능 여부와 함께 넘겨준다.


    }


    private val gestureListener by lazy {
        object : GestureDetector.SimpleOnGestureListener() {
            override fun onScroll(
                e1: MotionEvent,
                e2: MotionEvent,
                distanceX: Float,
                distanceY: Float
            ): Boolean {
                mainContainerView.getHitRect(hitRect)
                return hitRect.contains(e1.x.toInt(), e2.y.toInt())

            }
        }
    }
    private val gestureDetector by lazy {
        GestureDetector(context, gestureListener)
    }

    // 제스쳐 이벤트
    override fun onInterceptTouchEvent(event: MotionEvent?): Boolean {
        return gestureDetector.onTouchEvent(event)

    }


}
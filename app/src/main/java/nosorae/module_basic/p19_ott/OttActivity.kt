package nosorae.module_basic.p19_ott

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.motion.widget.MotionLayout
import nosorae.module_basic.databinding.ActivityOttBinding

/**
 * - ScrollView 안에서의 MotionLayout 활용
 *      https://developer.android.com/training/constraint-layout/motionlayout?hl=ko
 *      TODO https://codelabs.developers.google.com/codelabs/motion-layout#0 참고
 *      ConstraintLayout 상속, 복잡한 transition 구현을 편하게 만들었다.
 * - ConstraintSet 활용
 *
 * - CollapsingToolbar ( AppbarLayout 을 이용해 헤더 애니메이션 구현 )
 *
 * - Inset (FitSystemWindow)
 *
 * - 기본 constraintlayout 에서 런타임에 관계를 바꾸어주며 트랜지션을 구현할 수도 있다?
 *      https://developer.android.com/training/constraint-layout?hl=ko
 *      ConstraintLayout에서 ConstraintSet 및 TransitionManager를 사용하여 요소의 크기와 위치 변경사항을 애니메이션으로 보여줄 수 있습니다.
 *
 *
 * - android:fitsSystemWindows="false"
 *      상단의 status bar 하단까지가  윈도우에서 정의하고 있는 cf area 영역인데 그거 false 로 설정해서 그 바깥영역까지 침범할 수 있게 만든 것이다.
 *
 *
 *
 */
class OttActivity: AppCompatActivity() {
    private lateinit var binding: ActivityOttBinding
    private var isGatheringMotionsAnimating: Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOttBinding.inflate(layoutInflater)
        setContentView(binding.root)
        makeStatusBarTransparent()
        initScrollView()
        initDigitalThingsContainer()
        initMainContainer()
    }

    private fun initMainContainer() {
        binding.ottContainerMain.setTransitionListener(object : MotionLayout.TransitionListener{
            override fun onTransitionStarted(p0: MotionLayout?, p1: Int, p2: Int) {

            }

            override fun onTransitionChange(p0: MotionLayout?, p1: Int, p2: Int, p3: Float) {

            }

            override fun onTransitionCompleted(p0: MotionLayout?, p1: Int) {

            }

            override fun onTransitionTrigger(p0: MotionLayout?, p1: Int, p2: Boolean, p3: Float) {

            }
        })
    }

    private fun initScrollView() {
        binding.ottScrollView.viewTreeObserver.addOnScrollChangedListener {
            if (binding.ottScrollView.scrollY > 150f.dpToPx(this).toInt()) {
                if (isGatheringMotionsAnimating.not()) {
                    binding.ottContainerDigitalThings.transitionToEnd()
                    binding.ottContainerMain.transitionToEnd()
                }
            } else {
                if(isGatheringMotionsAnimating.not()) {
                    binding.ottContainerDigitalThings.transitionToStart()
                    binding.ottContainerMain.transitionToStart()
                }
            }
        }
    }
    private fun initDigitalThingsContainer() {
        binding.ottContainerDigitalThings.setTransitionListener(object : MotionLayout.TransitionListener {
            override fun onTransitionStarted(p0: MotionLayout?, p1: Int, p2: Int) {
               isGatheringMotionsAnimating = true
            }

            override fun onTransitionChange(p0: MotionLayout?, p1: Int, p2: Int, p3: Float) {

            }

            override fun onTransitionCompleted(p0: MotionLayout?, p1: Int) {
                isGatheringMotionsAnimating = false
            }

            override fun onTransitionTrigger(p0: MotionLayout?, p1: Int, p2: Boolean, p3: Float) {

            }
        })
    }


}

fun Float.dpToPx(context: Context): Float =
    TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this, context.resources.displayMetrics)

// System 의 statusbar 를 투명으로 만드는 확장함수
fun Activity.makeStatusBarTransparent() {
    window.apply {
        decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        statusBarColor = Color.TRANSPARENT
    }
}
package nosorae.module_basic.p19_ott

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.core.view.OnApplyWindowInsetsListener
import androidx.core.view.ViewCompat
import com.google.android.material.appbar.AppBarLayout
import nosorae.module_basic.R
import nosorae.module_basic.databinding.ActivityOttBinding
import kotlin.math.abs

/**
 * - ScrollView 안에서의 MotionLayout 활용
 *      https://developer.android.com/training/constraint-layout/motionlayout?hl=ko
 *      TODO https://codelabs.developers.google.com/codelabs/motion-layout#0 참고
 *      ConstraintLayout 상속, 복잡한 transition 구현을 편하게 만들었다.
 *      ScrollView 의 scrollY, height 속성을 이용하여 원하는 스크롤타이밍에 원하는 모션을 사용하였다.
 *
 *
 * - CollapsingToolbar ( AppbarLayout 을 이용해 헤더 애니메이션 구현 )
 *      TODO https://freehoon.tistory.com/38 참고 복습 필수!
 *
 * - Inset (FitSystemWindow)
 *      TODO https://stackoverflow.com/questions/38621380/what-are-insets-in-android 참고 아직 이해를 덜했다.
 *      뷰 안의 다른 뷰에대한 마진 개념인가?
 *
 * - 기본 constraintlayout 에서 런타임에 관계를 바꾸어주며 트랜지션을 구현할 수도 있다?
 *      https://developer.android.com/training/constraint-layout?hl=ko
 *      ConstraintLayout에서 ConstraintSet 및 TransitionManager를 사용하여 요소의 크기와 위치 변경사항을 애니메이션으로 보여줄 수 있습니다.
 *      그냥 MotionLayout 사용하는 게 나을듯?
 *
 *
 * - android:fitsSystemWindows="false"
 *      상단의 status bar 하단까지가  윈도우에서 정의하고 있는 cf area 영역인데 그거 false 로 설정해서 그 바깥영역까지 침범할 수 있게 만든 것이다.
 *
 */
class OttActivity: AppCompatActivity() {
    private lateinit var binding: ActivityOttBinding
    private var isGatheringMotionsAnimating: Boolean = false
    private var isCurationMotionAnimating: Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOttBinding.inflate(layoutInflater)
        setContentView(binding.root)
        makeStatusBarTransparent()
        initAppBar()
        initInsetMargin()

        initScrollView()
        initDigitalThingsContainerMotionLayout()
        initMainContainerMotionLayout()
        initCurationAnimationMotionLayout()
    }

    // 윈도우에 있는 모든 시스템영역의 인셋값을 조절하는 역할
    // 최상단 View 인 CoordinatorLayout 의 inset 값을 받아서 커스터마이징을 할 것이다.
    private fun initInsetMargin() = with(binding) {
        ViewCompat.setOnApplyWindowInsetsListener(ottCoordinatorLayout, OnApplyWindowInsetsListener { v, insets ->
            val params = v.layoutParams as ViewGroup.MarginLayoutParams
            params.bottomMargin = insets.systemWindowInsetBottom
            ottToolBarContainer.layoutParams = (ottToolBarContainer.layoutParams as ViewGroup.MarginLayoutParams).apply {
                setMargins(0, insets.systemWindowInsetTop, 0, 0)
            }
            ottCollapsingToolBarContainer.layoutParams = (ottCollapsingToolBarContainer.layoutParams as ViewGroup.MarginLayoutParams).apply {
                setMargins(0, 0, 0, 0)
            }
            insets.consumeSystemWindowInsets()
        })
    }

    // 스크롤하면서 알파값을 조절
    private fun initAppBar() {
        binding.ottAppBar.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
            val topPadding = 300f.dpToPx(this) // 420 dp 중에 300 dp 는 무시되늰 것이다.
            val realAlphaScrollHeight = appBarLayout.measuredHeight - appBarLayout.totalScrollRange // 스크롤 할 수 있는 범위 ( 120 dp )
            val abstractOffSet = abs(verticalOffset)
            val realAlphaVerticalOffset = if (abstractOffSet - topPadding < 0) 0f else abstractOffSet - topPadding // topPadding 이후에 실제로  움직인 값??
            if (abstractOffSet < topPadding) {
                binding.ottToolBarBackgroundView.alpha = 0f
                return@OnOffsetChangedListener
            }

            val percentage = realAlphaVerticalOffset / realAlphaScrollHeight //  topPadding 이후에 실제로  움직인 값을 120 dp 로 나눠주었다고 생각하면 됨
            binding.ottToolBarBackgroundView.alpha =
                1 - (if (1 - percentage * 2 < 0) 0f else 1 - percentage * 2) // 알파값이 순간적으로 빠르게 올리기 위해서 *2 처리
        })
        initActionBar()
    }

    private fun initActionBar() = with(binding) {
        ottToolBar.navigationIcon = null // @param icon Drawable to set, may be null to clear the icon
        ottToolBar.setContentInsetsAbsolute(0,0)
        setSupportActionBar(binding.ottToolBar) //Set a {@link android.widget.Toolbar Toolbar} to act as the {@link androidx.appcompat.app.ActionBar} for this Activity window.
        supportActionBar?.let {  // Retrieve a reference to this activity's ActionBar.
            it.setHomeButtonEnabled(false)
            it.setDisplayHomeAsUpEnabled(false)
            it.setDisplayShowHomeEnabled(false)
        }

    }

    private fun initMainContainerMotionLayout() {
        binding.ottMotionLayoutContainerMain.setTransitionListener(object : MotionLayout.TransitionListener{
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
        // collapsing layout 이 다 스크롤 된 후부터 이 아래 스크롤뷰가 스크롤 계산되기 시작한다.
        binding.ottScrollView.smoothScrollTo(0, 0)
        binding.ottScrollView.viewTreeObserver.addOnScrollChangedListener {
            val scrolledValue = binding.ottScrollView.scrollY
            if ( scrolledValue > 50f.dpToPx(this).toInt()) {
                if (isGatheringMotionsAnimating.not()) {
                    binding.ottMotionLayoutContainerDigitalThings.transitionToEnd()
                    binding.ottMotionLayoutContainerMain.transitionToEnd()
                    binding.ottMotionLayoutContainerDigitalThingsBackground.transitionToEnd()
                }
            } else {
                if(isGatheringMotionsAnimating.not()) {
                    binding.ottMotionLayoutContainerDigitalThings.transitionToStart()
                    binding.ottMotionLayoutContainerMain.transitionToStart()
                    binding.ottMotionLayoutContainerDigitalThingsBackground.transitionToStart()
                }
            }

            Log.d("scroll", "scrolledValue = $scrolledValue, scrollViewHeight = ${binding.ottScrollView.height}")

            if (scrolledValue > (binding.ottScrollView.height / 1.5).toInt()) {
                Log.d("scroll", "150f dpToPx 를 넘었다.")
                if (isCurationMotionAnimating.not()) {
                    binding.ottMotionLayoutCurationAnimation.setTransition(R.id.curation_animation_start1, R.id.curation_animation_end1)
                    binding.ottMotionLayoutCurationAnimation.transitionToEnd()
                    isCurationMotionAnimating = true // 한번 보여주고 끝낼거라서 여기서 true 로 설정해서 더이상 애니메이션이 발생하지 않게 만들어본다.
                }
            }


        }
    }
    private fun initDigitalThingsContainerMotionLayout() {
        binding.ottMotionLayoutContainerDigitalThings.setTransitionListener(object : MotionLayout.TransitionListener {
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

    private fun initCurationAnimationMotionLayout() {
        binding.ottMotionLayoutCurationAnimation.setTransitionListener(object : MotionLayout.TransitionListener {
            override fun onTransitionStarted(p0: MotionLayout?, p1: Int, p2: Int) = Unit

            override fun onTransitionChange(p0: MotionLayout?, p1: Int, p2: Int, p3: Float) {

            }

            override fun onTransitionCompleted(p0: MotionLayout?, currentId: Int) {
                when (currentId) {
                    R.id.curation_animation_end1 -> {
                        binding.ottMotionLayoutCurationAnimation.setTransition(R.id.curation_animation_start2, R.id.curation_animation_end2)
                        binding.ottMotionLayoutCurationAnimation.transitionToEnd()
                    }
                }


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
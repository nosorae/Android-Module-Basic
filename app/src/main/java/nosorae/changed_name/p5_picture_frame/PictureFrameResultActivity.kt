package nosorae.changed_name.p5_picture_frame

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import nosorae.changed_name.R
import java.util.*
import kotlin.concurrent.timer

/**
 * 이미지뷰를 바꿔가면서 보여주는 방법은 여러가지가 있다.
 * 그 중 하나가 일정 시간마다 setImage 해줄 수도 있고
 * 애니메이션으로 페이드인 페이드아웃 하며 이미지를 변환해줄 수도 있겠다. 여기서는 이 방식을 채택
 */
class PictureFrameResultActivity : AppCompatActivity() {
    private val photoList = mutableListOf<Uri>()

    private val photoImageView: ImageView by lazy {
        findViewById<ImageView>(R.id.picture_frame_result_image_foreground)
    }
    private val backgroundPhotoImageView: ImageView by lazy {
        findViewById<ImageView>(R.id.picture_frame_result_image_background)
    }

    private var currentPosition = 0

    private var timer: Timer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_picture_frame_result)

        getPhotoUriFromIntent()
        startTimer()
        Log.d("PictureFrame", "onCreate!!! timer start")
    }

    private fun getPhotoUriFromIntent() {
        val size = intent.getIntExtra("photoListSize", 0)
        for (i in 0..size) {
            intent.getStringExtra("photo$i")?.let {
                photoList.add(Uri.parse(it))
            }
        }
    }

    private fun startTimer() {
        timer = timer(period = 3000) {
            Log.d("PictureFrame", "5초 지나감")
            // 여기에 람다인자로 5초마다 무엇을 할지 결정해준다.
            runOnUiThread {
                val current = currentPosition
                // 다음 이미지가 띄워져야하는데 마지막 이미지를 보고있을 때 다시 처음 이미지로 설정하기 위한 변수 next
                val next = if (photoList.size <= currentPosition + 1) 0 else currentPosition + 1

                backgroundPhotoImageView.setImageURI(photoList[current])

                photoImageView.alpha = 0f // 투명도
                photoImageView.setImageURI(photoList[next])
                photoImageView.animate()
                    .alpha(1.0f)
                    .setDuration(1000)
                    .start()

                currentPosition = next


            }
        }
    }

    override fun onResume() {
        super.onResume()
        Log.d("PictureFrame", "onResume!!!")
    }

    override fun onPause() {
        super.onPause()
        Log.d("PictureFrame", "onPause!!!")
    }

    override fun onStop() {
        super.onStop()
        // timer 가 null 인 경우에는 아직 startTimer 함수가 호출되지 않은 것이므로 그냥 cancel 을 무시한다.
        timer?.cancel()
        Log.d("PictureFrame", "onStop!!! timer canceled  ${timer == null}")
    }

    override fun onRestart() {
        super.onRestart()
        startTimer()
        Log.d("PictureFrame", "onRestart!!! timer start")
    }

    override fun onStart() {
        super.onStart()
        //onCreate 함수는 액티비티가 만들어지고 Destroy 될 때까지 한 번만 호출되니 onStart 함수에도 startTimer를 호춣한다.

        //Log.d("PictureFrame", "onStart!!! timer start")
        Log.d("PictureFrame", "onStart!!!")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("PictureFrame", "onDestroy!!! timer canceled  ${timer == null}")
        timer?.cancel()
    }
}
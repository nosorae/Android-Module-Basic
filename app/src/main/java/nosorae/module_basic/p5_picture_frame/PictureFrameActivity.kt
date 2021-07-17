package nosorae.module_basic.p5_picture_frame

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import nosorae.module_basic.R

/**
 * 가로화면 그리기
 * Android Permission - 로컬사진 가져오기
 * -> https://developer.android.com/training/permissions/requesting?hl=ko 참고
 * View Animation 사용
 * Lifecycle 알아보기
 * Contene Provider 사용하는 여러가지 방법중 SAF (Storage Access Framework) 사용용
 * */


class PictureFrameActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_picture_frame)
    }
}
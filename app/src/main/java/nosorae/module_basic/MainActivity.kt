package nosorae.module_basic

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import nosorae.module_basic.p1_bmi.BMIActivity
import nosorae.module_basic.p3_diary.DiaryActivity
import nosorae.module_basic.p2_lotto.LottoActivity
import nosorae.module_basic.p4_calculator.CalculatorActivity
import nosorae.module_basic.p5_picture_frame.PictureFrameActivity

// TODO https://kotlinlang.org/ 참고해서 코틀린언어 공부
// 그래들은 안드로이드 앱을 빌드하는데 도움을 주는 것
//액티비티는 도화지 개념 이 도화지의 그림이 layout이 선언되어있는 파일이 R파일
//중복 변수는 밖으로 빼고, 중복기능은 함수로 만든다.
//onCreate 호출 시점은 뷰가 완전히 그려졌을 시점

class MainActivity : AppCompatActivity() {
    //액티비티가 실행됐을 때 가장 먼저 호출되는 게 onCreate함수
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main) // R파일에서 레이아웃을 가져와서 연결해준다.
        val button_bmi = findViewById<Button>(R.id.button_bmi) //레이아웃의 컴포넌트를 R파일의 id로 가져와서
        val button_lotto = findViewById<Button>(R.id.button_lotto)
        val button_diary = findViewById<Button>(R.id.button_diary)
        val button_calculator = findViewById<Button>(R.id.button_calculator)
        val button_picture_frame = findViewById<Button>(R.id.button_picture_frame)

        button_bmi.setOnClickListener {
            startActivity(Intent(this, BMIActivity::class.java))
        }
        button_lotto.setOnClickListener {
            startActivity(Intent(this, LottoActivity::class.java))
        }
        button_diary.setOnClickListener {
            startActivity(Intent(this, DiaryActivity::class.java))
        }

        button_calculator.setOnClickListener {
            startActivity(Intent(this, CalculatorActivity::class.java))
        }

        button_picture_frame.setOnClickListener {
            startActivity(Intent(this, PictureFrameActivity::class.java))
        }
    }
}

/*
Android 파일탐색기
크게 manifest, java, res로 나뉨
manifest는 앱의 특징, 기능, 구조, 권한 명시
java에는 소스코드들이 들어있음
res에는 앱에서 사용하는 이미지 소리 영상 폰트 레이아웃 같은 코드외의 모든 것이 들어간다.
drawable, layout, mipmap, values...
drawable 이미지 리소스 저장
layout 앱이 어떻게 그려질지 저장하는 도화지역할
mipmap 이미지 리소스 저장
values 여러 값들을 저장해서 하드코딩을 방지?

Gradle Scripts
각종 앱에관한 설정들

단축키
리포맷 코드 : ctrl + alt + l
빌드 : shift + f10
자동 import : alt + enter
 */
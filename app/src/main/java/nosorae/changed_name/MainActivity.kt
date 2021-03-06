package nosorae.changed_name

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import nosorae.changed_name.databinding.ActivityMainBinding
import nosorae.changed_name.p10_quote.QuoteActivity
import nosorae.changed_name.p11_alarm.AlarmActivity
import nosorae.changed_name.p12_book.BookActivity
import nosorae.changed_name.p13_tinder.TinderActivity
import nosorae.changed_name.p14_market.MarketActivity
import nosorae.changed_name.p15_airbnb.AirActivity
import nosorae.changed_name.p16_youtube.YoutubeActivity
import nosorae.changed_name.p17_music.MusicActivity
import nosorae.changed_name.p18_location.LocationSearchActivity
import nosorae.changed_name.p19_ott.OttActivity
import nosorae.changed_name.p1_bmi.BMIActivity
import nosorae.changed_name.p20_git.GitSignInActivity
import nosorae.changed_name.p21_dust.DustActivity
import nosorae.changed_name.p22_unsplash.UnsplashActivity
import nosorae.changed_name.p2_lotto.LottoActivity
import nosorae.changed_name.p3_diary.DiaryActivity
import nosorae.changed_name.p4_calculator.CalculatorActivity
import nosorae.changed_name.p5_picture_frame.PictureFrameActivity
import nosorae.changed_name.p6_pomodoro.PomodoroActivity
import nosorae.changed_name.p7_recorder.RecorderActivity
import nosorae.changed_name.p8_web.WebActivity
import nosorae.changed_name.p9_push.PushActivity

// TODO https://kotlinlang.org/ 참고해서 코틀린언어 공부
// 그래들은 안드로이드 앱을 빌드하는데 도움을 주는 것
//액티비티는 도화지 개념 이 도화지의 그림이 layout이 선언되어있는 파일이 R파일
//중복 변수는 밖으로 빼고, 중복기능은 함수로 만든다.
//onCreate 호출 시점은 뷰가 완전히 그려졌을 시점

class MainActivity : AppCompatActivity() {
    //액티비티가 실행됐을 때 가장 먼저 호출되는 게 onCreate함수
    private lateinit var binding: ActivityMainBinding // p12 부터 뷰바인딩 추가
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root) // R파일에서 레이아웃을 가져와서 연결해준다.

        startProject(binding.buttonBmi, BMIActivity::class.java)
        startProject(binding.buttonLotto, LottoActivity::class.java)
        startProject(binding.buttonDiary, DiaryActivity::class.java)
        startProject(binding.buttonCalculator, CalculatorActivity::class.java)
        startProject(binding.buttonPictureFrame, PictureFrameActivity::class.java)
        startProject(binding.buttonPomodoro, PomodoroActivity::class.java)
        startProject(binding.buttonRecorder, RecorderActivity::class.java)
        startProject(binding.buttonWeb, WebActivity::class.java)
        startProject(binding.buttonPush, PushActivity::class.java)
        startProject(binding.buttonQuote, QuoteActivity::class.java)
        startProject(binding.buttonAlarm, AlarmActivity::class.java)
        startProject(binding.buttonBook, BookActivity::class.java)
        startProject(binding.buttonTinder, TinderActivity::class.java)
        startProject(binding.buttonMarket, MarketActivity::class.java)
        startProject(binding.buttonAirbnb, AirActivity::class.java)
        startProject(binding.buttonYoutube, YoutubeActivity::class.java)
        startProject(binding.buttonMusic, MusicActivity::class.java)
        startProject(binding.buttonLocation, LocationSearchActivity::class.java)
        startProject(binding.buttonOtt, OttActivity::class.java)
        startProject(binding.buttonGit, GitSignInActivity::class.java)
        startProject(binding.buttonDust, DustActivity::class.java)
        startProject(binding.buttonUnsplash, UnsplashActivity::class.java)

    }
    private fun startProject(button: Button, activity: Class<*>) {
        button.setOnClickListener {
            startActivity(Intent(this, activity))
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

drawableStartCompat 에서 Compat 으로 쓰는 이유? 벡터 이미지 넣을 때 호환성이 필요해서

단축키
리포맷 코드 : ctrl + alt + l
빌드 : shift + f10
자동 import : alt + enter
함수만들기 Extract Function : 범위지정 -> ctrl + alt + m (우클릭 -> Refactor -> Function..)

Color Tool
https://material.io/resources/color/#!/?view.left=0&view.right=0&primary.color=607D8B

TODO ListAdapter 동작원리 알아보기

 */
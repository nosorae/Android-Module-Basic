package nosorae.changed_name.p3_diary

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.NumberPicker
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.edit
import nosorae.changed_name.R

/*
  - Handler 사용
  - SharedPreference (로컬 DB 개념, android-ktx(Kotlin Android Extension)로 사용)
    비밀번호, 다이어리 내용 저장해서 유지
  - EditText 가 수정될 때마다 0.5초 딜레이를 두고 저장하는 기능 TODO Handler 공부
  - Theme 사용 (NoActionBar 액션바 없애기)
  - AlertDialog 사용
  - font 설정
  - 테마를 변경하는 방법도 있지만 버튼 컴포넌트를 기본버튼이 아니라
    머터리얼 컴포넌트에 적용받지 않는 androidx.appcompat.widget.AppCompatButton
    사용해서 테마 속성에 영향 받지 않게 만듦
  - lazy 초기화하는 이유는 액티비티 생성될 시점에는 뷰가 아직 그려지지 않았기 때문.
    뷰가 다 그려졌다고 알려주는 시점이 onCreate함수, 그래서 onCreate에 그 변수를 써주면 초기화되는 것이다.

*/
class DiaryActivity : AppCompatActivity() {
    private val numberPicker1: NumberPicker by lazy {
        findViewById<NumberPicker>(R.id.diary_number_picker_first).apply {
            minValue = 0
            maxValue = 9
        }
    }
    private val numberPicker2: NumberPicker by lazy {
        findViewById<NumberPicker>(R.id.diary_number_picker_second).apply {
            minValue = 0
            maxValue = 9
        }
    }
    private val numberPicker3: NumberPicker by lazy {
        findViewById<NumberPicker>(R.id.diary_number_picker_third).apply {
            minValue = 0
            maxValue = 9
        }
    }
    private val openButton: AppCompatButton by lazy {
        findViewById<AppCompatButton>(R.id.diary_button_open)
    }
    private val changePasswordButton: AppCompatButton by lazy {
        findViewById<AppCompatButton>(R.id.diary_button_change_password)
    }

    private var changePasswordMode = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_diary)

        numberPicker1
        numberPicker2
        numberPicker3

        openButton.setOnClickListener {
            if (changePasswordMode) {
                Toast.makeText(this, "비밀번호 변경 중입니다.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 기기에 저장된 패스워드와 넘버피커 세개의 패스워드가 같은지 확인
            // 기기에 저장하는 방법은 localDB를 사용하는 방법과 파일에 직접 적는 방식이 있는데
            // 파일에 직접 적는 방식 중 하나가 sharedPreference
            // name 은 sharedPreference 라는 파일의 이름 이걸 다른 앱과 같이 사용할 수 있도록 해줌
            // MODE_PRIVATE 을 사용하면 이 앱에서만 사용하겠다는 의미
            val passwordPreference = getSharedPreferences("password", Context.MODE_PRIVATE)
            val passwordFromUser =
                "${numberPicker1.value}${numberPicker2.value}${numberPicker3.value}"
            //초기비밀번호는 자연스럽게 000이됨을 느끼자 저장을 안하면 000으로 나오고 저장한 이후에는 마지막에 저장한 값이 나온다.ㄷ
            if (passwordPreference.getString("password", "000").equals(passwordFromUser)) {
                // 패스워드 성공
                startActivity(Intent(this, DiaryResultActivity::class.java))
            } else {
                // 패스워드 실패 AlertDialog 는 빌드패턴으로 사용
                showErrorAlertDialog()
            }
        }

        changePasswordButton.setOnClickListener {
            val passwordPreference = getSharedPreferences("password", Context.MODE_PRIVATE)
            val passwordFromUser = "${numberPicker1.value}${numberPicker2.value}${numberPicker3.value}"

            if (changePasswordMode) {
                // 번호를 저장하는 기능
                // commit 은 다 저장될 때까지 UI를 멈추는 기능이고 (여기서는 저장하는데 많은 시간이 걸리지 않음으로 이거 사용)
                // apply 는 비동기적으로 저장하는 방식
                // commit 은 무거운 작업할 때 사용 x
                // 코틀린 익스텐션 기능 이용해서 edit 함수를 쉽게 사용할 수 있게 람다함수를 정의해놓았다?
                // commit 의 디폴트값이 false 라서 인자 빼도 apply 는 자동으로 된다.
                passwordPreference.edit(true) {
                    putString("password", passwordFromUser)
                }
                changePasswordMode = false
                changePasswordButton.setBackgroundColor(Color.BLACK)

            } else {
                // 비밀번호 확인 및 changePasswordMode 를 활성화
                if (passwordPreference.getString("password", "000").equals(passwordFromUser)) {
                    changePasswordMode = true
                    Toast.makeText(this, "변경할 패스워드를 입력해주세요", Toast.LENGTH_SHORT).show()
                    changePasswordButton.setBackgroundColor(Color.RED)
                } else {
                    showErrorAlertDialog()
                }
            }

        }

    }
    private fun showErrorAlertDialog() {
        AlertDialog.Builder(this)
            .setTitle("실패")
            .setMessage("비밀번호가 잘못되었습니다.")
            .setPositiveButton("확인") { _, _ -> }
            .create()
            .show()
    }
}
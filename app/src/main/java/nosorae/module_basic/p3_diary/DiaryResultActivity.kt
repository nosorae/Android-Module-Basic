package nosorae.module_basic.p3_diary

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.EditText
import androidx.core.content.edit
import androidx.core.widget.addTextChangedListener
import nosorae.module_basic.R

// UI에서 처리되는 작업을 UI쓰레드나 메인쓰레드라고 하고
// 새로운 쓰레드를 열었을 때 메인쓰레드와 연결을 해주는 기능을 가진 게 핸들러이다.
// 핸들러는 액티비티에도 기본적으로 하나 돌고 있고, 뷰에도 핸들러가 있어서 뷰에서도 핸들러의 기능을 사용할 수 있다.
// 핸들러를 사용하는 기능은 즈로 포스트함수나 포스트딜레이함수가 있을 수 있다. 여기서는 포스트딜레이함수를 사용
// 핸들러는 스레드간의 통신을 엮어주는 안드로이드에서 제공해주는 기능이다.
// 기본적으로 사용하게 되는 스레드는 메인스레드이고,
// 메인스레드가 느려지는 무거운 작업 (ex 네트워크 통신, 파일 저장 ... )은 별도로 스레드를 열어서 UI 스레드와 별개로 작업

class DiaryResultActivity : AppCompatActivity() {
    private val diaryEditText: EditText by lazy {
        findViewById(R.id.diary_result_edit_text)
    }
    private val handler = Handler(Looper.getMainLooper()) //메인스레드에 연결된 핸들러가 만들어진 것이다.

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_diary_result)

        val detailPreferences = getSharedPreferences("diary", Context.MODE_PRIVATE)

        diaryEditText.setText(detailPreferences.getString("detail", ""))
        //내용이 수정될 때마다 저장하려고 한다.

        val runnable = Runnable {
            //여기서는 일부러 true안주고 apply로 저장한다. (비동기)
            getSharedPreferences("diary", Context.MODE_PRIVATE).edit {
                putString("detail", diaryEditText.text.toString())
            }
            Log.d("diary", "save ${diaryEditText.text}")
        }


       //잠깐 이 앱과 관련없는 따로만든 스레드 예시
        val thread = Thread(Runnable {
            //네트워크 작업으로 데이터 업로드, 다운로드하고
            // 여기서 UI 업데이트해줘야하려고한다면 여기서는 불가능
            // 그래서 runOnUiThread 기능을 사용한다. 메인스레드를 블럭으로 잠깐 열어둔다
            runOnUiThread {
                //액티비티에 있는 메인 핸들러에서 실행이 되도록 사용할 수 있다.
            }
            // 또는 val handler = Handler(Looper.getMainLooper()) 해둔 변수로
            // handler.post 로 메인 핸들러에 던짐으로써 메인스레드를 실행시킬 수 있다. UI 를 변경할 수 있다.
            handler.post {

            }

            Log.e("aa", "aa")
        }).start()

        diaryEditText.addTextChangedListener {
            detailPreferences.edit {
                Log.d("diary", diaryEditText.text.toString())
                //0.5초 이전에 아직 실행되지 않고 펜딩상태인 러너블이 있다면 지워주기 위해 사용
                handler.removeCallbacks(runnable)
                handler.postDelayed(runnable, 500)


            }
        }
    }
}
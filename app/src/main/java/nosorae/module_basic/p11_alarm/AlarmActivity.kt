package nosorae.module_basic.p11_alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.edit
import nosorae.module_basic.R
import org.w3c.dom.Text
import java.util.*

/**
 * AlarmManager 를 통해서 내가 원하는 시간에 PendingIntent 를 시스템에서 브로드캐스트 리시버를 통해 작업을 받아서 알람을 띄우는 과정
 * AlarmManager
 * -> val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
 * -> alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, AlarmManager.INTERVAL_DAY, pendingIntent)
 * -> setExact 로 하면 정확해지지만 그만큼 자원이 많이 소모된다.
 * -> 도즈 모드에서도 꼭 실행시켜주는 옵션은 setExactAndAllowWhileIdle 이나 setAndAllowWhileIdle
 * -> 다른 알람방법은 서버에서 notification push 를 주는 방법이 있다.
 * Notification 사용
 * Broadcast Receiver 사용
 * -> 시스템에서 일어나는 작업 들(예. 기기 켜짐 여부, 배터리 잔량, 와이파이 켜짐 여부)이 일어날 때마다 전파해주는데,
 * -> 그걸 Broadcast Receiver 로 수신한다는 기능
 * -> 꼭 시스템 작업 뿐만 아니라 다른 앱으로 notification 하건 다른 앱의 작업을 수신해서 우리앱에서 작업을 처리할 수 있다.
 * Background 작업
 * -> 필요한 이유? ~분 뒤에 알림해줘야하는데 앱이 그동안 살아있을 거라는 보장이 없기 때문에 백그라운드 작업이 필요하다.
 * -> 백그라운드 작업의 종류
 * -> Immediate tasks( 즉시 실행해야하는 작업, Thread, Handler, Kotlin coroutines ), db 저장, ui 업데이트, ...
 * -> Deferred tasks ( 지연된 작업, WorkManager ) 푸시 수신, ...
 * -> Exact tasks ( 정시에 실행해야 하는 작업, AlarmManager ) 반복되는 지정된 시간, ...
 * Exact tasks 에는 PendingIntent 사용 예정 ( p9_MyFirebaseMessagingService 에도 나왔다.)
 *
 * AlarmManager
 * -> Real Time, Elapsed Time 두 가지 방식식
 * -> 설정한 시간이 되면 브로드캐스트 리시버를 통해서 알람이 울리는 작업을 수신함(?)
 *
 * string resource 로 쉽게 빼는 방법은 하드코딩된 텍스트에 나타나는 경고에 커서를 두고 alt+Enter -> Extract Resource
 *
 * TimePickDialog 사용 : AlertDialog 랑 비슷하게 미리 정의된 컴포넌트 ( AlertDialog 의 경우 타이틀, 내용, 예, 아니오 등)
 * dataclass 에서 getter 함수 만들기
 *
 * Calendar.getInstance(), Calendar.HOUR_OF_DAY, Calendar.minute, Calendar.DATE 활용용 *
 *
 */
class AlarmActivity: AppCompatActivity() {
    private val timeTextView: TextView by lazy {
        findViewById(R.id.alarm_text_view_time)
    }
    private val amPmTextView: TextView by lazy {
        findViewById(R.id.alarm_text_view_am_pm)
    }
    private val onOffButton: AppCompatButton by lazy {
        findViewById(R.id.alarm_button_on_off)
    }
    private val changeTimeButton: AppCompatButton by lazy {
        findViewById(R.id.alarm_button_change_time)
    }
    // project_alarm.preference 파일이 만들어지면서 alarm 에 대한 db 가 만들어질 거고, MODE_PRIVATE 으로 이앱에서만 사용
    private val sharedPreference by lazy {
        getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alarm)
        // 알람을 켰는지 껐는지를 sharedPreference 에 저장
        // step0 뷰 초기화 ( 리스너, ... )
        initOnOffButton()
        initChangeAlarmTimeButton()
        
        val model = fetchDataFromSharedPreferences()
        renderView(model)

        // step1 데이터 가져오기
        // step2 가져온 데이터 그려주기
    }




    private fun initOnOffButton() {
        onOffButton.setOnClickListener {
            // as? 에서 ? 는 형변환 실패에 대비하는 것이다. 형변환 실패시 null 처리
            val model = it.tag as? AlarmDisplayModel ?: return@setOnClickListener

            // 데이터 확인
            val newModel = saveAlarmModel(model.hour, model.minute, model.onOff.not())
            renderView(newModel) // 그저 "알람 켜기" -> "알람 끄기" 바꾸기 위해서?


            // 현재 온인지 오프인지에 따라 작업을 처리한다. 온 -> 알람 등록, 오프 -> 알람 제거
            if(newModel.onOff) {
                // 알람을 켜는 경우 -> 알람을 등록한다.
                val calendar = Calendar.getInstance().apply {
                    set(Calendar.HOUR_OF_DAY, newModel.hour)
                    set(Calendar.MINUTE, newModel.minute)

                    // 방금 set 한 시간이 현재 시간보다 이전이라면
                    if (before(Calendar.getInstance())) {
                        add(Calendar.DATE, 1)
                    }
                }

                val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
                val intent = Intent(this, AlarmReceiver::class.java)
                // 인텐트를 펜딩인텐트로 감싸서 전달한다.
                // PendingIntent 의 getBroadcast 를 통해서 BroadcastReceiver 에 등록할 PendingIntent 를 가져온다.
                val pendingIntent = PendingIntent.getBroadcast(this, ALARM_REQUEST_CODE,
                    intent, PendingIntent.FLAG_UPDATE_CURRENT)
                // setInexactRepeating 반복알람
                alarmManager.setInexactRepeating(
                    // RTC_WAKEUP 는 협정 세계시 즉, UTC(국제 표준시) 기준이다. 여기서 Calendar 사용했는데 이건 절대시간 등록방법
                    // ELAPSED_REALTIME_WAKEUP 핸드폰이 부팅이 된 이후부터 시간을 잰다.
                    // 세계적으로 사용하니 미국시간으로 폰에 등록되어있으면 정확하게 동작 안할 수도 있으니 이것을 사용하는 게 권장되지만
                    // 여기서는 Calendar 를 사용했기 때문에 RTC_WAKEUP 사용
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis, // 언제 알람이 발생할지 설정
                    AlarmManager.INTERVAL_DAY,
                    pendingIntent
                )

            } else {
                // 알람 끄는 경우
                cancelAlarm()
            }

        }
    }

    private fun initChangeAlarmTimeButton() {
        changeTimeButton.setOnClickListener {
            // 현재 시간을 가져온다.
            val calendar = Calendar.getInstance() // 시스템에 설저된 시간을 가져올 수 있다. 타임피커다이얼로그의 초기 시간으로 사용

            // TimePickDialog 사용
            TimePickerDialog(this, { picker, hour, minute ->
                // 데이터 저장 -> 뷰 업데이트
                val model = saveAlarmModel(hour, minute, false)
                renderView(model)

                // 기존에 있던 알람을 삭제한다. 브로드캐스트리시버에서 펜딩인텐트 가져와서 삭제
                cancelAlarm()




            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false)
                .show()
        }
    }

    private fun saveAlarmModel(
        hour: Int,
        minute: Int,
        onOff: Boolean
    ): AlarmDisplayModel {
        val model = AlarmDisplayModel(
            hour = hour,
            minute = minute,
            onOff = onOff
        )


        // with 스코프 함수는 sharedPreference.edit 과 함께 할 수 있는 작업들을 스코프 안에 저장하는 의미
        with(sharedPreference.edit()) {
            putString(ALARM_KEY, model.makeDataForDB())
            putBoolean(ONOFF_KEY, model.onOff)
            commit()
        }

        // 아래와 같이 edit 을 kotlin-extension 모드로 열어서 이용할 수 도 있다. 기본 commit
//        sharedPreference.edit {
//            putString("alarm", model.makeDataForDB())
//            putBoolean("onOff", model.onOff)
//        }
        return model
    }
    
    private fun fetchDataFromSharedPreferences(): AlarmDisplayModel {
        // ?: 가 붙는 이유는  timeDBValue 이 변수를 nullable 아니게 하고 싶어서다.
        val timeDBValue = sharedPreference.getString(ALARM_KEY, "9:30") ?: "9:30"
        // boolean 은 자바에서 primative 타입으로 사용 중이라서 nullable 이 애초에 아니다.
        val onOffDBValue = sharedPreference.getBoolean(ONOFF_KEY, false)
        val alarmData = timeDBValue.split(":")
        val alarmModel =  AlarmDisplayModel(
            hour = alarmData[0].toInt(),
            minute = alarmData[1].toInt(),
            onOff = onOffDBValue
        )

        // 보정, 예외처리 ( ex SharedPreference 에는 알람 켜져 있다고 했는데 실제로는 알람이 켜져있지 않은 경우 ) -> SharedPreference 도 off로
        // 브로드캐스트 가져와서 펜딩인텐트 달려있는지 확인
        // FLAG_NO_CREATE : if the described PendingIntent does not already exist, then simply return null instead of creating it.
        val pendingIntent = PendingIntent.getBroadcast(this, ALARM_REQUEST_CODE, Intent(this, AlarmReceiver::class.java), PendingIntent.FLAG_NO_CREATE)

        if ((pendingIntent == null) and alarmModel.onOff) {
            // 알람은 꺼져있는데, 알람 모델은 켜져있는 경우 ->
            alarmModel.onOff = false
        } else if ((pendingIntent != null) and alarmModel.onOff.not()) {
            // 알람은 켜져있는데, 데이터는 꺼져있는 경우 -> 알람을 취소
            pendingIntent.cancel()
        }

        return alarmModel
    }

    private fun renderView(model: AlarmDisplayModel) {
        amPmTextView.apply {
            text = model.amPmText
        }
        timeTextView.apply {
            text = model.timeText
        }
        onOffButton.apply {
            text = model.onOffText
            // 버튼에 대한 어떠한 타입이든 데이터를 저장하기 위해 사용
            tag = model
        }

    }

    private fun cancelAlarm() {
        val pendingIntent = PendingIntent.getBroadcast(this, ALARM_REQUEST_CODE,
            Intent(this, AlarmReceiver::class.java), PendingIntent.FLAG_NO_CREATE)
        pendingIntent?.cancel() // null 일 수도 있는 것은 이해하는데, 왜 스튜디오에서는 ! 처리 되어있나??
    }


    companion object {
        // 자바의 private static final 같은 거라 생각하면 된다. 모든 글자 대문자로 해서 정의
        private const val SHARED_PREFERENCES_NAME = "time"
        private const val ALARM_KEY = "alarm"
        private const val ONOFF_KEY = "onOff"
        private const val ALARM_REQUEST_CODE = 1000
    }


}
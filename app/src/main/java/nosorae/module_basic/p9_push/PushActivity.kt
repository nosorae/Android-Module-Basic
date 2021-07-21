package nosorae.module_basic.p9_push

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.messaging.FirebaseMessaging
import nosorae.module_basic.R
import org.w3c.dom.Text

/**
 * 마인드 : 개념은 한글로 ( 잘 변하지 않으니까 ), 예시 코드를 따오려면 영어로 ( 최신 코드인 게 중요하니가 )
 * onCreate 에 함수호출 이외의 명령어를 쓰지 않는 게 가독성을 높이는 것 같다.
 * Firebase 기반 푸시 알림 앱
 * -> Firebase 는 웹앱 어플리케이션을 보다 쉽게 만들고 운영하기 위한 플랫폼, 여기서는 Cloud Messaging 을 사용
 * -> TODO https://firebase.google.com/docs/reference/fcm/rest/v1/projects.messages/send 참고
 * -> TODO https://firebase.google.com/docs/cloud-messaging/fcm-architecture 참고
 * -> TODO https://firebase.google.com/docs/projects/learn-more 참고
 * -> firebase token 확인 ( 디바이스 식별자 역할 )
 * -> 일반, 확장형. 커스텀 알림
 * -> 알림 메세지 vs 데이터 메세지
 * -> 알림 메세지가 구현은 쉽지만 자유도?가 낮고 데이터 메세지가 구현은 어렵지만 자유도가 높다.?
 * -> 알림 메세지일 때 백그라운드면 onMessageReceived 가 호출되지 않는다.
 * -> google-service.json 파일은 깃헙에 업로드 하지마라, 한 프로젝트를 연동할 수 있는 정보 가지고 있는 파일이니까
 * -> BoM Bill of Materials, firebase 관련 제품 중 dependencies 에 써줄 것들이 많은 버전을 호환성 체크를 자동으로 해준다.
 * -> 토큰 갱신에 대비한 onNewToken 필수
 * -> FirebaseMessagingService 상속 ->
 *  아래 매니페스트 코드는 MESSAGING_EVENT 가 발생하면 이 서비스에서 수신하겠다는 서비스를 만들 것이다.
 * -> 근데 서비스 같은 경우는 외부에 오픈해줄 수 있으니 android:exported="false"
 *     <service android:name=".p9_push.MyFirebaseMessagingService"
 *              android:exported="false">
            <intent-filter>
                <action android:name="com.google.firebase.MESSAGING_EVENT"/>
            </intent-filter>
        </service>
 * -> https://developer.android.com/training/notify-user/custom-notification?hl=ko 맞춤 알림림 레이아웃 참고
 * -> 업데이트가 자주되는 분야이므로 호환성 체크를 위해 TODO https://developer.android.com/guide/topics/ui/notifiers/notifications?hl=ko 참고
 * -> Android 8.0(API 수준 26)부터는 모든 알림을 채널에 할당해야 합니다. 그렇지 않으면 에러발생 오히려 그 이하는 하면 에러..
 * -> 중요도 수준 설정 꼭 해줘야한다! TODO https://developer.android.com/training/notify-user/channels?hl=ko
 * android:textIsSelectable="true" 를 주면 길게 눌러서 복사하는 기능을 사용할 수 있다.
 * start 와 left 의 차이? 왼쪽에서 시작하는 언어 오른쪽에서 시작하는 언어 둘 다 지원하기 위해 start, end 가 존재한다.
 * -> 메니페스트에 android:supportsRtl="true" 이 그 의미이다. 그래서 left right 보다 start, end 쓰는 게 혹시모를 아랍어 앱 제작에 대비..?
 * -> TODO https://developer.android.com/training/basics/supporting-devices/languages 참고
 * -> android:layoutDirection="ltr" 속성이 왼->오 rtl 로 하면 아랍어마냥 start 가 오른쪽이다.
 *
 * PendingIntent
 * -> 내가 직접 다루는 인텐트가 아니라 다른 누군가한테, 여기서는 notification manager 한테 권한을 준다고 생각하면 된다.
 * -> TODO https://developer.android.com/reference/android/app/PendingIntent 참고
 *
 * private fun updateResult(isNewIntent: Boolean = false) 센스
 *
 * notification 구현 방식은 회사마다 천차만별이니 여기서 배운 것을 토대로 실험 많이 해볼것것 *
 */
class PushActivity: AppCompatActivity() {
    private val resultTextView: TextView by lazy {
        findViewById(R.id.push_text_view_result)
    }
    private val firebaseToken: TextView by lazy {
        findViewById(R.id.push_text_view_token)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_push)
        initFirebase()
        updateResult()
    }
    private fun initFirebase() {
        FirebaseMessaging.getInstance().token
            .addOnCompleteListener { task ->
                // 파이어베이스 메세지의 토큰 가져와서 테스크 완료됨을 리스너로
                if (task.isSuccessful) {
                    firebaseToken.text = task.result
                }
            }
    }

    @SuppressLint("SetTextI18n")
    private fun updateResult(isNewIntent: Boolean = false) {
        // 앱이 꺼져있다가 실행된 건지, 켜져있는데 알림 눌러서 갱신 된 것인지 알려주는 인자
        // 아이콘을 눌러서 들어오면 notificationType 의 밸류가 null 일 수 있으니 그에 대한 대처 ?:
        // createNotification 에서 넣어줬음 참고
        resultTextView.text = (intent.getStringExtra("notificationType") ?: "앱 런처") +
        if(isNewIntent) {
            "(으)로 갱신했습니다."

        } else {
            "(으)로 실행했습니다."
        }

   }

    // onNewIntent 가 호출되는 경우는 켜져있는데 알림 눌러서 갱신 된 것으로 간주 SINGLE_TOP 이니까 이미 그 화면인데 또 부르면 newIntent
    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        // 기존 onCreate 로 가져온 인텐트가 있기 때문에 setIntent 통해서 새로운 것으로 교체해야 데이터를 받을 수 있다.
        setIntent(intent)
        updateResult(true)
    }

}

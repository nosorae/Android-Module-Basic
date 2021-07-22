package nosorae.module_basic.p11_alarm

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import nosorae.module_basic.R


// Manifest 에 등록해야한다.
    //<receiver android:name=".p11_alarm.AlarmReceiver"
    //android:exported="false"/>
class AlarmReceiver: BroadcastReceiver() {
    companion object {
        const val NOTIFICATION_CHANNEL_ID = "1000"
        const val NOTIFICATION_ID = 100
    }
    // 인자 둘 다 null ? 표시를 떼어주었다.
    // context 란 이 앱의 상태나 맥락, 하는 일은 안드로이드 앱이 환경에서 글로벌 정보나 안드로이드 api, 시스템이 관리하고 있는 정보
    // (예를 들어 sharedPre~, resoure 파일) 이런 기능들을 접근할 때 필요한 객체, 근데 Activity 자체는 도화지라 했는데, Activity 에서는
    // 실행하고 있는 환경 자체가 이미 이런 기능들에 접근 하기 용이한 상태이기 때문에 Activity 자체가 context 라할 수 있음
    // Activity 가 실제로 context 를 상속하고도 있음,
    // 근데 브로드캐스트리시버 같은 경우는 백그라운드에서 브로드캐스트에서 새로운 펜딩인텐트가 넘어와서 실행이 되는 기능이라서,
    // context 를 onReceive 함수에서 받아와야 한다.
    override fun onReceive(context: Context, intent: Intent) {
        createNotificationChannel(context) // 채널 만들고
        notifyNotification(context) // 알람
    }

    private fun createNotificationChannel(context: Context) {
        // 26 버전이 채널이 필요한 분기점
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                "기상 알람",
                NotificationManager.IMPORTANCE_HIGH // 이 채널은 중요하다는 것을 명시 알림이 DEFAULT 일 때와 다르다
            )
            NotificationManagerCompat.from(context).createNotificationChannel(notificationChannel)
        }


    }
    private fun notifyNotification(context: Context) {
        with(NotificationManagerCompat.from(context)) {
            val builder = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
                .setContentTitle("알람")
                .setContentText("설정한 시간이 되었습니다.")
                .setSmallIcon(R.drawable.ic_push_notification)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
            notify(NOTIFICATION_ID, builder.build())
        }

    }

}
package nosorae.changed_name.p9_push

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import nosorae.changed_name.R

class MyFirebaseMessagingService: FirebaseMessagingService(){

    // 토큰이 언제든 변경될 수 있기 때문에 (새기기에서 앱 복원, 삭제-재설치, 앱데이터 소거 등)
    // 그래서 실제 라이브 서비스를 지원할 때는 토큰이 갱신될 때마다 해당 토큰을 갱신하는 함수를 꼭 사용해야한다.
    override fun onNewToken(p0: String) {
        super.onNewToken(p0)
    }
    // 메니패스트에 등록 여기서 메세지 받았을 때의 처리를 해준다.
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        // 8 이상부터 채널 만들어서 앱마다 따로 관리해서 만드는 듯?
        createNotificationChannel()

        //val title = message.data.get("title")
        val type = remoteMessage.data["type"]
            ?.let {
            // ENUM 값을 전달해서 데이터 가져오기
            NotificationType.valueOf(it)
            }
        type ?: return
        val title = remoteMessage.data["title"]
        val message = remoteMessage.data["message"]



        NotificationManagerCompat.from(this)
            .notify(type.id, createNotification(type, title, message))
    }
    private fun createNotification(
        type: NotificationType,
        title: String?,
        message: String?)
    : Notification {
        val intent = Intent(this, PushActivity::class.java).apply {
            putExtra("notificationType", "${type.title} 타입")
            // A, B 쌓여있는데 또 B 줬을 때 또 쌓이면 A, B, B 가 되잖아 근데 SINGLE_TOP 주면 A, B 로 할 수 있다.
            addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        }
        // https://developer.android.com/reference/android/app/PendingIntent
        // 내가 직접 다루는 인텐트가 아니라 다른 누군가한테, 여기서는 notification manager 한테 권한을 준다고 생각하면 된다.
        // FLAG_UPDATE_CURRENT 는 PendingIntent 에 정의된 상수. import 해준다.
        // If the creating application later re-retrieves the same kind of PendingIntent (same operation, same Intent action, data, categories, and components, and same flags), it will receive a PendingIntent representing the same token if that is still valid, and can thus call cancel() to remove it.
        // different request code integers supplied to getActivity(Context, int, Intent, int), getActivities(Context, int, Intent[], int), getBroadcast(Context, int, Intent, int), or getService(Context, int, Intent, int).
        // 위 두 문장이 두 번째 인자에 type.id 로 메세지 타입별로 PendingIntent 를 만드는 이유이다.
        val pendingIntent = PendingIntent.getActivity(this, type.id, intent, FLAG_UPDATE_CURRENT)


        val notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_push_notification)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent) // 클릭했을 때 intent 에 정의한 일을 한다.
            .setAutoCancel(true) // 알림 누르면 자동으로 사라진다.

        // 타입에 따라 추가 수행
       when(type) {
            NotificationType.NORMAL -> Unit
            NotificationType.EXPANDABLE -> {

                notificationBuilder
                    .setStyle(NotificationCompat.BigTextStyle()
                    .bigText("\uD83D\uDE00 \uD83D\uDE03 \uD83D\uDE04 \uD83D\uDE01 \uD83D\uDE06 \uD83D\uDE05 \uD83D\uDE02 \uD83E\uDD23 \uD83E\uDD72 ☺️ \uD83D\uDE0A \uD83D\uDE07 \uD83D\uDE42 \uD83D\uDE43 \uD83D\uDE09 \uD83D\uDE0C \uD83D\uDE0D \uD83E\uDD70 \uD83D\uDE18 \uD83D\uDE17 \uD83D\uDE19 \uD83D\uDE1A \uD83D\uDE0B \uD83D\uDE1B \uD83D\uDE1D \uD83D\uDE1C \uD83E\uDD2A \uD83E\uDD28 \uD83E\uDDD0 \uD83E\uDD13 \uD83D\uDE0E \uD83E\uDD78 \uD83E\uDD29 \uD83E\uDD73 \uD83D\uDE0F \uD83D\uDE12 \uD83D\uDE1E \uD83D\uDE14 \uD83D\uDE1F \uD83D\uDE15 \uD83D\uDE41 ☹️ \uD83D\uDE23 \uD83D\uDE16 \uD83D\uDE2B \uD83D\uDE29 \uD83E\uDD7A \uD83D\uDE22 \uD83D\uDE2D \uD83D\uDE24 \uD83D\uDE20 \uD83D\uDE21 \uD83E\uDD2C \uD83E\uDD2F \uD83D\uDE33 \uD83E\uDD75 \uD83E\uDD76 \uD83D\uDE31 \uD83D\uDE28 \uD83D\uDE30 \uD83D\uDE25 \uD83D\uDE13 \uD83E\uDD17 \uD83E\uDD14 \uD83E\uDD2D \uD83E\uDD2B \uD83E\uDD25 \uD83D\uDE36 \uD83D\uDE10 \uD83D\uDE11 \uD83D\uDE2C \uD83D\uDE44 \uD83D\uDE2F \uD83D\uDE26 \uD83D\uDE27 \uD83D\uDE2E \uD83D\uDE32 \uD83E\uDD71 \uD83D\uDE34 \uD83E\uDD24"))

            }
            NotificationType.CUSTOM -> {
                // https://developer.android.com/training/notify-user/custom-notification?hl=ko
                notificationBuilder
                    .setStyle(NotificationCompat.DecoratedCustomViewStyle())
                    .setCustomContentView(
                        RemoteViews(
                            packageName,
                            R.layout.push_view_custom_notification
                        ).apply {
                            setTextViewText(R.id.push_view_text_view_title, title)
                            setTextViewText(R.id.push_view_text_view_message, message)
                        }
                    )
            }
        }

        return notificationBuilder.build()
    }

    private fun createNotificationChannel() {
        // 8 미만은 채널 없으니 만들면 안됨, 그리고 여기서 IMPORTANCE 를 설정하니 8 미만에서는 setPriority 도 해주어야함
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT,
            )
            channel.description = CHANNEL_DESCRIPTION


            (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
                .createNotificationChannel(channel)

        }
    }
    companion object {
        private const val CHANNEL_NAME = "Emoji Party"
        private const val CHANNEL_DESCRIPTION = "Emoji Party 를 위한 채널"
        private const val CHANNEL_ID = "Channel ID"
    }

}
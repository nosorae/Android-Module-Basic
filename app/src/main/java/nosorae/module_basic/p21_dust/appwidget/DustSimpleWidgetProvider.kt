package nosorae.module_basic.p21_dust.appwidget

import android.Manifest
import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.view.View
import android.widget.RemoteViews
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.launch
import nosorae.module_basic.R
import nosorae.module_basic.p21_dust.data.Repository
import nosorae.module_basic.p21_dust.data.models.airquality.AirQualityGrade
import java.lang.Exception

class DustSimpleWidgetProvider : AppWidgetProvider() {
    override fun onUpdate(
        context: Context?,
        appWidgetManager: AppWidgetManager?,
        appWidgetIds: IntArray?
    ) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)
        ContextCompat.startForegroundService(
            context!!,
            Intent(context, UpdateWidgetService::class.java)
        )
    }

    class UpdateWidgetService : LifecycleService() {


        @RequiresApi(Build.VERSION_CODES.O)
        override fun onCreate() {
            super.onCreate()
            createChannelIfNeeded() // api level 26 이상에서는 채널 생성
            startForeground(
                NOTIFICATION_ID,
                createNotification()
            ) // 포그라운드 시작 onStartCommand 호출?


        }

        override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R &&
                ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                val updateViews = RemoteViews(packageName, R.layout.widget_dust_simple).apply {
                    setTextViewText( // TextView 의 텍스트를 변경하는 메서드
                        R.id.widget_dust_result,
                        "권한 없음"
                    )
                    setViewVisibility(R.id.widget_dust_text_label, View.GONE)
                    setViewVisibility(R.id.widget_dust_grade_label, View.GONE)
                }
                updateWidget(updateViews)
                stopSelf() // 권한 없으니 서비스 종료

                return super.onStartCommand(intent, flags, startId)
            }


            LocationServices.getFusedLocationProviderClient(this).lastLocation
                .addOnSuccessListener { location ->
                    lifecycleScope.launch {
                        try {
                            val nearbyMonitoringStation = Repository.getNearbyMonitoringStation(
                                location.latitude,
                                location.longitude
                            )
                            val measuredValue =
                                Repository.getLatestAirQualityData(nearbyMonitoringStation!!.stationName!!)


                            val updateViews =
                                RemoteViews(packageName, R.layout.widget_dust_simple).apply {
                                    setViewVisibility(R.id.widget_dust_text_label, View.VISIBLE)
                                    setViewVisibility(R.id.widget_dust_grade_label, View.VISIBLE)
                                    val currentGrade = (measuredValue?.khaiGrade ?: AirQualityGrade.UNKNOWN)
                                    setTextViewText(R.id.widget_dust_result, currentGrade.emoji)
                                    setTextViewText(R.id.widget_dust_grade_label, currentGrade.label)
                                }

                            updateWidget(updateViews)

                        } catch (e: Exception) {
                            e.printStackTrace()
                        } finally {
                            stopSelf()
                        }


                    }
                }

            return super.onStartCommand(
                intent,
                flags,
                startId
            ) // 이거 타고 들어가면 디폴트인 START_STICKY 인 것을 확인


        }


        @RequiresApi(Build.VERSION_CODES.O)
        private fun createNotification(): Notification =
            Notification.Builder(this)
                .setSmallIcon(R.drawable.ic_refresh)
                .setChannelId(WIDGET_REFRESH_CHANNEL_ID)
                .build()

        private fun updateWidget(updateViews: RemoteViews) {
            val widgetProvider = ComponentName(this, DustSimpleWidgetProvider::class.java)
            AppWidgetManager.getInstance(this).updateAppWidget(widgetProvider, updateViews)
        }


        private fun createChannelIfNeeded() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                (getSystemService(NOTIFICATION_SERVICE) as? NotificationManager)
                    ?.createNotificationChannel(
                        NotificationChannel(
                            WIDGET_REFRESH_CHANNEL_ID,
                            "위젯 갱신 채널",
                            NotificationManager.IMPORTANCE_LOW
                        )

                    )
            }
        }

        override fun onDestroy() {
            super.onDestroy()
            stopForeground(true)
        }

    }

    companion object {
        private const val WIDGET_REFRESH_CHANNEL_ID = "WIDGET_REFRESH_CHANNEL_ID"
        private const val NOTIFICATION_ID = 101
    }
}
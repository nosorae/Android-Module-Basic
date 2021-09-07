package nosorae.changed_name.p21_dust

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import nosorae.changed_name.databinding.ActivityDustBinding
import nosorae.changed_name.p21_dust.data.Repository
import nosorae.changed_name.p21_dust.data.models.airquality.AirQualityGrade
import nosorae.changed_name.p21_dust.data.models.airquality.MeasuredValue
import nosorae.changed_name.p21_dust.data.models.monitoringstation.MonitoringStation

/**
 * FusedLocationProviderClient 로 현재 위치 찾기
 *      ( LocationManager 보다 구글에서 더 권장하고 있는 라이브러리,google PlayService 는 4.1 이상부터는 Google PlayStore 를 통해 자동으로 업데이트된다. 그런데 Google PlayService 가 없을 수도 있으니 Google PlayStore 가능한지 체크한느 것이 좋다. )
 *      런타임 권한 요청 복습 - > TODO https://developer.android.com/training/location/permissions
 *      FusedLocationProviderClient -> TODO https://developers.google.com/android/reference/com/google/android/gms/location/FusedLocationProviderClient
 *      TODO FusedLocationProviderClient 와 LocationManager 의 장단점 알아보기기 *
 * kakao developers 의 좌표 변환 api
 *      https://developers.kakao.com/docs/latest/ko/local/dev-guide#trans-coord
 *
 * 공공데이터 포털에서 tm 좌표를 보내고 근접 미세먼지 측정소 조회 api
 *      https://www.data.go.kr/iim/api/selectAPIAcountView.do
 *
 * 측정소별 실시간 측정정보 조회 api
 *      https://www.data.go.kr/iim/api/selectAPIAcountView.do
 *
 * App Widgets 로 홈화면에 위젯 띄우기 usesCleartextTraffic="true" 를 application 단에 넣어준다.
 *      크게 세 가지 요소 필요
 *      AppWidgetProviderInfo : 위젯에 대한 정보를 담는 xml 파일, layout size 업데이트 주기, 초기에 보여줄 레이아웃 등등 // xml, <receiver <intent-filter> <meta-data> ... >
 *      AppWidgetProvider : 시스템에서 위젯을 달라는 이벤트가 발생했을 때 이를 수신해서 실제로 위젯을 제공하는 클래스, 이게 구현되어야 위젯 추가하려할 때 위젯이 보일 것이다. 갱신, 삭제, 활성화 비활성화 같은 이벤트를 받아서 핸들링 가능
 *      Layout : 리모트 뷰 즉 위젯의 뼈대를 담는 xml 파일, 사용할 수 있는 컴포넌트에 제약이 있다.
 *      안드 4.0 이상부터는 Bounding Box 와 frame 사이의 widget margins 는 자동으로 들어가서 신경 안 써도 무방
 *      그런데 디바이스마다 셀이 차지하는 정도가 다를 수 있으니 minWidth minHeight 를 설정
 *      공홈에서 셀 개수에 따른 사용가능한 크기(dp) 가 나와있으니 확인
 *      AppWidgetProviderInfo 에서 android:updatePeriodMillis 속성은 베터리 소모량에 영향을 줄 수 있음에 유의한다. 아무리 짧게해도 30분 미만은 안된다. 더 짧게 하고싶으면 알람매니저 활용?
 *      위젯 업데이트는 DustSimpleWidgetProvider 참고
 *
 *
 *
 * api key 저장 패턴
 *      gradle.properties + gradle app module 의 BuildConfig 에 buildConfigField "String", "KAKAO_API_KEY", project.properties["KAKAO_API_KEY"] 형식으로 저장장
 *
 *
 * rest api response 에서 gson 으로 파싱할 때 파싱에 문제가 생기면 null 정책이 다르기 때문에 Response 의 변수들을 nullable 로 설정해주는 것이 바람직하다.
 *
 * 한 줄로 되어있는 json 파일을 보기 좋게 만드는 법 : google 에 json formatter 검색
 *
 * List 의 minByNull 로 제일 작거나 원소가 0개인 경우 null 을 반환한다.
 *
 * http 도 허용하게 하기 위해,
 *
 * enum 으로 grade 에 들어오는 값에 따른 뷰 관리
 *
 * include 한 view 에 속한 view 들에 접근하는 방법
 *      include 한 view 에서 또 view 에 접근하면 된다.
 *      그래서 with(view 객체)  {} 로 하면 include 된 뷰 안의 여러 뷰에 접근할 때 편하다.
 *
 * Theme 에서 <item name="android:windowTranslucentStatus">true</item> 로 배경색과 일맥상통하는? status bar color 를 보여줄 수 있다.
 *      대신 기존 레이아웃이 상태바 밑으로 들어감에 유의
 *
 *
 * TODO https://developer.android.com/training/location/background 참고
 * TODO https://developer.android.com/guide/components/services?hl=ko 참고 백그라운드 포그라운드 위치 접근 서비스 이해 잘 못했다.
 * TODO https://developer.android.com/about/versions/11/privacy/location 참고
 *
 * 여기서는 한꺼번에 권한 요청했지만 실제 프로젝트에서는 UX 를 높이기 위해 포그라운드 요청을 먼저하고, 백그라운드 기능이 필요할 때 충분한 설명 후 요청해야한다.
 *
 * TODO 서비스 vs  스레드
 *
 *      안드로이드 서비스는 U.I 없이 백그라운드에서 실행되는 기능을 말합니다.
 *      서비스는 메인 스레드에서 실행됩니다. (서비스는 쓰레드가 아님, 별도의 프로세스도 아님)
 *      서비스가 CPU 자원을 많이 소모하는 작업이라면 서비스안에 스레드를 생성해서 작업하는게 좋습니다.
 *      앱이 실행중일때만 필요한 기능이라면 스레드를 사용하는게 맞고
 *      앱이 실행중이지 않을때 실행되어야 한다면 서비스를 이용해야 한다.
 *      서비스가 부모 어플리케이션이나 프로세스 상태와 무관하게 유지되는 백그라운드 작업처리 장치임에 비해
 *      안드로이드 스레드는 자바 경우와 같이 부모 프로세스가 살아있는 동안에
 *      runnable, running과 blocked 상태를 뱅글뱅글 거쳐
 *      일을 마치면 dead 상태에 들어가는 단순한 흐름을 갖고 있다.
 *      1. UI 스레드에서 시간이 요하는 태스크 처리가 필요하고, 계속 현재 UI 스레드가 foreground에서 놀고 있을 가능성이 많으면 간단히 별도 스레드를 만들거나 AsyncTask로 처리한다.
 *      2. 만약 부모 스레드가 더 이상 foreground가 아닐때에나, 그 스레드를 소유한 어플이 중지되었거나
 *      관계없이 백그라운드에서 서비스가 계속 살아있으면서 일을 해야 하면 서비스로 구현한다.
그리고는 서비스내에서 스레드를 만들어 서비스가 수행해야 하는 작업을 스레드가 담당하도록 한다.
작업량이 많거나 작거나 관계없이 별도 스레드에서 하도록 한다.
안전하게...   만약 어플리케이션의 UI 스레드가 돌고 있다고 생각되면 Handler 클래스를 이용해도 되고,
잘 모르겠으면 BroadcastReceiver 클래스를 이용하여 어플리케이션 UI를 관장하는 스레드에게 접근한다.
BroadcastReceiver 클래스는 돌고 있지 않던 프로세스도 깨울 수 있다.


 * 다른 종류의 두 퍼미션에 대해 퍼미션 코드 상수를 같게 해서 조금 삽질했다.
 * 그리고 api level 29 에서만 background location 권한을 설정하려 했는데 막상 위젯 뷰에서는 api level 을 체크하지 않아서 위젯이 정상적으로 나오지 않았었다.
 * 왜 그런지 모르겠지만 안되다가 되는 경우가 있었으니 TODO 다시 앱 빌드해서 확인해보자
 */

class DustActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDustBinding
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private var cancellationTokenSource: CancellationTokenSource? = null

    private val scope = MainScope() // 이 안에 들어있는 모든 코루틴들이 액티비티가 종료될 때 다 캔슬되어야한다.

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDustBinding.inflate(layoutInflater)
        setContentView(binding.root)

        bindViews()
        initVariables()
        requestLocationPermissions()

    }

    private fun bindViews() {
        binding.dustRefreshLayout.setOnRefreshListener {
            binding.dustProgressBar.visibility = View.VISIBLE
            binding.dustConstraintLayoutContent.alpha = 0f
            fetchAirQualityData()
        }
    }

    private fun initVariables() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

    }

    private fun requestLocationPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                android.Manifest.permission.ACCESS_COARSE_LOCATION,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ),
            REQUEST_ACCESS_LOCATION_PERMISSIONS
        )

    }



    @RequiresApi(Build.VERSION_CODES.Q)
    private fun requestBackgroundLocationPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(android.Manifest.permission.ACCESS_BACKGROUND_LOCATION),
            REQUEST_BACKGROUND_ACCESS_LOCATION_PERMISSIONS
        )

    }

    override fun onDestroy() {
        super.onDestroy()
        cancellationTokenSource?.cancel()
        scope.cancel()
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        val locationPermissionGranted =
            requestCode == REQUEST_ACCESS_LOCATION_PERMISSIONS &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED

        val backgroundLocationPermissionGranted =
            requestCode == REQUEST_BACKGROUND_ACCESS_LOCATION_PERMISSIONS &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED

        Log.e(
            "permission",
            "location : $locationPermissionGranted, background : $backgroundLocationPermissionGranted"
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Log.e("permission", "R 이상이다.")

            if (!backgroundLocationPermissionGranted) {
                requestBackgroundLocationPermissions()
            } else {
                fetchAirQualityData()
            }

        } else {
            if (locationPermissionGranted.not()) {
                finish()
            } else {
                fetchAirQualityData()
            }
        }

    }

    @SuppressLint("MissingPermission")
    private fun fetchAirQualityData() {
        // fetch
        // public Task<Location> getCurrentLocation (int priority, CancellationToken token)
        // 자주 요청할수록 베터리 소모를 절약하기 위해 낮은 우선순위를 줄 수 있겠다.
        Log.e("dust", "fetchAirQualityData 도달")
        cancellationTokenSource = CancellationTokenSource()
        Log.e("dust", "getCurrentLocation 도달")
        fusedLocationProviderClient.getCurrentLocation(
            LocationRequest.PRIORITY_HIGH_ACCURACY,
            cancellationTokenSource!!.token
        ).addOnSuccessListener { location ->
            scope.launch {
                Log.e("dust", "addOnSuccessListener 도달")
                binding.dustTextErrorDescription.visibility = View.GONE
                try {
                    Log.e("dust", "getNearbyMonitoringStation 시도")
                    val monitoringStation =
                        Repository.getNearbyMonitoringStation(location.latitude, location.longitude)

                    // 위가 null 이면 이미 에러이기 때문에 non-null assertion 표시
                    Log.e("dust", "getLatestAirQualityData 시도")
                    val measuredValue =
                        Repository.getLatestAirQualityData(monitoringStation!!.stationName!!)


                    displayAirQualityData(monitoringStation, measuredValue!!)
                    Log.e("dust", "addOnSuccessListener 완료")

                } catch (e: Exception) {
                    //Log.e("dust", "왜 이곳으로 오게 되었는가")
                    Log.e("dust", e.toString())
                    e.printStackTrace()
                    binding.dustTextErrorDescription.visibility = View.VISIBLE
                    binding.dustConstraintLayoutContent.alpha = 0f

                } finally {
                    binding.dustProgressBar.visibility = View.GONE
                    binding.dustRefreshLayout.isRefreshing = false
                }


            }
        }

    }

    @SuppressLint("SetTextI18n") // 다국어 처리에 대한 SuppressLint
    private fun displayAirQualityData(
        monitoringStation: MonitoringStation,
        measuredValue: MeasuredValue
    ) {

        binding.dustConstraintLayoutContent.animate()
            .alpha(1f)
            .setDuration(500L)
            .start()

        binding.dustTextStationName.text = monitoringStation.stationName
        binding.dustTextMeasuringStationAddress.text = monitoringStation.addr

        (measuredValue.khaiGrade ?: AirQualityGrade.UNKNOWN).let { grade ->
            binding.root.setBackgroundResource(grade.colorResId)
            binding.dustTextTotalGradeLabel.text = grade.label
            binding.dustTextTotalGradeEmoji.text = grade.emoji
        }

        with(measuredValue) {
            binding.dustTextFineDustInformation.text =
                "미세먼지 : $pm10Value ㎍/㎥ ${(pm10Grade ?: AirQualityGrade.UNKNOWN).emoji}"
            binding.dustTextUltraFineDustInformation.text =
                "초미세머지: $pm25Value ㎍/㎥ ${(pm25Grade ?: AirQualityGrade.UNKNOWN).emoji}"

            with(binding.dustSo2Item) {
                viewDustTextLabel.text = "아황산가스"
                viewDustTextGrade.text = (so2Grade ?: AirQualityGrade.UNKNOWN).toString()
                viewDustTextValue.text = "$so2Value ppm"
            }
            with(binding.dustCoItem) {
                viewDustTextLabel.text = "일산화탄소"
                viewDustTextGrade.text = (coGrade ?: AirQualityGrade.UNKNOWN).toString()
                viewDustTextValue.text = "$coValue ppm"
            }
            with(binding.dustO3Item) {
                viewDustTextLabel.text = "오존"
                viewDustTextGrade.text = (o3Grade ?: AirQualityGrade.UNKNOWN).toString()
                viewDustTextValue.text = "$o3Value ppm"
            }
            with(binding.dustNo2Item) {
                viewDustTextLabel.text = "이산화질소"
                viewDustTextGrade.text = (no2Grade
                    ?: nosorae.changed_name.p21_dust.data.models.airquality.AirQualityGrade.UNKNOWN).toString()
                viewDustTextValue.text = "$no2Value ppm"
            }

        }


    }


    companion object {
        private const val REQUEST_ACCESS_LOCATION_PERMISSIONS = 100
        private const val REQUEST_BACKGROUND_ACCESS_LOCATION_PERMISSIONS = 101
    }

}
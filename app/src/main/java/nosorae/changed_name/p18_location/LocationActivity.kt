package nosorae.changed_name.p18_location

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.*
import nosorae.changed_name.R
import nosorae.changed_name.databinding.ActivityLocationBinding
import nosorae.changed_name.p18_location.model.LocationLatLngEntity
import nosorae.changed_name.p18_location.model.LocationSearchResultEntity
import nosorae.changed_name.p18_location.utility.LocationRetrofitUtil
import kotlin.coroutines.CoroutineContext

/**
 * - 구글 맵 활용
 *      프로젝트 생성하고
 *      https://console.cloud.google.com/apis/dashboard 에 들어가서 키 제한사항을 등록한다.
 *      TODO https://developers.google.com/maps/documentation/android-sdk/overview?hl=ko 참고해서 개발
 *      fragment 뷰에  android:name="com.google.android.gms.maps.SupportMapFragment" 추가
 *
 * - 인텐트에 객체를 넣기 위해서 Parcelable 사용
 *
 *
 * - Retrofit 구현의 다른 버전 Service 의 함수에서 :Response<DTO 아이템> 으로 반환해서 그대로 변수로 받는다.
 *
 * - POI api 활용
 *      point of interest?
 *      https://tmapapi.sktelecom.com/
 *      https://openapi.sk.com/api/detailView
 *      TODO http://tmapapi.sktelecom.com/main.html#webservice/docs/tmapPoiSearch 참고
 *
 *
 * - Coroutines 활용
 *      아이오슬에 대해 돌려준다?
 *
 * - view binding 복습
 *      https://developer.android.com/topic/libraries/view-binding 참고
 *
 * - RecyclerView 의 layoutManager 를 프로그래머틱으로 정의하는 게 아니라 xml 에서도 정의할 수 있구나
 *       app:layoutManager="androidx.recyclerview.widget.LinearLayoutManager"
 *
 * - 이번에 권한 부분 학습이 미흡하다.
 *
 * - TODO 현재 내 위치 불러오는 코드가 정상동작하지 않는다. 코드 수정이 필요하다.
 *
 */
class LocationActivity : AppCompatActivity(), OnMapReadyCallback, CoroutineScope {
    private lateinit var binding: ActivityLocationBinding
    private lateinit var searchResult: LocationSearchResultEntity
    private lateinit var map: GoogleMap
    private var currentSelectMarker: Marker? = null
    private lateinit var locationManager: LocationManager // 안드로이드에서 위치정보를 불러올 떄 관리해주는 유틸리티 클래스
    private lateinit var myLocationListener: MyLocationListener

    private lateinit var job: Job
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job // 어떤 스레드에서 기본동작할지 명시해줘야한다.

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLocationBinding.inflate(layoutInflater)
        setContentView(binding.root)


        //bindViews()

//        if (::searchResult.isInitialized.not()) {
//            intent?.let {
//                searchResult =
//                    it.getParcelableExtra<LocationSearchResultEntity>(SEARCH_RESULT_EXTRA_KEY)
//                        ?: throw Exception("데이터가 존재하지 않습니다.")
//                setupGoogleMap()
//            }
//        }
        setupGoogleMap()
        loadReverseGeoInformation(LocationLatLngEntity((37.586697).toFloat(), (126.987489).toFloat()))

    }

    private fun bindViews() = with(binding) {
        locationFloatingButtonCurrent.setOnClickListener {
            Log.d("floating", "플로팅 버튼 클릭됨")
            getMyLocation()
        }
    }

    private fun getMyLocation() {
        if (::locationManager.isInitialized.not()) {
            locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        }
        val isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        if (isGpsEnabled) {
            if ((ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED) || (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED)
            ) {
                // 권한 없는 상태
                Log.d("floating", "권한이 없어서 requestPermission 호출")
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ),
                    PERMISSION_REQUEST_CODE
                )

            } else {
                Log.d("floating", "권한 있음. setMyLocationListener 호출")
                // 현재 내 위치 불러오기
                setMyLocationListener()
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        Log.d("floating", "onRequestPermissionsResult 도착")
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                setMyLocationListener()
                Log.d("floating", "onRequestPermissionsResult 에서 권한 확인 완료, setMyLocationListener 호출")

            } else {
                Toast.makeText(this, "권한을 받지 못했습니다.", Toast.LENGTH_SHORT).show()
                Log.d("floating", "onRequestPermissionsResult 에서 권한을 받지 못함을 확인")
            }
        }
    }

    @SuppressLint("MissingPermission") // 이미 셀프로 permission 체크 했다.
    private fun setMyLocationListener() {
        val minTime = 1500L
        val minDistance = 100f
        if (::myLocationListener.isInitialized.not()) {
            myLocationListener = MyLocationListener()
        }


        with(locationManager) {
            Log.d("floating", "setMyLocationListener 에서 requestLocationUpdates 호출 onLocationChanged 에서 listen")
            requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                minTime,
                minDistance,
                myLocationListener
            )
            requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                minTime,
                minDistance,
                myLocationListener
            ) // 이거 왜 두번하지??
        }


    }

    private fun onCurrentLocationChanged(locationLatLngEntity: LocationLatLngEntity) {
        Log.d("floating", "onCurrentLocationChanged 도착 loadReverseGeoInformation 호출")
        map.moveCamera(
            CameraUpdateFactory.newLatLngZoom(
                LatLng(
                    locationLatLngEntity.lat.toDouble(),
                    locationLatLngEntity.lng.toDouble()
                ), CAMERA_ZOOM_LEVEL
            )
        )

        loadReverseGeoInformation(locationLatLngEntity)
        removeLocationListener() // 내 위치가 바뀌면 제거
    }

    private fun loadReverseGeoInformation(locationLatLngEntity: LocationLatLngEntity) {
        Log.d("floating", "loadReverseGeoInformation 들어옴")
        launch(coroutineContext) {
            Log.d("floating", "loadReverseGeoInformation 에서 코루틴 들어온 직후")
            try {
                withContext(Dispatchers.IO) {
                    Log.d("floating", "loadReverseGeoInformation 에서 api 호출 직전")
                    val response = LocationRetrofitUtil.apiService.getGeoLocation(
                        lat = locationLatLngEntity.lat.toDouble(),
                        lon = locationLatLngEntity.lng.toDouble()
                    )
                    Log.d("floating", "loadReverseGeoInformation 에서 api 호출 중")
                    if (response.isSuccessful) {
                        val body = response.body()

                        withContext(Dispatchers.Main) {
                            Log.e("floating", body.toString())
                            body?.let {
                                currentSelectMarker = setUpMarker(LocationSearchResultEntity(
                                    fullAdress = it.addressInfo.fullAddress ?: "주소 정보 없음",
                                    buildingName = it.addressInfo.buildingName ?: "빌딩 이름 없음",
                                    locationLatLng = locationLatLngEntity
                                ))
                                currentSelectMarker?.showInfoWindow() // 현재 설정된 마커를 뿌려줄 수 있게
                            }

                        }

                    }

                }

            } catch (e: Exception) {
                Log.e("floating", e.toString())
                e.printStackTrace()
                Toast.makeText(this@LocationActivity, "현재위치를 불러오지 못했습니다.", Toast.LENGTH_SHORT).show()

            }
        }


    }


    private fun removeLocationListener() {
        if (::locationManager.isInitialized && ::myLocationListener.isInitialized) {
            locationManager.removeUpdates(myLocationListener)
        }
    }

    inner class MyLocationListener : LocationListener {
        override fun onLocationChanged(location: Location) {
            Log.d("floating", "onLocationChanged 도착 onCurrentLocationChanged 호출")
            val locationLatLngEntity = LocationLatLngEntity(
                location.latitude.toFloat(),
                location.longitude.toFloat()
            )

            onCurrentLocationChanged(locationLatLngEntity)
        }
    }

    private fun setupGoogleMap() {
        val mapFragment =
            supportFragmentManager.findFragmentById(R.id.location_fragment_google_map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(map: GoogleMap) {
        this.map = map
        // 여기서 구글맵 객체를 받아와서 사용할 수 있다.
        if (::searchResult.isInitialized) {
            currentSelectMarker = setUpMarker(searchResult)
            currentSelectMarker?.showInfoWindow()
        }



    }

    private fun setUpMarker(searchResult: LocationSearchResultEntity): Marker {
        val positionLatLng = LatLng(
            searchResult.locationLatLng.lat.toDouble(),
            searchResult.locationLatLng.lng.toDouble()
        )

        val markerOptions = MarkerOptions().apply {
            position(positionLatLng)
            title(searchResult.buildingName)
            snippet(searchResult.fullAdress)
        }
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(positionLatLng, CAMERA_ZOOM_LEVEL))

        return map.addMarker(markerOptions)

    }

    companion object {
        const val SEARCH_RESULT_EXTRA_KEY: String = "SEARCH_RESULT_EXTRA_KEY"
        private const val CAMERA_ZOOM_LEVEL = 17f
        private const val PERMISSION_REQUEST_CODE = 101
    }
}
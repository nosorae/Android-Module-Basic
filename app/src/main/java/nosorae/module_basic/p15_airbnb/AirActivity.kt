package nosorae.module_basic.p15_airbnb

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.*
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.Overlay
import com.naver.maps.map.util.FusedLocationSource
import com.naver.maps.map.util.MarkerIcons
import nosorae.module_basic.R
import nosorae.module_basic.databinding.ActivityAirBinding
import nosorae.module_basic.p15_airbnb.adapter.AirHouseRecyclerViewAdapter
import nosorae.module_basic.p15_airbnb.adapter.AirHouseViewPagerAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


/**
 * - Naver Map API 사용
 *      https://www.ncloud.com/product/applicationService/maps 접속
 *      TODO https://guide.ncloud-docs.com/docs/naveropenapiv3-maps-android-sdk-v3-start 참고
 *      MapView 자체로 사용하는 방법과 Fragment 와 사용하는 방법이 있다.
 *      Fragment 와 사용하면 좋은 점은 액티비티의 수명주기를 가져와서 사용을 알아서 해준다는 점.
 *      액티비티에서 사용할 때는 Fragment 방식을 그대로 사용하면되지만, Fragment 내에서 지도 사용할 때는 MapView 사용해서 붙여줘야한다는 제약이 있다? 뭔솔
 *      MapView 자체로 사용하면 좀 더 커스터마이징이 가능하지만 뷰에 액티비티의 수명주기를 그대로 넘겨줘야한다는 단점이 있다.
 *      여기서는 Activity 위에서 MapView 를 사용하는데, 생명주기를 다 연결해주는 것으로 초기화한다. 이것이 네이버의 권장사항
 *      마커 찍기
 *      naverMap.uiSettings.isLocationButtonEnabled 로 현재위치버튼 활성화 ( 매니페스트에 권한 설정 필수 6.0 이후부터 Popup 띄워서 선택하게 해야함 )
 *      권한 팝업을 구글에서 제공하는 라이브러리를 이용해서 더 쉽게 구현   implementation 'com.google.android.gms:play-services-location:18.0.0' 사용
 *      마커 관련은 이곳을 참고 https://navermaps.github.io/android-map-sdk/guide-ko/5-2.html
 *      네이버지도를 사용하고 있음을 알려야하는 약관 때문에 네이버로고를 꼭 보여야한다. 그래서 맵뷰에 마진바텀을 주게되었다..
 *
 *
 *
 * - FrameLayout 과 Coordinator Layout 사용
 *      FrameLayout 은 단일 레이아웃을 그릴 때 많이 사용 ( 액자의 사진을 생각하면 된다. 지금까지는 Fragment 영역을 미리 잡아놓을 때 사용했었다. )
 *      FrameLayout 은 뷰를 중첩이 가능해서 제일 하단 뷰가 위에 올라와서 겹쳐서 보여진다. ( 액자에 사진을 중첩해서 올려놓을 수 있음을 생각 )
 *      근데 뷰를 중첩하는 다른 방법이 많아서 안드로이드에서는 중첩은 권장하지 않음 단일 레이아웃을 그리는 것을 권장 다른 레이아웃으로도 중첩은 가능?
 *      Coordinator Layout 은 FrameLayout 이 확장된 뷰
 *      인터랙션이 중요할 때 사용 ( 스크롤과 동시에 뷰가 확장되는 경우를 예로 들 수 있다. )
 *      TODO https://developer.android.com/reference/androidx/coordinatorlayout/widget/CoordinatorLayout 참고
 *      <include layout="@layout/support_simple_spinner_dropdown_item"/> 로 실제로는 없지만 있는 것처럼 사용할 수 있음
 *      app:layout_behavior="com.google.android.material.bottomsheet.BottomSheetBehavior" // Coordinator Layout 안에서만 쓸 수 있다
        app:behavior_peekHeight="100dp" 하단에 몇 dp 까지 튀어나와 있을지 지정해주는 속성
 *      BottomSheetDialog 로 활용하고 싶다면 여기서 쓴 layout 을 인플레이트해서 Dialog 를 구현
 *
 * - Coordinator Layout와 BottomSheetBehavior 사용
 *
 * - Mock API 를 서버에 업로드하고 그 API 를 Retrofit 으로 가져오기 -> 지도에 마커로 찍고 바텀다이얼로그에 목록을 보여줄 예정
 *      mocky 웹사이트 - new mock - jason 작성 - 주소 받아오기 - Retrofit 으로 jason 받아오기
 *      picsum 사용
 *
 *
 * - 외부로 공유하기
 *      action = Intent.ACTION_SEND
 *      putExtra(Intent.EXTRA_TEXT, "[지금 이 가격에 예약하세요!]
 *      type = "text/plain" // 이렇게 하면 text/plain 을 받는 모든 앱들이 나오게된다.
 *      startActivity(Intent.createChooser(intent, null))
 *      TODO ( 딥링크기능을 구현하지 않아서 이 앱으로 다시 돌아와서 확인은 불가, 나중에 딥링크 알아보고 다시오자 )
 *
 * - 코드가 블록이 많이 중첩되어 오른쪽으로 들어가면 보기 안좋으니 function 으로 빼는 경우를 보았다.
 *
 * - ViewPager2
 *      프래그먼트 화면 전환에 많이 사용
 *      fragment state adapter 사용
 *      orientation 속성으로 가로세로를 결정할 수 있다.
 *      여기서는 View 를 한칸한칸씩 탭으로 만들기 위해 사용
 *      item View 는 layout_height 를 match_parent 로 설정해야한다. 디자인 시 tools:layout_height 를 임의로 줘서 보기편하게 디자인할 수 있다.
 *      ( Recycler View 였을 때는 wrap_content 로 주었던 것을 기억하라! )
 *      레이아웃이 다르면 adapter 를 재사용할 수 없다!! 왜냐하면 onCreateViewHolder 에서 갈리니까
 *
 * - dp 를 pixel 로 변환하는 법!
 *      TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp.toFloat(), context.resources.displayMetrics).toInt()
 *
 *
 *
 *
 *
 */
class AirActivity : AppCompatActivity(), OnMapReadyCallback, Overlay.OnClickListener {

    private lateinit var binding: ActivityAirBinding
    private lateinit var mapView: MapView
    private lateinit var naverMap: NaverMap
    private lateinit var locationSource: FusedLocationSource

    private val recyclerView: RecyclerView by lazy {
        findViewById(R.id.bottom_sheet_air_recycler_view)
    }
    private val bottomSheetTitleTextView: TextView by lazy {
        findViewById(R.id.bottom_sheet_air_text_view_title)
    }

    private val recyclerViewAdapter = AirHouseRecyclerViewAdapter()

    // 외부로 공유. 여기서는 Intent 의 츄저? 기능을 사용 -> 공유하기 기능을 받는 앱들이 자동으로 공유창이 나온다.
    private val viewPagerAdapter = AirHouseViewPagerAdapter(itemClicked = { model ->
        val intent = Intent()
            .apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, "[지금 이 가격에 예약하세요!] ${model.title} ${model.price} \n 사진보기 : ${model.imgUrl}")
                type = "text/plain" // 이렇게 하면 text/plain 을 받는 모든 앱들이 나오게된다.
            }

        startActivity(Intent.createChooser(intent, null))

    })

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAirBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mapView = binding.airMapView
        mapView.onCreate(savedInstanceState) // 생명주기 연결시켜주기 onCreate



        initRecyclerView()
        // 네이버맵 객체 가져오기 람다 or 콜백으로 가져오기
        // 콜백으로 구현하는 것은 OnMapReadyCallback 을 구현해서 인자로 전달하면 된다.
        // 여기서는 OnMapReadyCallback 을 액티비티 클래스에서 구현했으므로 this 를 전달했다.
        // 지도를 다 그린 다음에 마커를 찍어야함에 주의 -> 레트로핏으로 가져오는 것은 지도를 다 그린 후이다.
        mapView.getMapAsync(this)
        initViewPager()




    }

    override fun onMapReady(map: NaverMap) {
        naverMap = map
        naverMap.maxZoom = 18.0
        naverMap.minZoom = 10.0 // 이러면 줌아웃이 나라레벨까지 안간다.
        // 위경도 주소를 넣을 수 있는 LatLng
        val cameraUpdate = CameraUpdate.scrollTo(LatLng(37.650071, 127.242747))
        naverMap.moveCamera(cameraUpdate)

        // 현위치 버튼 활성화
        val uiSetting = naverMap.uiSettings

        // 원래있던 버튼 비활성화하고
        uiSetting.isLocationButtonEnabled = false
        binding.airLocationButtonCurrent.map = naverMap

        // 현재위치 권한 요청 코드
        locationSource =
            FusedLocationSource(this@AirActivity, LOCATION_PERMISSION_REQUEST_CODE) // 권한 요청
        naverMap.locationSource = locationSource

        // 마커 찍기
        val marker = Marker()
        marker.position = LatLng(37.507874, 127.057982)
        marker.map = naverMap
        marker.icon = MarkerIcons.BLACK
        marker.iconTintColor = Color.GRAY

        getHouseListFromAPI()


    }

    private fun getHouseListFromAPI() {
        // 레트로핏 객체 만들기
        val retrofit = Retrofit.Builder()
            .baseUrl("https://run.mocky.io/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        // 레트로핏 객체로 api 데이터 받아오기
        retrofit.create(AirHouseService::class.java).also {
            it.getHouseList()
                .enqueue(object : Callback<AirHouseDTO> {
                    @SuppressLint("SetTextI18n")
                    override fun onResponse(
                        call: Call<AirHouseDTO>,
                        response: Response<AirHouseDTO>
                    ) {
                        if (response.isSuccessful.not()) {
                            Log.d(LOG_TAG, "Retrofit : response.isSuccessful.not()")
                            return
                        }
                        response.body()?.let { dto ->
                            Log.d(LOG_TAG, "Retrofit : $dto")
                            updateMarker(dto)
                            viewPagerAdapter.submitList(dto.items)
                            recyclerViewAdapter.submitList(dto.items)
                            bottomSheetTitleTextView.text = "${dto.items.size} 개의 숙소"
                        }

                    }

                    override fun onFailure(call: Call<AirHouseDTO>, t: Throwable) {
                        Log.d(LOG_TAG, "Retrofit : onFailure")

                    }
                })
        }
    }

    private fun initViewPager() {
        binding.airViewPager.adapter = viewPagerAdapter

        binding.airViewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            // abstract static 이라 모든 함수를 구현할 필요는 없는 인터페이스이다.
            // onPageSelected 는 유저가 직접 옮길 때도 해당되지만, 코드로 옮길 때도 적용됨을 기억하라
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)

                val selectedModel = viewPagerAdapter.currentList[position]
                val cameraUpdate = CameraUpdate.scrollTo(LatLng(selectedModel.lat, selectedModel.lng)).animate(CameraAnimation.Easing)
                naverMap.moveCamera(cameraUpdate)


            }
        })


    }

    private fun initRecyclerView() {
        recyclerView.adapter = recyclerViewAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)

    }


    private fun updateMarker(dto: AirHouseDTO) {
        dto.items.forEach { house ->

            val marker = Marker()
            marker.position = LatLng(house.lat, house.lng)
            marker.icon = MarkerIcons.GRAY
            marker.iconTintColor = Color.BLACK
            // 리스너도 달 수 있다. 이 함수가 복잡해지는 것을 막기 위해 클래스에서 구현하고 this 를 넘겨주었다.
            marker.onClickListener = this
            marker.map = naverMap
            marker.tag = house.id

        }
    }

    override fun onClick(overlay: Overlay): Boolean {
        // overLay 는 마커의 총 집합

        // firstOrNull 은 제일 먼저나오는 아이템을 반환하고 없으면 null 을 반환하는 함수
        val selectedModel = viewPagerAdapter.currentList.firstOrNull {
            it.id == overlay.tag
        }

        selectedModel?.let {
            val position = viewPagerAdapter.currentList.indexOf(it)
            binding.airViewPager.currentItem = position

        }

        return true
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            return
        }

        if (locationSource.onRequestPermissionsResult(requestCode, permissions, grantResults)) {
            if (!locationSource.isActivated) {
                naverMap.locationTrackingMode =
                    LocationTrackingMode.None // 권한이 거부되었음을 네이버맵에 알려준 것이다.
            }
            return
        }

    }

    override fun onStart() {
        super.onStart()
        mapView.onStart()
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onStop() {
        super.onStop()
        mapView.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView.onSaveInstanceState(outState)
    }

    // 메모리가 얼마남지 않을 때 호출되는 함수
    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1000
        private const val LOG_TAG = "air"
    }

}
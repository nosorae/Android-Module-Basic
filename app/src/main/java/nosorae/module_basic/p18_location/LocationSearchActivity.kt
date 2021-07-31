package nosorae.module_basic.p18_location

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.*
import nosorae.module_basic.databinding.ActivityLocationSearchBinding
import nosorae.module_basic.p18_location.LocationActivity.Companion.SEARCH_RESULT_EXTRA_KEY
import nosorae.module_basic.p18_location.adapter.LocationRecyclerAdapter
import nosorae.module_basic.p18_location.model.LocationLatLngEntity
import nosorae.module_basic.p18_location.model.LocationSearchResultEntity
import nosorae.module_basic.p18_location.response.search.Poi
import nosorae.module_basic.p18_location.response.search.Pois
import nosorae.module_basic.p18_location.utility.LocationRetrofitUtil
import kotlin.coroutines.CoroutineContext

class LocationSearchActivity: AppCompatActivity(), CoroutineScope {
    private lateinit var job: Job
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main // 어떤 스레드에서 기본동작할지 명시해줘야한다.
    private lateinit var binding: ActivityLocationSearchBinding
    private lateinit var searchRecyclerAdapter: LocationRecyclerAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLocationSearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        job = Job()


        initAdapter()
        initViews()
        bindViews()

    }


    //
    private fun initViews() = with(binding) {
        // with 라는 스코프펑션으로 바인딩에 쉽게 접근
        locationTextEmptyResult.isVisible = false
        locationRecyclerView.adapter = searchRecyclerAdapter
        locationRecyclerView.layoutManager = LinearLayoutManager(this@LocationSearchActivity)

    }

    private fun bindViews() = with(binding) {
        locationButtonSearch.setOnClickListener {
            if (locationEditTextSearch.text.toString().isEmpty()) {
                return@setOnClickListener
            }
            searchKeyword(locationEditTextSearch.text.toString())

        }
    }

    private fun searchKeyword(keyword: String) {
        // 검색을 할 때 비동기로 io 스레드로 바꿨다가 다시 메인스레드로 바꿔줄 것이다.
        // launch(coroutineContext) 에서 coroutineContext 는 Main 이었으니 메인스레드로 시작하는 것을 알린다.
        launch(coroutineContext) {
            try {
                // IO 스레드로 전환
                withContext(Dispatchers.IO) {
                    //여기서 api 호출
                    val response = LocationRetrofitUtil.apiService.getSearchLocation(
                        keyWord = keyword
                    )
                    if (response.isSuccessful) {
                        val body = response.body()
                        withContext(Dispatchers.Main) {
                            Log.e("response", body.toString())

                            body.let { searchPoiInfo ->
                                setData(searchPoiInfo?.searchPoiInfo?.pois)


                            }

                        }
                    }
                }


            } catch (e: Exception) {
                e.printStackTrace()
            }

        }

    }


    private fun initAdapter() {
        searchRecyclerAdapter = LocationRecyclerAdapter()
    }

    private fun initData() {
        searchRecyclerAdapter.notifyDataSetChanged()

    }

    private fun setData(param: Pois?) {
        val pois = param ?: return

        val dataList = (pois.poi).map {
            LocationSearchResultEntity(
                buildingName = it.name ?: "빌딩이름 없음",
                fullAdress = makeMainAddress(it),
                locationLatLng = LocationLatLngEntity(it.noorLat.toFloat(), it.noorLon.toFloat())
            )
        }

        searchRecyclerAdapter.setSearchResultListener(dataList) {
            Toast.makeText(this, "아이템이 클릭되었습니다. \n ${it.buildingName} \n${it.fullAdress} \n ${it.locationLatLng}", Toast.LENGTH_LONG).show()

            startActivity(Intent(
                this,
                LocationActivity::class.java).apply {
                    putExtra(SEARCH_RESULT_EXTRA_KEY, it)
            })
        }
    }
    private fun makeMainAddress(poi: Poi): String =
        if(poi.secondNo?.trim().isNullOrEmpty()) {
            (poi.middleAddrName?.trim() ?: "") + " " +
            (poi.lowerAddrName?.trim() ?: "") + " "  +
            (poi.detailAddrname?.trim() ?: "") + " " +
            (poi.firstNo?.trim() ?: "") + " " +
            poi.secondNo?.trim()
        } else {
            poi.upperAddrName?.trim() + " " +
                    poi.middleAddrName?.trim() + " " +
                    poi.lowerAddrName?.trim() + " " +
                    poi.detailAddrname?.trim() + " " +
                    poi.firstNo?.trim() + " " +
                    poi.secondNo?.trim()
        }



}
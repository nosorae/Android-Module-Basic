package nosorae.module_basic.p18_location.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import nosorae.module_basic.databinding.ItemLocationBinding
import nosorae.module_basic.p18_location.model.LocationSearchResultEntity

class LocationRecyclerAdapter() :
    RecyclerView.Adapter<LocationRecyclerAdapter.SearchResultItemViewHolder>() {

    private var searchResultList: List<LocationSearchResultEntity> = listOf()
    lateinit var searchResultClickListener: (LocationSearchResultEntity) -> Unit

    // searchResultClickListener 는 클릭시 결과를 반환 받으려고 파라미터로 정의 Unit 을 이용하면 호출시 클릭리스너의 동작과 동일하게 구성 가능
    inner class SearchResultItemViewHolder(
        private val binding: ItemLocationBinding,
        val searchResultClickListener: (LocationSearchResultEntity) -> Unit
    ) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindData(data: LocationSearchResultEntity) = with(binding) {
            itemLocationTextBuildingName.text = data.buildingName
            itemLocationTextAddress.text = data.fullAdress
        }

        fun bindViews(data: LocationSearchResultEntity) {
            binding.root.setOnClickListener {
                searchResultClickListener(data)
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchResultItemViewHolder {
        val view = ItemLocationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SearchResultItemViewHolder(view, searchResultClickListener)
    }

    override fun onBindViewHolder(holder: SearchResultItemViewHolder, position: Int) {
        holder.bindData(searchResultList[position])
        holder.bindViews(searchResultList[position])
    }

    override fun getItemCount(): Int = searchResultList.size

    fun setSearchResultListener(searchResultList: List<LocationSearchResultEntity>, searchResultClickListener: (LocationSearchResultEntity) -> Unit) {
        this.searchResultList = searchResultList
        this.searchResultClickListener = searchResultClickListener
        notifyDataSetChanged()
    }


}
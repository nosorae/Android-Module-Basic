package nosorae.module_basic.p15_airbnb.adapter

import android.content.Context
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import nosorae.module_basic.databinding.ItemAirHouseRecyclerViewBinding
import nosorae.module_basic.databinding.ItemAirHouseViewPagerBinding
import nosorae.module_basic.p15_airbnb.AirHouseModel


class AirHouseRecyclerViewAdapter: ListAdapter<AirHouseModel, AirHouseRecyclerViewAdapter.ViewHolder>(diffUtil) {

    inner class ViewHolder(private val binding: ItemAirHouseRecyclerViewBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(model : AirHouseModel) {
            binding.itemAirHouseRecyclerViewTextViewTitle.text = model.title
            binding.itemAirHouseRecyclerViewTextViewPrice.text = model.price
            Glide.with(binding.itemAirHouseRecyclerViewImageView.context)
                .load(model.imgUrl) // 로드가 다 되면 비트맵 형식으로 이미지를 가져오게된다.
                .transform(CenterCrop(), RoundedCorners(dpToPx(binding.itemAirHouseRecyclerViewImageView.context, 15))) // 이렇게 글라이드 into 하기 전에 효과를 줄 수 있다.
                // 이렇게 하면 미리 센터크롭을 한 이미지를 이미지뷰에 넣을 수 있다. 가져와서 이미지뷰에서 센터크롭하는 것과 엄연히 다른 동작이다.
                // 여기서 RoundedCorners 의 인자 단위는 픽셀이라서 화면 핸드폰화면마다 다르게 나올 수 있다. 이것을 픽셀의 값으로 변환하는 함수를 만들겠다.
                .into(binding.itemAirHouseRecyclerViewImageView)

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder
            = ViewHolder(ItemAirHouseRecyclerViewBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    companion object {
        private val diffUtil = object : DiffUtil.ItemCallback<AirHouseModel>() {
            override fun areItemsTheSame(oldItem: AirHouseModel, newItem: AirHouseModel): Boolean
                    = oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: AirHouseModel, newItem: AirHouseModel): Boolean
                    = oldItem == newItem
        }
    }

    private fun dpToPx(context: Context, dp: Int): Int {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp.toFloat(), context.resources.displayMetrics).toInt()

    }
}
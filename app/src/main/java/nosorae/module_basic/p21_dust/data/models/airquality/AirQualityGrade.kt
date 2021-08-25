package nosorae.module_basic.p21_dust.data.models.airquality

import androidx.annotation.ColorRes
import com.google.gson.annotations.SerializedName
import nosorae.module_basic.R

// MeasuredValue 의 ~Grade 에 담긴 값에 따라 배정, 없는
enum class AirQualityGrade(
    val label: String,
    val emoji: String,
    @ColorRes val colorResId: Int // @ColorRes 어노테이션으로 나중에 인자로 들어온 값이 ColorRes 가 맞는지 확인해서 아니면 경고를 보여준다.
) {

    @SerializedName("1")
    GOOD("좋음", "💙", R.color.dust_blue),

    @SerializedName("2")
    NORMAL("보통", "💚", R.color.dust_green),

    @SerializedName("3")
    BAD("나쁨", "💛", R.color.dust_yellow),

    @SerializedName("4")
    AWFUL("매우나쁨", "❤", R.color.dust_red),


    UNKNOWN("미측정", "❌", R.color.dust_gray);

    override fun toString(): String {
        return "$label $emoji"
    }
}
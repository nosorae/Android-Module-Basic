package nosorae.module_basic.p12_book.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

// 서버에서 반환해주는 변수명과 내 데이터 클래스에서의 변수명을 맵핑하기 위해 SerializedName 사용
@Parcelize
data class Book(
    @SerializedName("itemId") val id: Long,
    @SerializedName("title") val title: String,
    @SerializedName("description") val description: String,
    @SerializedName("coverSmallUrl") val coverSmallUrl: String
): Parcelable

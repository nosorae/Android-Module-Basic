package nosorae.module_basic.p12_book.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class BookReview(
    @PrimaryKey val id: Int?,
    @ColumnInfo(name = "review") val review: String?
)
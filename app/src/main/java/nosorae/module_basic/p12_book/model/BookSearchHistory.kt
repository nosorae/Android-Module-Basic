package nosorae.module_basic.p12_book.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class BookSearchHistory(
    @PrimaryKey val uid: Int?,
    @ColumnInfo(name = "keyword") val keyword: String?
)

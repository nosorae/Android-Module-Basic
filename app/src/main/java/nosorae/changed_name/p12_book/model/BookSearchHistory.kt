package nosorae.changed_name.p12_book.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class BookSearchHistory(
    @PrimaryKey val uid: Int?,
    @ColumnInfo(name = "keyword") val keyword: String?
)

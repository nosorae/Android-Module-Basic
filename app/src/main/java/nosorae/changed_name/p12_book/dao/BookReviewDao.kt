package nosorae.changed_name.p12_book.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import nosorae.changed_name.p12_book.model.BookReview

@Dao
interface BookReviewDao {

    @Query("SELECT * FROM BOOKREVIEW WHERE id == :id")
    fun getOneReview(id: Int): BookReview?

    // 만약에 있다면, replace 하는 방식으로 삽입할 수도 있다.
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveReview(review: BookReview)
}
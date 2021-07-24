package nosorae.module_basic.p12_book.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import nosorae.module_basic.p12_book.model.BookSearchHistory

@Dao
interface BookSearchHistoryDao {

    // 삽입
    @Insert
    fun insertHistory(history: BookSearchHistory)

    // 조회
    @Query("SELECT * FROM BOOKSEARCHHISTORY")
    fun getAll(): List<BookSearchHistory>

    // 하나 이상 삭제
    @Query("DELETE FROM BOOKSEARCHHISTORY WHERE keyword == :keyword")
    fun delete(keyword: String)

}
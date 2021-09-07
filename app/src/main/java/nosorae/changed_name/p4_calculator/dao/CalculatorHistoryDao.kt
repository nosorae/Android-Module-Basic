package nosorae.changed_name.p4_calculator.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import nosorae.changed_name.p4_calculator.model.CalculatorHistory

// 룸에 연결된 Dao. 여기에는 엔터티를 저장 조회 삭제는 어떻게 할지 정리
@Dao
interface CalculatorHistoryDao {
    // 쿼리문 작성법 : @Query("여기에 쿼리문 작성")
    @Query("SELECT * FROM calculatorhistory")
    fun getAll(): List<CalculatorHistory>

    @Insert
    fun insertHistory(history: CalculatorHistory)

    @Query("DELETE FROM calculatorhistory")
    fun deleteAll()


    // 아래부터는 여기서 쓰진 않지만 룸에 대해 더 알아볼 수 있는 예시용

    @Delete
    fun delete(history: CalculatorHistory)

    @Query("SELECT * FROM calculatorhistory WHERE result LIKE :result")
    fun findByResult(result: String) : List<CalculatorHistory>

    @Query("SELECT * FROM calculatorhistory WHERE result LIKE :result LIMIT 1")
    fun findOneByResult(result: String) : CalculatorHistory

}
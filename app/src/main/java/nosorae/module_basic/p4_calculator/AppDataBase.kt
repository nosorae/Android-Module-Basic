package nosorae.module_basic.p4_calculator

import androidx.room.Database
import androidx.room.RoomDatabase
import nosorae.module_basic.p4_calculator.dao.CalculatorHistoryDao
import nosorae.module_basic.p4_calculator.model.CalculatorHistory

// 이전버전에서는 result 있고 다음버전은 result 가 expression에 통합된다고 했을 때
// 그럼 테이블 구조도 같이 바꿔야할 필요가 있으니 버전을 명시해야할 필요가 있다. (마이그레이션 코드)
@Database(entities = [CalculatorHistory::class], version = 1)
abstract class AppDataBase : RoomDatabase() {
    // 앱데이터 베이스 생성할 때 Dao 가져올 수 있도록 함
    abstract fun historyDao(): CalculatorHistoryDao

}
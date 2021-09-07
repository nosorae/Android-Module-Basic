package nosorae.changed_name.p4_calculator.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

// data class 라서 toString, equals, hashcode, 등등이 자동으로 생성된다.
// gradle 에 추가
// room의 데이터 클래스로 사용

@Entity
data class CalculatorHistory(
    @PrimaryKey val uid: Int?,
    @ColumnInfo(name = "expression") val expression: String?,
    @ColumnInfo(name = "result") val result: String?
)

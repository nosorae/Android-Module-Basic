package nosorae.module_basic.p4_calculator

import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewAnimationUtils
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.room.Room
import nosorae.module_basic.R
import nosorae.module_basic.p4_calculator.model.CalculatorHistory

/**
 * TableLayout (격자구조에 유용하게 쓰임)
 * -> android:shrinkColumns="*"
 * LayoutInflator (xml 파일을 메모리에 올려서 뷰에 할당할 수 있게 도와주는 것)
 * -> LinearLayout.removeAllViews()
 * -> LinearLayout.addView(View)
 * Room (local DB를 쓸 수 있게 도와줌, 이 과정에서 gradle 에 라이브러리 추가 하는 과정)
 * DB에 저장하는 작업 중에 Thread 를 사용하게 됨 (무거운 작업은 메인이외의 타 스레드를 만들어서 사용)
 * Thread 사용하다가 UI를 업데이트하려면 runOnUiThread 사용
 * 확장 함수 사용
 * data class 사용
 * 연산자 하나로 제한한 계산기 구현
 * android:enabled="false" 눌리지 않게하고, android:clickable="false" 로 클릭도 안되게하였다.
 * ShapeDrawable 에서 ripple 효과주는 법도 배웠다. 리플컬러와, item 에 id/background 로 기본 백그라운드컬러를 준다.
 * android:stateListAnimator="@null" 로 애니메이션을 없앨 수 있다.
 * app:layout_constraintVertical_weight 비율을 아래위 1대 1.5로 만드려고 사용 (서로 아래위로 연결되어있어야한다.)
 * android:shrinkColumns="*" 을 TableLayout 에 주면 삐져나오지 않게 딱 맞게 맞춰준다. android:shrinkColumns="0,1,2,3,4" 이렇게 넣어줘도 된다.
 * TODO https://kangchobo.tistory.com/29 참고
 *
 * text.dropLast(1) //맨 끝에서부터 n 자리 지워주는 함수
 * expressionText.last() // 리스트의 마지막 원소 반환환
 * SpannableStringBuilder 사용해서
 * fun String.isNumber(): Boolean 이런 게 바로 확장 함수
 * xml 파일의 뷰에 onClick 속성과 클래스 함수를 이어주었다.
 * */
class CalculatorActivity : AppCompatActivity() {
    private val expressionTextView: TextView by lazy {
        findViewById<TextView>(R.id.calculator_text_view_expression)
    }
    private val resultTextView: TextView by lazy {
        findViewById<TextView>(R.id.calculator_text_view_result)
    }

    //View에도 visibility 기능이 있기에 뷰로 받겠다.
    private val historyLayout: View by lazy {
        findViewById<View>(R.id.calculator_layout_history_container)
    }
    private val historyLinearLayout: LinearLayout by lazy {
        findViewById<LinearLayout>(R.id.calculator_layout_history_list)
    }

    lateinit var db :AppDataBase




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calculator)

        // 앱데이터베이스가 db 라는 변수에 들어간다.
        db = Room.databaseBuilder(
            applicationContext,
            AppDataBase::class.java,
            "historyDB"
        ).build()
    }

    private var isOperator = false
    private var hasOperator = false
    fun buttonClicked(v: View) {
        when (v.id) {
            R.id.calculator_button_number_0 -> numberButtonClicked("0")
            R.id.calculator_button_number_1 -> numberButtonClicked("1")
            R.id.calculator_button_number_2 -> numberButtonClicked("2")
            R.id.calculator_button_number_3 -> numberButtonClicked("3")
            R.id.calculator_button_number_4 -> numberButtonClicked("4")
            R.id.calculator_button_number_5 -> numberButtonClicked("5")
            R.id.calculator_button_number_6 -> numberButtonClicked("6")
            R.id.calculator_button_number_7 -> numberButtonClicked("7")
            R.id.calculator_button_number_8 -> numberButtonClicked("8")
            R.id.calculator_button_number_9 -> numberButtonClicked("9")
            R.id.calculator_button_modulus -> operatorButtonClicked("%")
            R.id.calculator_button_divide -> operatorButtonClicked("/")
            R.id.calculator_button_mutiply -> operatorButtonClicked("*")
            R.id.calculator_button_minus -> operatorButtonClicked("-")
            R.id.calculator_button_plus -> operatorButtonClicked("+")

        }

    }

    private fun numberButtonClicked(number: String) {
        // 연산자를 치다가 들어온 경우에는 띄어쓰기 해
        if (isOperator) {
            expressionTextView.append(" ")
        }
        isOperator = false

        val expressionText = expressionTextView.text.split(" ")
        if (expressionText.isNotEmpty() && expressionText.last().length >= 15) {
            Toast.makeText(this, "15자리 까지만 사용할 수 있습니다.", Toast.LENGTH_SHORT).show()
            return
        } else if (expressionText.last().isEmpty() && number == "0") {
            Toast.makeText(this, "첫 자리에 0이 올 수 없습니다.", Toast.LENGTH_SHORT).show()
            return
        }

        expressionTextView.append(number)
        resultTextView.text = calculateExpression()

    }

    private fun operatorButtonClicked(operator: String) {
        //val expressionText = expressionTextView.text.split(" ")
        if (expressionTextView.text.isEmpty()) {
            return
        }
        when {
            isOperator -> {
                val text = expressionTextView.text.toString()
                expressionTextView.text = text.dropLast(1) + operator //맨 끝에서부터 n 자리 지워주는 함수
            }
            hasOperator -> {
                Toast.makeText(this, "연산자는 한 번만 사용할 수 있습니다.", Toast.LENGTH_SHORT).show()
                return
            }
            else -> {
                expressionTextView.append(" $operator")
            }

        }

        val ssb = SpannableStringBuilder(expressionTextView.text)
        ssb.setSpan(
            ForegroundColorSpan(getColor(R.color.calculator_text_green)),
            expressionTextView.text.length - 1,
            expressionTextView.text.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        expressionTextView.text = ssb
        isOperator = true
        hasOperator = true

    }

    fun resultButtonClicked(v: View) {

        val expressionTexts = expressionTextView.text.split(" ")
        if (expressionTextView.text.isEmpty() ||  expressionTexts.size == 1) {
            return
        }

        if(expressionTexts.size != 3 && hasOperator) {
            Toast.makeText(this, "아직 완성되지 않은 수식입니다.", Toast.LENGTH_SHORT).show()
            return
        }

        if (expressionTexts[0].isNumber().not() || expressionTexts[2].isNumber().not()) {
            Toast.makeText(this, "오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
            return
        }

        val expressionText = expressionTextView.text.toString()
        val resultText = calculateExpression()


        Thread(Runnable {
            // 프라이머리키는 null 로 넣어도 자동으로 값이 들어가?
            db.historyDao().insertHistory(CalculatorHistory(null, expressionText, resultText))
            Log.d("calculator", "$expressionText = $resultText 저장 완료!")
        }).start()


        resultTextView.text= ""
        expressionTextView.text = resultText
        //계산 결과를 저장한다.
        isOperator = false
        hasOperator = false

    }

    private fun calculateExpression(): String {
        val expressionTexts = expressionTextView.text.split(" ")
        // operator 를 가지고 있으면서 요소는 3개 있고 첫번째와 세번째가 숫자일때만 연산하겠다는 예외처리
        if (hasOperator.not() || expressionTexts.size != 3) {
            return ""
        } else if (expressionTexts[0].isNumber().not() || expressionTexts[2].isNumber().not()) {
            return ""
        }
        // 여기 아래부터는 NumberFormatException 이 날 일 이 없다.
        val exp1 = expressionTexts[0].toBigInteger()
        val exp2 = expressionTexts[2].toBigInteger()
        val op = expressionTexts[1]
        return when (op) {
            "+" -> (exp1 + exp2).toString()
            "-" -> (exp1 - exp2).toString()
            "*" -> (exp1 * exp2).toString()
            "/" -> (exp1 / exp2).toString()
            "%" -> (exp1 % exp2).toString()
            else -> ""
        }

    }

    fun clearButtonClicked(v: View) {
        expressionTextView.text = ""
        resultTextView.text = ""
        isOperator = false
        hasOperator = false
    }

    fun historyButtonClicked(v: View) {
        historyLayout.isVisible = true

        historyLinearLayout.removeAllViews()
        historyLinearLayout.isVisible = true
        Thread(Runnable {
            // 디비에서 모든 기록 가져오기
            // 최신 것을 나중에 저장되어서 히스토리에서는 최신것을 위에 보여주기위해 reverse 를 사용했다.
            db.historyDao().getAll().reversed().forEach {
                // 리니어레이아웃에 뷰를 붙여준다.
                runOnUiThread {
                    // 리니어 레이아웃에 붙이는 거지만 addView 를 통해 붙일 거라서 일단 root 인자에 null 을 넣음
                    val historyView = LayoutInflater.from(this).inflate(R.layout.calculator_history_row, null, false)
                    historyView.findViewById<TextView>(R.id.calculator_text_view_history_expression).text = it.expression
                    historyView.findViewById<TextView>(R.id.calculator_text_view_history_result).text = " = ${it.result}"
                    Log.d("calculator", it.expression+" =  "+it.result+" 불러오기 완료!")
                    // 뷰에 모든 기록 할당하기LinearLayout 이니 자연스럽게 세로로 차곡차곡 들어가게 된다.
                    historyLinearLayout.addView(historyView)
                }
            }
        }).start()


    }

    fun historyClearButtonClicked(v: View) {
        historyLayout.isVisible = false
    }
    fun historyDeleteButtonClicked(v: View) {
        // 뷰에서 모든 기록 삭제
        historyLinearLayout.removeAllViews()

        // 디비에서 모든 기록 삭제
        Thread(Runnable {
            db.historyDao().deleteAll()
        }).start()
    }


}

fun String.isNumber(): Boolean {
    return try {
        this.toBigInteger()
        return true
    } catch (e: NumberFormatException) {
        return false
    }
}
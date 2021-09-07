package nosorae.changed_name.p2_lotto

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.NumberPicker
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import nosorae.changed_name.R
/*
ConstraintLayout
TODO https://developer.android.com/reference/androidx/constraintlayout/widget/ConstraintLayout 참고
NumberPicker 사용 (minValue, maxValue 를 설정해주어야한다.)
셰이프 드로어블
TODO https://developer.android.com/guide/topics/resources/drawable-resource?hl=ko 참고
TODO https://developer.android.com/guide/topics/resources/drawable-resource?hl=ko#Shape 참고
(코드로 되어있어서 이미지 파일을 넣는 것보다 용량이 작고, 수정이 용이하다는 장점이 있다.)
apply 함수 사용
Random 클래스 사용
TODO https://kotlinlang.org/api/latest/jvm/stdlib/kotlin.random/-random/ 참고
Collection 의 Set, List 사용
TODO https://kotlinlang.org/docs/collections-overview.html 참고
xml 에서 tools의 사용
in 이라는 범위연산자 when과 함께 사용해봄
lazy 초기화하는 이유는 액티비티 생성될 시점에는 뷰가 아직 그려지지 않았기 때문 뷰가 다 그려졌다고 알려주는 시점이 onCreate함수
그래서 onCreate에 그 변수를 써주면 초기화되는 것이다.
 */
class LottoActivity : AppCompatActivity() {
    private val clearButton: Button by lazy {
        findViewById<Button>(R.id.lotto_button_clear)
        //이걸 lotto_button_add로 했다가 클릭이 안돼서 삽질했다.
        //하나의 아이디로 두개의 setOnClickListener를 달아서 나중에 리스너 단 것이 실행된 것이다.
    }
    private val runButton: Button by lazy {
        findViewById<Button>(R.id.lotto_button_run)
    }
    private val addButton: Button by lazy {
        findViewById<Button>(R.id.lotto_button_add)
    }
    private val numberPicker: NumberPicker by lazy {
        findViewById<NumberPicker>(R.id.lotto_number_picker)
    }

    private val numberTextViewList: List<TextView> by lazy {
        listOf<TextView>(
            findViewById(R.id.lotto_text_view_num1),
            findViewById(R.id.lotto_text_view_num2),
            findViewById(R.id.lotto_text_view_num3),
            findViewById(R.id.lotto_text_view_num4),
            findViewById(R.id.lotto_text_view_num5),
            findViewById(R.id.lotto_text_view_num6)
        )
    }

    private var didRun = false

    private val pickNumberSet = hashSetOf<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lotto)
        //NumberPicker의 범위 설정
        numberPicker.minValue = 1
        numberPicker.maxValue = 45

        initRunButton()
        initAddButton()
        initClearButton()

    }
    private fun initRunButton() {
        runButton.setOnClickListener {
            val list = getRandomNumber()
            Log.d("Lotto", list.toString())

            //forEach로는 인덱스값을 알 수 없기 때문에 forEachIndexed함수를 사용한다.
            list.forEachIndexed { index, number ->
                val textView = numberTextViewList[index]
                textView.text = number.toString()
                textView.isVisible = true
                setNumberBackground(number, textView)
            }

            didRun = true

        }
    }

    private fun initAddButton() {
        addButton.setOnClickListener {
            Log.d("Lotto", "addButton 이 클릭되었습니다.")
            //예외처리에 주목하라
            //이미 자동생성을 했다면 버튼을 추가할 수 없다.
            if(didRun) {
                Toast.makeText(this, "초기화 후에 시도해주세요. ", Toast.LENGTH_SHORT).show()
                return@setOnClickListener //함수중첩이므로 어노테이션으로 어느 함수를 리턴할지 명시한다.
            }
            //꽉찼다면 번호를 추가할 수 없다. 6개가 최대인데 다 선택하면 난수의 의미가 없잖아,,
            if (pickNumberSet.size >= 5) {
                Toast.makeText(this, "번호는 5개까지만 선택할 수 있습니다. ", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            //중복방지
            if(pickNumberSet.contains(numberPicker.value)) {
                Toast.makeText(this, "이미 선택한 번호입니다. ", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            //이 이후에는 추가할 수 있다.
            val textView = numberTextViewList[pickNumberSet.size]
            textView.isVisible = true
            textView.text = numberPicker.value.toString()
            //드로어블이 안드로이드 앱에 저장되어있기 때문에 컨텍스트에서 가져온다.?
            setNumberBackground(numberPicker.value, textView)
            pickNumberSet.add(numberPicker.value)

        }
    }
    //중복코드를 함수로 만들어서 사용
    private fun setNumberBackground(number: Int, textView: TextView) {
        when(number) {
            in 1..10 ->  textView.background = ContextCompat.getDrawable(this, R.drawable.lotto_circle_yello)
            in 11..20 ->  textView.background = ContextCompat.getDrawable(this, R.drawable.lotto_circle_blue)
            in 21..30 ->  textView.background = ContextCompat.getDrawable(this, R.drawable.lotto_circle_red)
            in 31..40 ->  textView.background = ContextCompat.getDrawable(this, R.drawable.lotto_circle_gray)
            else ->  textView.background = ContextCompat.getDrawable(this, R.drawable.lotto_circle_green)
        }
    }

    private fun initClearButton() {
        clearButton.setOnClickListener {
            Log.d("Lotto", "clearButton 이 클릭되었습니다.")
            pickNumberSet.clear()
            numberTextViewList.forEach {
                it.isVisible = false
            }
            didRun = false
        }


    }

    // 리스트에 1부터 45까지 채우고 shuffle후 0~5인덱스로 subList를 만들고 sorted된 리스트를 반환
    private fun getRandomNumber(): List<Int> {
        val numberList = mutableListOf<Int>()
            .apply {
            for(i in 1..45) {
                if(pickNumberSet.contains(i)) {
                    continue
                }

                this.add(i)
            }
        }
        numberList.shuffle()

        //두 리스트를 + 연산자로 이어붙일 수도 있다.
        val newList = pickNumberSet.toList() + numberList.subList(0, 6 - pickNumberSet.size)
        Log.d("Lotto", "${newList.toString()}")
        return newList.sorted()
    }



}
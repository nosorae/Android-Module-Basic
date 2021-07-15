package nosorae.module_basic.p1_bmi

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import nosorae.module_basic.R
import kotlin.math.pow

class BMIResultActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bmi_result)

        //만든 클래스를 쓰려면 보여줄 xml파일과 manifest에 명시해주는 과정이 필요하다.
        val height = intent.getIntExtra("height", 0)
        val weight = intent.getIntExtra("weight", 0)

        Log.d("BMIResultActivity", "height : $height weight : $weight")

        val bmi = weight / (height / 100.0).pow(2.0) // Double 타입으로 맞춰준다.

        val resultText = when {
            bmi >= 35.0 -> "고도 비만"
            bmi >= 30.0 -> "중정도"
            bmi >= 25.0 -> "경도"
            bmi >= 23.0 -> "과체중"
            bmi >= 18.5 -> "정상체중"
            else -> "저체중"
        }

        val resultValueTextView = findViewById<TextView>(R.id.bmi_result_value)
        val resultStringTextView = findViewById<TextView>(R.id.bmi_result)

        resultValueTextView.text = bmi.toString()
        resultStringTextView.text = resultText


    }
}
package nosorae.module_basic.p1_bmi

//다른 파일을 가져와서 이 클래스에서 사용하겠다는 말이다.
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import nosorae.module_basic.R

class BMIActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //이름을 붙이면 R이라는 파일에 저장된다. 그것을 가져온다.
        setContentView(R.layout.activity_bmi) //실제로는 1300036이라고 저장되어있지만 우리가 항상 기억할 수 없기에 치환해서 표현

        //두 가지 방식으로 EditText라는 것을 알려주고 뷰를 참조할 수 있다.
        val heightEditText: EditText = findViewById(R.id.heightEditText) //마우스 갖다대면 1000233 확인할 수 있다.
        val weightEditText = findViewById<EditText>(R.id.weightEditText)

        val resultButton = findViewById<Button>(R.id.resultButton)

        //람다함수로 setOnClickListener 사용
        resultButton.setOnClickListener {
            //로그뒤에 붙는 d e i v이런 것들은 로그의 수준이다.
            Log.d("BMI", "ResultButton이 클릭되었습니다.")
            if(heightEditText.text.isEmpty() || weightEditText.text.isEmpty()) {
                Toast.makeText(this, "빈 값이 있습니다.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener //함수 두개 중첩되어있기 때문에 어느 함수를 나갈지 어노테이션으로 명시
            }
            // 이 아래로는 빈값이 절대 올 수 없다.
            val height: Int = heightEditText.text.toString().toInt() //빈값을 넣으면 toInt()에서 에러가 난다.
            val weight: Int = weightEditText.text.toString().toInt()
            Log.d("BMI", "height : $height weight : $weight")

            //인텐트는 두가지 인자, 전액티비티, 다믐으로 넘어갈 액티비티 클래스 이름
            //TODO https://developer.android.com/guide/components/intents-filters 참조
            //만든 클래스를 쓰려면 보여줄 xml파일과 manifest에 명시해주는 과정이 필요하다.
            //왜냐하면 intent를 Android System에 보내서 해당 Activity를 찾아서 보여주는데 해당 Activity를 manifest에서 찾기 때문이다.
            //manifest에서 해당 Acitivty를 찾으면 onCreate함수에 Intent를 전달해준다.
            val intent = Intent(this, BMIResultActivity::class.java)
            intent.putExtra("height", height)
            intent.putExtra("weight", weight)
            startActivity(intent)



        }


    }
}
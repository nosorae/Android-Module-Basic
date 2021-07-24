package nosorae.module_basic.p12_book

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import androidx.room.migration.Migration
import com.bumptech.glide.Glide
import nosorae.module_basic.databinding.ActivityBookDetailBinding
import nosorae.module_basic.p12_book.model.Book
import nosorae.module_basic.p12_book.model.BookReview

class BookDetailActivity: AppCompatActivity() {

    private lateinit var binding: ActivityBookDetailBinding
    private lateinit var db: BookAppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBookDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // db 만들기 ( BookActivity 의 코드 그대로 가져온 것 )
        db = getAppDatabase(this)


        val model = intent.getParcelableExtra<Book>("bookModel")
        // getParcelableExtra 이게 실제로 인텐트로 넘어왔을 수도 있고 아닐 수도 있기에 null 일 수도 있음에 대비
        Log.d("book", "model : ${model?.toString().orEmpty()}")
        // 근데 model 이 null 이면 애초에 거기서 끊겨서 orEmpty 못가는 거 아니야?? model 은 null 이 아니지만 title 이 null 일 때를 대비?
        binding.bookDetailTextViewTitle.text  = model?.title.orEmpty()
        binding.bookDetailTextViewDescription.text = model?.description.orEmpty()
        Glide
            .with(binding.bookDetailImageViewCover.context)
            .load(model?.coverSmallUrl.orEmpty())
            .into(binding.bookDetailImageViewCover)

        Thread {
            // id 를 찾았는데 가져오지 못한경우 review 가 null 일 수 있음을 인지
            // 즉 DB 작업을 할 때는 쿼리한 결과가 없을 수도 있음을 인지하고 코드를 작성한다.
            // getOneReview 를 nullable 로 하지 않아서 리뷰가 써있지 않은 책의 id 로 BookReview 객체를 가져오려하면 앱이 터졌다.
            val review = db.reviewDao().getOneReview(model?.id?.toInt() ?: 0)
            Log.d("book", "review : ${review?.toString() ?: "review 가 null 이네요"}")
            review ?: return@Thread
            runOnUiThread {
                binding.bookDetailEditTextReview.setText(review?.review.orEmpty())
            }
        }.start()

        binding.bookDetailButtonSave.setOnClickListener {
            Thread {
                db.reviewDao().saveReview(
                    BookReview(
                        model?.id?.toInt() ?: 0,
                        binding.bookDetailEditTextReview.text.toString()
                    )
                )
                runOnUiThread {
                    Toast.makeText(this, "리뷰가 저장되었습니다.", Toast.LENGTH_SHORT).show()
                }
            }.start()
        }

    }
}
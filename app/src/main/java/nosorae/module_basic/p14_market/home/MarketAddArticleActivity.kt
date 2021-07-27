package nosorae.module_basic.p14_market.home

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.google.android.gms.common.internal.Objects
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import nosorae.module_basic.databinding.ActivityMarketAddArticleBinding
import nosorae.module_basic.p14_market.MarketDBKey.Companion.ARTICLES
import nosorae.module_basic.p14_market.MarketDBKey.Companion.DB_MARKET

/**
 * EXTERNAL_STORAGE 권한 추가
 * 데이터베이스에 업로드
 */
class MarketAddArticleActivity: AppCompatActivity() {
    private lateinit var binding: ActivityMarketAddArticleBinding
    private var selectedUri: Uri? = null
    private val auth: FirebaseAuth by lazy {
        Firebase.auth
    }
    private val storage: FirebaseStorage by lazy {
        Firebase.storage
    }
    private val articleDB: DatabaseReference by lazy {
        Firebase.database.reference.child(DB_MARKET).child(ARTICLES)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMarketAddArticleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initAddArticleButton()
        initAddImageButton()
    }
    private fun initAddArticleButton() {
        binding.fragmentMarketAddArticleButtonSubmit.setOnClickListener {
            showProgress()
            val title = binding.fragmentMarketAddArticleEditTextTitle.text.toString()
            val price = binding.fragmentMarketAddArticleEditTextPrice.text.toString()
            val sellerId = auth.currentUser?.uid.orEmpty()

            // 이미지가 있으면 업로드 과정을 추가
            if (selectedUri != null) {
                val photoUri = selectedUri ?: return@setOnClickListener // 물론 리턴될 일은 거의 없다. (다른 스레드에서 건드리는 경우정도..)
                uploadPhoto(selectedUri!!,
                    successHandler = { uri ->
                        uploadArticle(sellerId, title, price, uri)
                    },
                    errorHandler = {
                        Toast.makeText(this, "사진 업로드에 실패했습니다.", Toast.LENGTH_SHORT).show()
                        hideProgress()
                    })
            } else {
                Toast.makeText(this, "", Toast.LENGTH_SHORT).show()
                uploadArticle(sellerId, title, price, "")
            }

        }

    }


    private fun initAddImageButton() {
        binding.fragmentMarketAddArticleButtonImageAdd.setOnClickListener {
            when {
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED -> {
                            startContentProvider()

                        }
                shouldShowRequestPermissionRationale(android.Manifest.permission.READ_EXTERNAL_STORAGE) -> {
                    showPermissionContextPopup()
                }
                else -> {
                    requestPermissions(arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), REQUEST_CODE)
                }
            }
        }
    }

    private fun uploadPhoto(uri: Uri, successHandler: (String) -> Unit, errorHandler: () -> Unit) {
        // 다른서버에서 겹치지 않는 이름이 되면 좋겠다.
        // 랜덤한 String 을 만들어서 올리는 방법도 있을거고
        // uid 랑 현재시간 조합해서 키를 만들어도 되겠다. 여기서는 유저가 나 하나니까 그냥 시간으로 해도 중복 없음
        val fileName = "${System.currentTimeMillis()}.png"
        storage.reference.child("market/article/photo").child(fileName).putFile(uri).addOnCompleteListener {
                if (it.isSuccessful) {
                    Log.d("market", "storage 에 이미지가 업로드 됨")
                    // storage 에 업로드 완료된 경우, 이제 해당 파일로 가서 download url 을 가져온다. 그것에 성공함을 나타내는 게 addOnCompleteListener
                        // 이미지를 업로드하는데 시간이 좀 걸린다.
                    storage.reference
                        .child("market/article/photo")
                        .child(fileName)
                        .downloadUrl
                        // 이게 핵심!!! 이걸 database 에 저장해서 가져온 다음 Glide load 메서드에 인자로 넣어주면 되겠다??
                        .addOnSuccessListener { uri ->
                            // DB 에 업로드 하는 함수
                            Log.d("market", "url 가져오기 성공")
                            successHandler(uri.toString())
                        }
                        .addOnFailureListener {
                            Log.d("market", "url 가져오기 실패")
                            errorHandler()
                        }

                } else {
                    Log.d("market", "storage 에 이미지가 업로드되지 않음")
                    errorHandler()
                }
            }

    }
    private fun uploadArticle(sellerId: String, title: String, price: String, imageUrl : String) {
        val model = ArticleModel(sellerId, title, System.currentTimeMillis(), "$price 원", imageUrl)
        // push 는 하나의 아이템을 만들겠다는 말
        articleDB.push().setValue(model) // 이러면 임의의 키값이 생긴 것 아래에 ArticleModel 객체가 들어가게된다.
        hideProgress()
        finish()
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            REQUEST_CODE ->
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startContentProvider()
                } else {
                    Toast.makeText(this, "권한을 거부하셨습니다.", Toast.LENGTH_SHORT).show()
                }
        }
    }
    private fun startContentProvider() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*" // 이미지타입의 모든 것을 가져와라
        startActivityForResult(intent, RESULT_CODE)
    }

    private fun showProgress() {
        binding.marketAddArticleProgressBar.isVisible = true

    }
    private fun hideProgress() {
        binding.marketAddArticleProgressBar.isVisible = false
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_OK) {
            return
        }
        when (requestCode) {
            RESULT_CODE -> {
                val uri = data?.data
                if (uri != null) {
                    binding.fragmentMarketAddArticleImageView.setImageURI(uri)
                    selectedUri = uri
                } else {
                    Toast.makeText(this, "사진을 가져오지 못했습니다. uri 가 null 입니다.", Toast.LENGTH_SHORT).show()
                }
            }
            else -> {
                Toast.makeText(this, "사진을 가져오지 못했습니다. REQUEST_CODE 확인바람.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showPermissionContextPopup() {
        AlertDialog.Builder(this)
            .setTitle("권한이 필요합니다.")
            .setMessage("사진을 가져오려면 권한이 필요합니다.")
            .setPositiveButton("동의") {_, _ ->
                requestPermissions(arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), REQUEST_CODE)
            }
            .create()
            .show()

    }
    companion object {
        const val REQUEST_CODE = 1001
        const val RESULT_CODE = 1002
    }
}
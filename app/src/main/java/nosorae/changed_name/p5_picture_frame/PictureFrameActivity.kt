package nosorae.changed_name.p5_picture_frame

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import nosorae.changed_name.R

/**
 * 가로화면 그리기
 * Android Permission - 로컬사진 가져오기
 * -> 옛날에는 매니페스트에만 명시하면 다운받기 전에 유저가 허락하는 방식이었다면 요즘은 해당기능을 사용할 때 앱내에서 물어본다고한다.
 * -> TODO https://developer.android.com/training/permissions/requesting?hl=ko 참고
 * View Animation 사용
 * -> .animate().애니메이션 내용
 * Lifecycle 알아보기
 * -> TODO https://developer.android.com/guide/components/activities/activity-lifecycle?hl=ko 참고
 * -> 이 앱에서는 timer 를 써서 백그라운드 상태에 들어 갔을 때는 멈춰주는 역할을 시키고 싶기 때문이다.
 * -> startTimer() 함수를 onCreate 함수에도 쓰고, onStart 함수에도 썼더니 onStop 에서 cancel 해도 안먹히는 문제가 있었다.
 * -> onStart 함수에 썼던 startTimer 함수를 onRestart 함수에 써주니 문제가 해결되었다.
 * Contene Provider 사용하는 여러가지 방법중 SAF (Storage Access Framework) 사용용
 * -> SAF 는 코드도 간결하고 사용자에게 익숙하기도 하다. 다른 앱에서도 똑같이 SAF 기능을 사용하면, 똑같은 화면이 뜨기 떄문이다.
 * app:layout_constraintDimensionRatio="W, 1:3" 가로가 3이면 세로는 1
 * app:layout_constraintDimensionRatio="H, 1:3" 세로가 3이면 가로는 1
 * -> 이 뷰의 가로세로 사이즈의 비율을 설정할 수 있다. 영향받는 방향으로는 0dp를 주어야한다.
 *
 * 초기화 함수는 위쪽에 있는 것이 좋다. 왜냐하면 onCreate 가 상단에 있어서 순서대로 함수를 확인할 수 있기 때문이다.
 * */


class PictureFrameActivity : AppCompatActivity() {
    private val addPhotoButton: Button by lazy {
        findViewById<Button>(R.id.picture_frame_button_add)
    }
    private val runPhotoFrameButton: Button by lazy {
        findViewById<Button>(R.id.picture_frame_button_run)
    }


    private val imageViewList: List<ImageView> by lazy {
        mutableListOf<ImageView>().apply {
            add(findViewById<ImageView>(R.id.photo_frame_image_view_1))
            add(findViewById<ImageView>(R.id.photo_frame_image_view_2))
            add(findViewById<ImageView>(R.id.photo_frame_image_view_3))
            add(findViewById<ImageView>(R.id.photo_frame_image_view_4))
            add(findViewById<ImageView>(R.id.photo_frame_image_view_5))
            add(findViewById<ImageView>(R.id.photo_frame_image_view_6))
        }
    }
    private val imageUriList: MutableList<Uri> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_picture_frame)
        //코드 추상화 onCreate 에 함수 안쓰고 코드 나열하면 찾기 힘드니까 함수로 나눠서 한눈에 알아볼 수 있게 만든다.
        initAddPhotoButton()
        initRunPhotoFrameButton()
    }

    private fun initAddPhotoButton() {
        addPhotoButton.setOnClickListener {
            when {
                ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED -> {
                    // TODO 권한이 잘 부여됐을 때 갤러리에서 사진 선택 하는 기능
                    navigatePhotos()
                }

                //Gets whether you should show UI with rationale before requesting a permission.
                // 권한 거절했다가 다시 버튼누르면 이게 등장한다.
                shouldShowRequestPermissionRationale(android.Manifest.permission.READ_EXTERNAL_STORAGE) -> {
                    // 교육용 팝업 환인 후 권한 팝업을 띄우는 기능
                    showPermissionContextPopup()
                }
                else -> {
                    // android.Manifest.permission.READ_EXTERNAL_STORAGE 이런 것들은 다 String 이고
                    // 따라서 requestPermissions()의 인자로는 String 배열이 들어간다.
                    requestPermissions(arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), 1000)
                }
            }
        }
    }

    private fun initRunPhotoFrameButton() {
        runPhotoFrameButton.setOnClickListener {
            val intent = Intent(this, PictureFrameResultActivity::class.java)
            // 이미지 리스트에 있는 uri 를 하나하나 putExtra 해주고 startActivity 해준다.
            imageUriList.forEachIndexed { index, uri ->
                intent.putExtra("photo$index", uri.toString())
            }
            intent.putExtra("photoListSize", imageUriList.size)
            startActivity(intent)
        }

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when(requestCode) {
            1000 -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    navigatePhotos()

                } else {
                    Toast.makeText(this, "권한을 거부하였습니다.", Toast.LENGTH_SHORT).show()
                }
            }
            else -> {

            }
        }

    }

    private fun navigatePhotos() {
        val intent = Intent(Intent.ACTION_GET_CONTENT) // SAF 기능, 안드로이드에 내장해있는 액티비티를 실행
        intent.type = "image/*" // 필터링 기능
        startActivityForResult(intent, 2000)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // 정상적으로 끝나지 않았다면 리턴
        if(resultCode != Activity.RESULT_OK) {
            return
        }
        // 정상적으로 실행됐다면 사진을 가져왔는지 확인
        when(requestCode) {
            2000 -> {
                // ?. 은 data 가 null 이면은 뒤에 것을 실행하지 않는다는 말
                val selectedImageUri: Uri? = data?.data

                if (selectedImageUri != null) {
                    if(imageUriList.size == 6) {
                        Toast.makeText(this, "이미 사진이 꽉 찼습니다.", Toast.LENGTH_SHORT).show()
                        return
                    }
                    // 위 if 문을 통과하면 non-null 로 오토캐스팅 된다고 생각하면된다.
                    imageUriList.add(selectedImageUri)
                    imageViewList[imageUriList.size - 1].setImageURI(selectedImageUri)
                } else {
                    Toast.makeText(this, "사진을 가져오지 못했습니다.", Toast.LENGTH_SHORT).show()
                }

            }
            else -> {
                Toast.makeText(this, "사진을 가져오지 못했습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun showPermissionContextPopup() {
        AlertDialog.Builder(this)
            .setTitle("갤러리 접근 권한이 필요합니다.")
            .setMessage("사진을 가져와야 보여줄 수 있습니다.")
            .setPositiveButton("동의하기", { _, _ ->
                requestPermissions(arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), 1000)
            })
            .setNegativeButton("취소하기", { _, _ -> })
            .create()
            .show()
    }

}
package nosorae.changed_name.p22_unsplash

import android.app.WallpaperManager
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import nosorae.changed_name.databinding.ActivityUnsplashBinding
import nosorae.changed_name.p22_unsplash.adapter.PhotoRecyclerAdapter
import nosorae.changed_name.p22_unsplash.data.Repository
import nosorae.changed_name.p22_unsplash.data.models.photo.PhotoResponse
import java.io.IOException

/**
 * 저작권 무료 이미지 제공 서비스 unsplash 사용 ( 상업적 목적 x, 노션 외부 백그라운드 지정같은 것은 가능 즉, 이거빼면 시체인 앱은 안된다는 말 )
 *      Unsplash api 사용해서 사진을 랜덤하게 받아오기 ( Retrofit2, Coroutine, Glide)
 *      https://unsplash.com/documentation#schema 문서 참고
 *      이런 api 쓸 때는 가이드라인, 약관을 잘 읽어보고 사용해야 블락 당하지 않는다.
 *
 *
 * 사진 다운로드 ( shared storage , 앱이 지워지더라도 남게됨 )
 *      https://developer.android.com/training/data-storage/shared?hl=ko
 *      sdk 28 까지는 EXTERNAL_STORAGE 권한을 받아야한다. 따라서 30은 권한요청 없이 앱내의 사진을 불러올 수 있다.
 *      핵심은 contentResolver.insert(collectionUri, imageInfo)
 *
 *
 * 사진을 배경화면으로 설정 (WallpaperManager)
 *
 *
 * Loading Shimmer 기능 (ShimmerLayout made by facebook)
 *      TODO https://github.com/facebook/shimmer-android
 *
 * Glide 로 고화질 이미지 불러올 때 오래 걸릴 수 있으니 먼저 저화질의 썸네일을 불러와서 보여주기기 ( Thumnail request )
 *      TODO https://bumptech.github.io/glide/doc/getting-started.html 맨날 쓰는 글라이드 공식문서 읽어보자 그리고 피카소와 비교도 해보자자 *
 *      TODO https://bumptech.github.io/glide/doc/options.html
 *      Placeholders are Drawables that are shown while a request is in progress.
 *      TODO https://bumptech.github.io/glide/doc/placeholders.html
 *      TODO https://bumptech.github.io/glide/doc/transitions.html#performance-tips
 *
 * 이때까지 EditText 앞에 돋보기 이미지 붙이는 거 이미지 뷰 따로 만들어서 했는데 속성이 따로 있었다.
 *      android:drawableStart="@drawable/ic_search"
 *      android:drawableTint="@color/black"
 *
 * EditText 에 검색 버튼을 따로 두지 않고 키보드에 두어서 검색타이밍을 설정
 *      android:imeOptions="actionSearch"
 *      setOnEditorActionListener
 *      actionId == EditorInfo.IME_ACTION_SEARCH
 *
 * RecyclerView 에서 패딩주는데 아이템이 짤리게하지 않으려면면 *
 *      android:paddingVertical="6dp"
android:clipToPadding="false"

 * CardView 는 테두리 그림자 때문에 여백을 두고 구현하는 것이 좋다.
 *
 * TextView 에서 textColor 가 배경색과 비슷할 때도 잘 보이게 하기 위해
 *      android:shadowColor="#60000000"
 *      android:shadowDx="1"
 *      android:shadowDy="1"
 *      android:shadowRadius="5"
 *
 * 정보의 중요도를 alpha 값으로 표현할 수도 있다.
 *
 * 체인을 활용해서 프로필 사진 옆 아래의 description 이 사라져도 자동으로 Author 는 프로필 중앙에 위치할 수 있게 만듦
 *
 * 이미지가 로드되기 전에 미리 height 를 설정하기 위해서 ( 유저가 미리 로딩 되는 것들에 대한 힌트를 얻을 수 있다. -> 더 좋은 UI/UX )
 *      .layoutParams.height 사용
 *      그리고 그 전에 값을 구하기 위해 url 과 함께 전달된 width 와 height 정보로 비율을 구하고
 *      이미 알고있는 뷰의 width 로 뷰의 height 도 구했다.
 *      binding.root.resources.displayMetrics.widthPixels 로 뷰의 width 를 구했다.
 *
 * 키보드 없애는 법
 *      val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
 *      inputMethodManager?.hideSoftInputFromWindow(view.windowToken)
 *
 * Material Design 의 SnackBar 활용
 *
 *
 * https://developer.android.com/reference/android/Manifest.permission#READ_EXTERNAL_STORAGE
 *      이곳에서 protection level 이 normal 이라는 게 런타임 권한요청없이 매니페스트에 명시만으로 기능을 사용할 수 있다는 뜻이다.
 *

 *
 * */
class UnsplashActivity : AppCompatActivity() {

    private val scope = MainScope()
    lateinit var photoRecyclerAdapter: PhotoRecyclerAdapter

    lateinit var binding: ActivityUnsplashBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUnsplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initViews()
        bindViews()
        fetchRandomPhotos()


    }


    private fun initViews() {
        photoRecyclerAdapter = PhotoRecyclerAdapter()
        binding.recyclerView.apply {
            adapter = photoRecyclerAdapter
            layoutManager = LinearLayoutManager(this@UnsplashActivity, RecyclerView.VERTICAL, false)
        }

    }

    private fun bindViews() {
        binding.searchEditText.setOnEditorActionListener { editText, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                currentFocus?.let { view ->
                    val inputMethodManager =
                        getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
                    inputMethodManager?.hideSoftInputFromWindow(view.windowToken, 0)
                    view.clearFocus() // editText 가 현재 포커싱 되어있을 것이고 clearFocus 하면 커서가 사라질 것이다.
                }
                photoRecyclerAdapter.submitList(listOf())
                fetchRandomPhotos(editText.text.toString())

            }
            true // true 면 다 소비했다는 뜻이다.
        }

        binding.swipeRefreshLayout.setOnRefreshListener {
            photoRecyclerAdapter.submitList(listOf())
            fetchRandomPhotos(binding.searchEditText.text.toString()) // 검색을 한 상태에서 새로고침할 때 그대로 재검색
        }

        photoRecyclerAdapter.setOnItemClickListener { photo ->
            showDownloadPhotoConfirmationDialog(photo)
        }
    }

    private fun requestWriteExternalStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) return
        ActivityCompat.requestPermissions(
            this,
            arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),
            REQUEST_WRITE_EXTERNAL_PERMISSION_CODE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        val writeExternalStoragePermissionGranted =
            requestCode == REQUEST_WRITE_EXTERNAL_PERMISSION_CODE &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED
        if (writeExternalStoragePermissionGranted.not()) {
            Toast.makeText(this, "권한이 있어야 사진을 저장할 수 있습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun fetchRandomPhotos(query: String? = null) = scope.launch {
        try {
            binding.shimmerLayout.visibility = View.VISIBLE
            binding.errorDescriptionText.visibility = View.GONE
            Repository.getRandomPhotos(query)?.let { photos ->

                photoRecyclerAdapter.submitList(photos)
            }
            binding.recyclerView.visibility = View.VISIBLE
        } catch (e: Exception) {
            Log.e("unsplash", e.toString())
            binding.recyclerView.visibility = View.INVISIBLE
            binding.errorDescriptionText.visibility = View.VISIBLE
        } finally {
            binding.swipeRefreshLayout.isRefreshing = false
            binding.shimmerLayout.visibility = View.GONE
        }


    }

    private fun showDownloadPhotoConfirmationDialog(photo: PhotoResponse) {
        AlertDialog.Builder(this)
            .setMessage("사진을 저장하시겠습니까?")
            .setPositiveButton("저장") { dialog, _ ->

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    downloadPhoto(photo.urls?.full)
                } else {
                    val isPermitted =
                        checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                    if (isPermitted) {
                        downloadPhoto(photo.urls?.full)
                    } else {
                        requestWriteExternalStoragePermission()
                    }
                }

                dialog.dismiss()
            }
            .setNegativeButton("취소") { dialog, _ ->


                dialog.dismiss()
            }
            .create()
            .show()

    }

    private fun downloadPhoto(photoUrl: String?) {
        photoUrl ?: return


        Glide.with(this)
            .asBitmap()
            .load(photoUrl)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .into(
                object : CustomTarget<Bitmap>(SIZE_ORIGINAL, SIZE_ORIGINAL) {
                    override fun onResourceReady(
                        resource: Bitmap,
                        transition: Transition<in Bitmap>?
                    ) {
                        saveBitmapToMediaStore(resource)


                        val wallpaperManager = WallpaperManager.getInstance(this@UnsplashActivity)
                        val snackbar =
                            Snackbar.make(
                                binding.root,
                                "다운로드 완료!",
                                Snackbar.LENGTH_SHORT
                            )
                        if (wallpaperManager.isWallpaperSupported && (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && wallpaperManager.isSetWallpaperAllowed)) {
                            snackbar.setAction("배경화면으로 저장") {
                                // 액션을 받았을 때의 콜백 리스너 람다
                                try {
                                    wallpaperManager.setBitmap(resource)
                                } catch (e: IOException) {
                                    e.printStackTrace()
                                    Snackbar.make(binding.root, "배경화면 저장 실패", Snackbar.LENGTH_SHORT).show()
                                }

                            }
                            snackbar.duration = Snackbar.LENGTH_INDEFINITE // 애초에 세번째 인자가 duration 인데 그것을 수정한 것?
                        }
                        snackbar.show()


                    }

                    override fun onLoadCleared(placeholder: Drawable?) {
                        // 불러오는 게 해제 되었을 때
                    }

                    override fun onLoadFailed(errorDrawable: Drawable?) {
                        super.onLoadFailed(errorDrawable)
                        Snackbar.make(binding.root, "다운로드 실패", Snackbar.LENGTH_SHORT).show()
                    }

                    override fun onLoadStarted(placeholder: Drawable?) {
                        super.onLoadStarted(placeholder)
                        Snackbar.make(binding.root, "다운로드 중...", Snackbar.LENGTH_INDEFINITE).show()
                    }
                }
            )

    }

    private fun saveBitmapToMediaStore(bitmap: Bitmap) {
        val fileName = "${System.currentTimeMillis()}.jpg"
        val resolver = applicationContext.contentResolver
        val imageCollectionUri =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
            } else {
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            } // 어디에 저장할지를 나타낸다.
        val imageDetails = ContentValues().apply {

            put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg")

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(
                    MediaStore.Images.Media.IS_PENDING,
                    1
                ) // 안드로이드 10 이상부터, 저장 끝날 때까지 다른 앱에서 파일에 접근할 수 없다.
            }

        } // 이미지 정보

        val imageUri = resolver.insert(imageCollectionUri, imageDetails) // Uri 반환
        imageUri ?: return

        resolver.openOutputStream(imageUri)?.use { outputStream ->
            // 열었다가 다시 닫아줘야하는 상황에서 use 사용 자동으로 닫아주기까지 해줌
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            imageDetails.clear()
            imageDetails.put(MediaStore.Images.Media.IS_PENDING, 0)
            resolver.update(imageUri, imageDetails, null, null)
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
    }

    companion object {
        const val REQUEST_WRITE_EXTERNAL_PERMISSION_CODE = 100
    }
}
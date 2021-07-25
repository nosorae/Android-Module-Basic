package nosorae.module_basic.p13_tinder

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import nosorae.module_basic.R
import nosorae.module_basic.databinding.ActivityTinderBinding

/**
 * - Firebase Authentication -> Email 로그인
 *  예전에는 네트워크 처리를 해줘야했지만 파이어베이스가 로그인 관련 데이터 처리를 알아서 다 해준다.
 *  시작하기 누르고 google-service.jason 파일을 다시 다운 받아야한다. ( 프로젝트 개요 - 프로젝트 - 설정아이콘 )
 *  왜냐하면 Realtime Database 에 대한 권한이 포함되어있지 않기 때문이다.
 *   auth = Firebase.auth
 *   auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this) { task -> }  로그인 패턴
 *   auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this) { task -> } 회원가입 패턴
 *  TODO https://developers.facebook.com/apps/584373109636939/fb-login/quickstart/ 참고
 *  TODO https://developers.facebook.com/docs/facebook-login/android/?locale=ko_KR 참고
 *
 *  - Firebase Authentication -> Facebook 로그인
 *   firebase 의 Authentication 에서 facebook 를 추가할 뿐만 아니라,
 *   facebook 계정 연동은 일단 facebook developers 에 앱 등록하고 설정가서 앱 id 와 password 받아오고 facebook 로그인 설정에 유효한 OAuth 리디렉션 URI 를 추가하면 email 할 때와 같다.
 *  모바일 앱 딥링킹은 사용자가 웹 페이지 또는 다른 앱에서 URL을 클릭하면 앱을 실행하고 특정 페이지를 여는 기술입니다.
 *  페이스북 로그인은 버튼 누르면 페이스북 앱이나 웹페이지 열리고 로그인성공하면 다시 이 앱으로 넘어와서 콜백 메서드가 호출된다. onActivityResult 로 android sdk 에 전달
 *  CallbackManager.Factory.create() 로 초기화하고
 *  onActivityResult 에서 callbackManager.onActivityResult(requestCode, resultCode, data) 값 세개 그대로 넣어준다.
 *  .registerCallback(callbackManager, object: FacebookCallback<LoginResult> { onSuccess onCancel onError )
 *
 *
 *
 * - Firebase Realtime Database
 *  시작하기 누르고 google-service.jason 파일을 다시 다운 받아야한다. ( 프로젝트 개요 - 프로젝트 - 설정아이콘 )
 *  왜냐하면 Realtime Database 에 대한 권한이 포함되어있지 않기 때문이다.
 *  project-info 에 "firebase_url": "https://android-module-fast-campus-default-rtdb.firebaseio.com" 이 추가됨을 확인
 *  json 형식으로 저장된다. child 의 연속으로 데이터 저장 조회를 한다. 없으면 자동으로 실시간으로 만든다. Firebase.database.reference 는 최상위의 레퍼런스이다.
 *  updateChildren(mutableMapOf()~) 또는 setValue 로 저장
 *  addListenerForSingleValueEvent 즉시성으로 한번만 불러온다??, addChildEventListener 전체 차일드에 대한 체인지를 다 보게됨
 *
 * - CardStackView -> 스와이프 애니메이션
 *  어떻게 찾고싶은 좋은 라이브러리를 쉽게 찾을 수 있을까? Most Stars, not Archived, recent update , detail description
 *  버그를 발견하면 Issue 로 제작자에게 직접 질문할 수도 있다.
 *  샘플 코드를 제공하면 ( Readme 아니어도 직접 코드로 ) 들어가서 확인
 *
 *
 * - 어떤 블록 내에서의 this 는 FacebookCallback<LoginResult> 이거니까 어노테이션으로 어느 클래스의 this 인지 명시해줄 수 있다.
 *  this@TinderLoginActivity
 *
 * - 함수를 override 하는데 인자가 ? 타입 일때 이 함수에 도달했을 때 null 이 아닌 게 확실하다면 인자에서 ? 지워도 무방한가보다?
 *
 * - CardView 사용
 *
 * - DB 에서 사용되는 Key 값 같은 여러번 쓰이면서 상수로 사용되는 것은 따로 상수 관리 클래스를 만들어 실수를 미연에 방지한다.
 *
 * - xml 이든 클래스든 유저에게 보이는 모든 string 은 resource 로 관리해주는 것이 좋다.
 *
 *
 */
class TinderActivity : AppCompatActivity() {

    //private lateinit var binding: ActivityTinderBinding
    private val auth =  FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //binding = ActivityTinderBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_tinder)


    }

    override fun onStart() {
        super.onStart()
        // 로그인 되지 않은 상태라면 로그인 액티비티로 넘어가고 로그인 되어있다면 라이크 액티비티로 넘어간다.
        if (auth.currentUser == null) {
            startActivity(Intent(this, TinderLoginActivity::class.java))
        } else {
            startActivity(Intent(this, TinderLikeActivity::class.java))
            finish()
        }
    }



}
package nosorae.module_basic.p13_tinder

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton
import com.google.android.gms.common.internal.Objects
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import nosorae.module_basic.databinding.ActivityTinderLoginBinding

class TinderLoginActivity: AppCompatActivity() {

    private lateinit var binding: ActivityTinderLoginBinding

    private lateinit var auth: FirebaseAuth
    private lateinit var callbackManager: CallbackManager

    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button
    private lateinit var signButton: Button
    private lateinit var facebookLoginButton: LoginButton


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTinderLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // auth = FirebaseAuth.getInstance() 와 같은 의미
        auth = Firebase.auth // FirebaseAuth 객체를 코틀린스럽게 가져오기
        callbackManager = CallbackManager.Factory.create()

        initViews()
        initLoginButton()
        initSignButton()
        initEmailAndPasswordEditText()
        initFacebookLoginButton()

    }

    private fun initViews() {
        emailEditText = binding.tinderLoginEditTextEmail
        passwordEditText = binding.tinderLoginEditTextPassword
        loginButton = binding.tinderLoginButtonLogin
        signButton = binding.tinderLoginButtonSign
        facebookLoginButton = binding.tinderLoginButtonFacebook
    }



    private fun initLoginButton() {
        loginButton.setOnClickListener {
            val email = getInputEmail()
            val password = getInputPassword()


            // signInWithEmailAndPassword 의 반환 값은 com.google.android.gms.tasks.Task<com.google.firebase.auth.AuthResult>
            // 이메일과 패스워드를 firebase auth 에 넘겨서 firebase 서버에서 이메일 패스워드 맞는지 판단해서 completeListener 로 전달 이외의 것은 firebase 가 다 구축해줌
            // email 이나 password 가 비어있으면 에러를 발생시킴에 주의
            auth.signInWithEmailAndPassword(email, password)
                // 테스크가 완료됐을 때의 리스너
                .addOnCompleteListener(this) { task ->
                    // 메인액티비티에서 로그인 안됐을 떄 이 화면을 띄운 것이기 때문에 로그인에 성공했다면 로그인화면을 끝내면된다.
                    if (task.isSuccessful) {
                        handleSuccessLogin()
                    } else {
                        Toast.makeText(this, "로그인에 실패했습니다. 이메일 또는 비밀번호를 확인해주세요.", Toast.LENGTH_SHORT).show()
                    }
                }




        }
    }
    private fun initSignButton() {
        signButton.setOnClickListener {
            val email = getInputEmail()
            val password = getInputPassword()

            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "회원가입에 성공했습니다. 로그인 버튼을 눌러 로그인해주세요", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "이미 가입한 이메일이거나, 회원가입에 실패했습니다.", Toast.LENGTH_SHORT).show()
                    }
                }

        }
    }

    // 키보드 누를 때마다 리스너를 달아서 이메일, 패스워드 둘 중 하나라도 빈 문자열이 들어있다면 회원가입 및 로그인 버튼을 비활성화
    private fun initEmailAndPasswordEditText() {
        emailEditText.addTextChangedListener {
            val enable = emailEditText.text.isNotEmpty() && passwordEditText.text.isNotEmpty()
            loginButton.isEnabled = enable
            signButton.isEnabled = enable
        }

        passwordEditText.addTextChangedListener {
            val enable = emailEditText.text.isNotEmpty() && passwordEditText.text.isNotEmpty()
            loginButton.isEnabled = enable
            signButton.isEnabled = enable
        }



    }

    private fun getInputEmail(): String = emailEditText.text.toString()
    private fun getInputPassword(): String = passwordEditText.text.toString()

    private fun initFacebookLoginButton() {
        // setPermissions 함수는 로그인 버튼 눌렀을 때 유저에게 받아올 정보에 대한 permission 이다. 생일 등 다른 정보는 문서 참고해서 추가
        facebookLoginButton.setPermissions("email", "public_profile")
        facebookLoginButton.registerCallback(callbackManager, object: FacebookCallback<LoginResult> {

            override fun onSuccess(result: LoginResult) {
                // 완료되면 firebase 에 access token 을 넘겨준다.
                val credential = FacebookAuthProvider.getCredential(result.accessToken.token)
                auth.signInWithCredential(credential)
                    .addOnCompleteListener(this@TinderLoginActivity) { task ->
                        // 페이스북으로 처음 이 앱에 가입해도 자동으로 가입시키고 로그인을 시켜준다. 회원가입이 되어있어도 당연히 로그인 가능이다.
                        if (task.isSuccessful) {
                            Toast.makeText(this@TinderLoginActivity , "페이스북 로그인에 성공했습니다.", Toast.LENGTH_SHORT).show()
                            handleSuccessLogin()
                        } else {
                            Toast.makeText(this@TinderLoginActivity , "페이스북 로그인에 실패했습니다.", Toast.LENGTH_SHORT).show()
                        }
                    }
            }

            override fun onCancel() {
                // 로그인을 하다가 취소되었을 경우
            }

            override fun onError(error: FacebookException?) {
                // 이 블록 내에서의 this는 FacebookCallback<LoginResult> 이거니까 어노테이션으로 어느 클래스의 this 인지 명시해줄 수 있다.
                Toast.makeText(this@TinderLoginActivity , "페이스북 로그인에 실패했습니다. 에러발생", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        callbackManager.onActivityResult(requestCode, resultCode, data)
    }

    // 로그인에 성공했을 때, firebse realtime database 에 유저아이디를 추가해주는 기능
    private fun handleSuccessLogin() {
        //이 예외처리는 왜 하는거지?? 이미 성공했다며??
        if (auth.currentUser == null) {
            Toast.makeText(this, "로그인에 실패했습니다.", Toast.LENGTH_SHORT).show()
            return
        }
        val userId = auth.currentUser?.uid.orEmpty() // 사실상 null 처리 해줘서 null 일 일은 없다.
        val currentUserDB = Firebase.database.reference.child("Users").child(userId) // 없으면 만든다?
        val user = mutableMapOf<String, Any>()
        user["userId"] = userId
        currentUserDB.updateChildren(user)

        finish()
    }
}
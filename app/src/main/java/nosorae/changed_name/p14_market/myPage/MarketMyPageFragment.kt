package nosorae.changed_name.p14_market.myPage

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import nosorae.changed_name.R
import nosorae.changed_name.databinding.FragmentMarketMyPageBinding

class MarketMyPageFragment: Fragment(R.layout.fragment_market_my_page) {

    // 강의에서는 private var binding: FragmentMarketMyPageBinding? = null 로 하고
    // 리스너 다는 것은 fragmentMyPageBinding 로 하고 그 안에서 뷰에 접근하는 방식은 binding?. 였다. 왜 그런지는 모르겠다. 나는 그냥 binding 으로 다 처리했다.
    private lateinit var binding: FragmentMarketMyPageBinding
    private val auth: FirebaseAuth by lazy {
        Firebase.auth
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val fragmentMyPageBinding = FragmentMarketMyPageBinding.bind(view)
        binding = fragmentMyPageBinding
        initEditTexts()
        initLogInOutButton()
        initSignUpButton()

    }

    override fun onStart() {
        super.onStart()
        // 여기서 로그인이 풀려있는지 안풀려있는지 또 확인한다.
        if (auth.currentUser == null) {
            makeLogoutState()
        } else {
            binding.marketMyPageEditTextEmail.setText(auth.currentUser?.email.orEmpty())
            binding.marketMyPageButtonLoginLogout.text = getString(R.string.market_logout)
            binding.marketMyPageButtonLoginLogout.isEnabled = true
            binding.marketMyPageEditTextEmail.isEnabled = false
            binding.marketMyPageEditTextPassword.isEnabled = false
            binding.marketMyPageButtonSignUp.isEnabled = false
        }
    }

    private fun initEditTexts() {
        binding.marketMyPageEditTextEmail.addTextChangedListener {
            setEnableButtons()

        }
        binding.marketMyPageEditTextPassword.addTextChangedListener {
            setEnableButtons()
        }
    }
    private fun setEnableButtons() {
        val enable = binding.marketMyPageEditTextEmail.text.isNotEmpty() && binding.marketMyPageEditTextPassword.text.isNotEmpty()
        binding.marketMyPageButtonSignUp.isEnabled = enable
        binding.marketMyPageButtonLoginLogout.isEnabled = enable
    }

    private fun initLogInOutButton() {
        binding.marketMyPageButtonLoginLogout.setOnClickListener {
            val email = binding.marketMyPageEditTextEmail.text.toString()
            val password = binding.marketMyPageEditTextPassword.text.toString()

            if (auth.currentUser == null) {
                // 로그인, 참고로 email 이나 password 둘 중 하나라도 빈 값이면 로그인 버튼 자체가 눌리지 않으므로 그와 관련된 예외처리는 해줄 필요가 없다.
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(requireActivity()) { task ->
                        if (task.isSuccessful) {
                            successLogin()

                        } else {
                            Toast.makeText(context, "로그인에 실패했습니다. 이메일 비밀번호를 확인해주십시오.", Toast.LENGTH_SHORT).show()
                        }
                    }
            } else {
                auth.signOut()
                makeLogoutState()
            }
        }
    }
    private fun makeLogoutState() {
        binding.marketMyPageEditTextEmail.text.clear()
        binding.marketMyPageEditTextPassword.text.clear()
        binding.marketMyPageEditTextEmail.isEnabled = true
        binding.marketMyPageEditTextPassword.isEnabled = true
        binding.marketMyPageButtonLoginLogout.text = getString(R.string.market_login)
        binding.marketMyPageButtonLoginLogout.isEnabled = false
        binding.marketMyPageButtonSignUp.isEnabled = false
    }

    private fun successLogin() {
        // 여기로 들어와도 로그인에 실패했을 수 있다?? 예외처리!
        if (auth.currentUser == null) {
            Toast.makeText(context, "로그인 관련 오류. 다시 시도해주세요.", Toast.LENGTH_SHORT).show()
            return
        }

        binding.marketMyPageEditTextEmail.isEnabled = false
        binding.marketMyPageEditTextPassword.isEnabled = false
        binding.marketMyPageButtonSignUp.isEnabled = false

        binding.marketMyPageButtonLoginLogout.text = getString(R.string.market_logout)
    }
    private fun initSignUpButton() {
        binding.marketMyPageButtonSignUp.setOnClickListener {
            val email = binding.marketMyPageEditTextEmail.text.toString()
            val password = binding.marketMyPageEditTextPassword.text.toString()

            auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(context, "회원가입에 성공했습니다.", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "회원가입 실패, 이미 가입한 이메일인지 확인해주십시오,", Toast.LENGTH_SHORT).show()
                }
            }
        }

    }
}
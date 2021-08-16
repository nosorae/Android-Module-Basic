package nosorae.module_basic.p23_todo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import nosorae.module_basic.databinding.ActivityTodoBinding

/**
 * MVP, MVVM, 구글 아키텍쳐에 대해 배움
 *      구글 아키텍쳐 샘플레포지토리
 *      https://developer.android.com/jetpack/guide?hl=ko#cache-data 여기서 구글에서 권장하는 아키텍쳐에대한 대략적인 설명이 나온다.
 *
 * DI, KOIN ( 테스트 코드를 원활하게 작성하고, 의존성이 없게 구현하기 위해서 ) 배움
 *
 * TDD 방식으로 코드작성
 *
 *
 *
 */
class TodoActivity: AppCompatActivity() {
    private lateinit var binding : ActivityTodoBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTodoBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}
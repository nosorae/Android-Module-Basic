package nosorae.changed_name.p23_todo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import nosorae.changed_name.databinding.ActivityTodoBinding

class TodoActivity: AppCompatActivity() {
    /**
     * 클릭아키텍쳐 적용한 MVP 패턴으로 TODO 앱 만들기
     *
     * MVP, MVVM, 구글 권장 아키텍쳐에 대해 배움
     *      구글 아키텍쳐 샘플레포지토리
     *      https://developer.android.com/jetpack/guide?hl=ko#cache-data 여기서 구글에서 권장하는 아키텍쳐에대한 대략적인 설명이 나온다.
     * Model : data class?
     * View : xml?
     * Controller : Activity or Fragment class
     * Presenter : View 를 제어, 모델과 뷰를 함께 관리하지만 인터페이스를 통해서 뷰에 넣어줄 데이터를 단순하게 추상화해서 구현했다는 특징
     * View(ViewController) : ViewController 에 의해 View 가 제어됨
     *
     * Model 과 View 의 관계를 어떻게 처리할까?
     * MVC : Model + View + ViewController
     *      -> Activity or Fragment 가 View 와 Model 을 함께 관리
     *      이 방식은 비즈니스로직과 모델들 뷰가 함께 혼용되니 코드가 난잡해지고, 버그만들기 쉬운 코드가 발생하더라
     * MVP : Model + View(ViewController) + Presenter
     *      -> View(ViewController) 에서 비즈니스로직을 Presenter 통해서 호출,
     *         Presenter 는  Model 에 직접 접근해 데이터를 가공해서 뷰에 콜백으로 던져주는 방식
     * MVVM : Model + View(ViewController) + ViewModel
     *      -> View 와 ViewModel 이 분리, View 는 ViewModel 을 알지만 ViewModel 은 View 를 모르는 형태, 단방향 형태의 제어
     *      -> ViewModel 에서는 Model 에 대한 데이터를 받아 제어하고,그 Model 의 데이터가 변형됐을 때 변형된 데이터를 브로드캐스팅한다.
     *      전파됐을 때 ViewController  에서 옵저버 패턴 통해서 ViewModel 에서 data holder 를 구독하거나,
     *      콜백을 통해서 데이터 변형시 View 변형될 수 있게 구현
     *      여기서 ViewController 에서 구독에대한 보일러플레이트코드가 생기는 것을 막기 위해,
     *      DataBinding 을 도입해 View 에서 구독처리? 하는 방법도 있다. (취향차이~)
     *
     *      다른 설명
     *      ViewController 에서 ViewModel 에 비즈니스로직을 직접적으로 호출하고, 데이터 옵저빙
     *      매개체인 ViewModel 은 라이프사이클에 맞게 데이터 홀더클래스를 기반으로 구성해서 모델에 접근해서 데이터를 가공해서 가져오고난 이후에
     *      데이터 변형 됐을시에 데이터 홀더 클래스에 담아서? 브로드캐스팅해서 구독한 Active View 에게 알려준다.
     *      여기서 Active View 는 DataBinding 이 없으면 Activity or Fragment 이고
     *      DataBinding 을 사용한다면 xml 이 된다.
     *
     *      ViewModel 과 View 는 1:n 관계로 하나의 ViewModel 에 어떤 메소드를 구현해 놓으면 여러 뷰에서 호출해서 재사용할 수 있다.
     * 최근에 유행하는 단방향 플로우의 아키텍쳐
     * MVI : Model + View + Intent
     * MvRx (메버릭스) :
     *
     * google architecture sample 에 있는
     *
     *
     * 종속성 주입 DI - (Hilt, Dagger ...), Service Locator - (KOIN)
     *      ( 테스트 코드를 원활하게 작성하고, 의존성이 없게 구현하기 위해서 ) 배움
     *      Dependency Injection 의 약자로
     *      컴포넌트간의 의존 관계를 소스코드 내부가 아닌, 외부 설정 파일등을 통해 정의되게하는 디자인 패턴 중 하다ㅏ.
     *      객체를 직접 생성하지 않고 외부에서 주입한 객체를 사용하는 방식
     *      즉 인스턴스 간 디커플링을 만들어주게된다. -> 유닛테스트 용이성 증대
     *      여기서는 Service Locator 를 먼저 배움
     * Service Locator - (ex. KOIN : 경량화된 DI, 내부 동작은 Service Locator)
     *      중앙 등록자 'Service Locator' 를 통해 요청이 들어왔을 때 특정 인스턴스 라우팅해서 반환
     *      DI 대비 장점
     *      어플리케이션 크기가 DI 에 비해서 늘어나지 않는다는 장점
     *      컴파일시 빌드 속도에 영향 x. (기존 DI 는 어노테이션  프로세스를 이용해서 직접 인스턴스를 생성하지 않고 어노테이션 프로세스를 통한 객체생성 및 주입을 해서 빌드속도가 크게 느려진다.)
     *      메서드 수가 적다.
     *      TODO https://insert-koin.io/docs/quickstart/android
     *      module  {} : 키워드로 주입받고자 하는 객체의 집합 정의, 이 안에 single, factory, viewModle, get
     *      single {} : 앱이 실행되는 동안 계속 유지되는 싱글톤 객체 생성
     *      factory {} : 요청할 때마다 매번 새로운 객체 생성
     *      viewModel {} : factory 의 viewModel 버전?
     *      get() : 모듈 내에서 쓸 수 있는 스코프내에서 get 을 호출해서 컴포넌트 내에서 알맞은 의존성 주의
     *
     *
     *      장
     *      - Kotlin 기반으로 한 dsl 라이브러리기 때문에, 코틀린 개발 환경에 도입하기 쉬움
     *      - 별도의 어노테이션 없기에 컴파일 시간 단축
     *      - ViewModel 주입이 쉬워진다.
     *      단
     *      - 런타임시 주입이 필요한 컴포넌트가 생성이 되어있지 않은 파라미터가 있는 경우 크래시 발생
     *      - 런타임 타임에 주입대상을 선정하는 di에 비해 런타임에 서비스 로케이팅을 통해 인스턴스를 동적으로 주입해주기 때문에 런타임 퍼포먼스가 떨어진다.
     *
     *
     *
     * DI vs ServiceLocator
     *      종속성
     *      DI : 일부 핵심 클래스에 종속성을 주입, 안드로이드의 경우 앱에서 DI 한다는 트리거를 만들고 그것을 기반으로 특정 어노테이션이 붙은 모듈 컴포넌트 생성해서 종속해주게된다.
     *      SL : 모든 클래스가 서비스 로케이터에 종속이 될 수 있기 때문에, 어떤 특정 클래스를 요청할시에 그 해당 클래스 타입에 맞춰서 인스턴스를 반환 받을 수 있다.
     *      호출방법
     *      DI : 앱 시작할 때 처음 한번만 호출 (명시적 호출 x)
     *      SL : 인젝터를 직접 호출 (명시적 호출 o)
     *      의존관계
     *      DI : 의존 관계 파악이 쉬움
     *      SL : 모든 클래스가 서비스 로케이터에 종속되기 때문에 의존관계 파악이 어려운 경우가 있다. (그래서 모듈 잘 나눠서 어떤 곳에서 어떤 곳 참조하는지 정확한 판단이 필요할 것)
     *
     *
     *
     *
     *
     * TDD 방식으로 코드작성
     *
     * 클린아키텍쳐
     *      TODO https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html 참고
     *      SOLID 원칙을 기반으로하는 설계 철학
     *
     *
     *
     */
    private lateinit var binding : ActivityTodoBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTodoBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}
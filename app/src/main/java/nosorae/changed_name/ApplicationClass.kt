package nosorae.changed_name

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.GlobalContext.startKoin

class ApplicationClass: Application() {
    override fun onCreate() {
        super.onCreate()
        // 앱 시작시에 모듈에 대한 트리거를 발생 시킨다.
        startKoin{
            androidLogger()
            androidContext(this@ApplicationClass)
            modules()
        }
    }
}
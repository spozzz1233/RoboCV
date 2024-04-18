package com.example.robocv
import android.app.Application
import com.example.robocv.di.mainModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin

class App : Application() {

    override fun onCreate(){
        super.onCreate()

        startKoin {
            androidContext(this@App)
            modules(
                mainModule
            )
        }
    }
}
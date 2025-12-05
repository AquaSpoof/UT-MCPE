package com.aquaspoof.unified.toolkit.mcpe

import android.app.Application
// мб пофикшу этот класс ибо GCode
class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        LocaleHelper.applySavedLocale(this)
    }
}
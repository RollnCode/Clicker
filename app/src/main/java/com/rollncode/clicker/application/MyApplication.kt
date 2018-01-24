package com.rollncode.clicker.application

import android.app.Application

/**
 *
 * @author Osadchiy Artem osadchiyzp93@gmail.com
 * @since 2018.01.12
 */
class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        //ClicksDbManager.initDataBase(this)
    }
}
package com.example.sportsbet

import android.app.Application
import com.google.firebase.analytics.FirebaseAnalytics

class MyApplication : Application(){

    private lateinit var firebaseAnalytics: FirebaseAnalytics

    private lateinit var app: MyApplication

    override fun onCreate() {
        super.onCreate()

        app = this
    }

    fun getFirebase(): FirebaseAnalytics {
        firebaseAnalytics = FirebaseAnalytics.getInstance(this)
        return firebaseAnalytics
    }

    fun getApp(): MyApplication {

        return app
    }

}
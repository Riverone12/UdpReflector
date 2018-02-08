package biz.riverone.udpreflector.services

import android.app.Service
import android.content.Intent
import android.os.IBinder
import biz.riverone.udpreflector.AppPreference

/**
 * UdpReflectorService.kt: UDP待ち受けサービス
 * Copyright (C) 2018 J.Kawahara
 * 2018.2.7 J.Kawahara 新規作成
 */

class UdpReflectorService : Service() {

    private val udpReflector = UdpReflector()

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        AppPreference.initialize(applicationContext)
        if (!AppPreference.enabled) {
            return START_NOT_STICKY
        }
        udpReflector.sendAddress = AppPreference.sendAddress
        udpReflector.sendPort = AppPreference.sendPort
        udpReflector.start(applicationContext, AppPreference.receivePort)

        return START_STICKY
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        if (udpReflector.isConnected) {
            udpReflector.disconnect()
        }
    }
}

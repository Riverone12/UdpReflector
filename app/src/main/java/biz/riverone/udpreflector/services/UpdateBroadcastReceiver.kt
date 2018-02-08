package biz.riverone.udpreflector.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import biz.riverone.udpreflector.AppPreference

/**
 * UpdateBroadcastReceiver.kt: アプリがアップロードされた時のイベントハンドラ
 * Created by kawahara on 2018/02/07.
 */
class UpdateBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        val action = intent?.action
        if (context != null && action == Intent.ACTION_MY_PACKAGE_REPLACED) {
            AppPreference.initialize(context)
            if (AppPreference.enabled) {
                val startServiceIntent = Intent(context, UdpReflectorService::class.java)
                context.startService(startServiceIntent)
            }
        }
    }
}
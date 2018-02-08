package biz.riverone.udpreflector.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import biz.riverone.udpreflector.AppPreference

/**
 * BootCompleteReceiver.kt: システムが起動完了した時の処理
 * Created by kawahara on 2018/02/07.
 */
class BootCompleteReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (context != null) {
            AppPreference.initialize(context)
            if (AppPreference.enabled) {
                val startServiceIntent = Intent(context, UdpReflectorService::class.java)
                context.startService(startServiceIntent)
            }
        }
    }
}
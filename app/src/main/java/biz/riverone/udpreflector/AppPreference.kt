package biz.riverone.udpreflector

import android.content.Context
import android.preference.PreferenceManager

/**
 * AppPreference.kt: このアプリの設定項目
 * Created by kawahara on 2018/02/07.
 */
object AppPreference {
    private const val PREFERENCE_VERSION = 1

    // サービスの稼働可否
    var enabled = false

    // 待ち受けポート
    var receivePort: Int = 51234

    // 送信先IPアドレス
    var sendAddress0: Int = 192
    var sendAddress1: Int = 168
    var sendAddress2: Int = 1
    var sendAddress3: Int = 255

    val sendAddress: String
        get() { return "$sendAddress0.$sendAddress1.$sendAddress2.$sendAddress3" }

    // 送信先ポート
    var sendPort: Int = 51234

    private const val PREF_KEY_VERSION = "pref_version"
    private const val PREF_KEY_ENABLED = "pref_enabled"
    private const val PREF_KEY_RECEIVE_PORT = "pref_receive_port"
    private const val PREF_KEY_SEND_ADDRESS0 = "pref_send_address0"
    private const val PREF_KEY_SEND_ADDRESS1 = "pref_send_address1"
    private const val PREF_KEY_SEND_ADDRESS2 = "pref_send_address2"
    private const val PREF_KEY_SEND_ADDRESS3 = "pref_send_address3"
    private const val PREF_KEY_SEND_PORT = "pref_send_port"

    fun initialize(applicationContext: Context) {
        val pref = PreferenceManager.getDefaultSharedPreferences(applicationContext)

        enabled = pref.getBoolean(PREF_KEY_ENABLED, false)
        receivePort = pref.getInt(PREF_KEY_RECEIVE_PORT, 51234)
        sendAddress0 = pref.getInt(PREF_KEY_SEND_ADDRESS0, 192)
        sendAddress1 = pref.getInt(PREF_KEY_SEND_ADDRESS1, 168)
        sendAddress2 = pref.getInt(PREF_KEY_SEND_ADDRESS2, 1)
        sendAddress3 = pref.getInt(PREF_KEY_SEND_ADDRESS3, 255)
        sendPort = pref.getInt(PREF_KEY_SEND_PORT, 51234)

        val version = pref.getInt(PREF_KEY_VERSION, 0)
        if (version < PREFERENCE_VERSION) {
            saveAll(applicationContext)
        }
    }

    fun saveAll(applicationContext: Context) {
        val pref = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        val editor = pref.edit()

        editor.putInt(PREF_KEY_VERSION, PREFERENCE_VERSION)
        editor.putBoolean(PREF_KEY_ENABLED, enabled)
        editor.putInt(PREF_KEY_RECEIVE_PORT, receivePort)
        editor.putInt(PREF_KEY_SEND_ADDRESS0, sendAddress0)
        editor.putInt(PREF_KEY_SEND_ADDRESS1, sendAddress1)
        editor.putInt(PREF_KEY_SEND_ADDRESS2, sendAddress2)
        editor.putInt(PREF_KEY_SEND_ADDRESS3, sendAddress3)
        editor.putInt(PREF_KEY_SEND_PORT, sendPort)

        editor.apply()
    }
}
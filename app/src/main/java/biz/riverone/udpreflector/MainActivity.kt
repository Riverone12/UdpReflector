package biz.riverone.udpreflector

import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.net.wifi.WifiManager
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.widget.CompoundButton
import android.widget.EditText
import android.widget.Switch
import android.widget.TextView
import biz.riverone.udpreflector.services.UdpReflectorService
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds

/**
 * UDPリフレクター
 * Copyright (C) 2018 J.Kawahara
 * 2018.2.7 J.Kawahara 新規作成
 * 2018.2.7 J.Kawahara ver.1.00 初版公開
 * 2018.2.8 J.Kawahara ver.1.02 Firebase 対応
 * 2018.2.16 J.Kawahara ver.1.03 丸型アイコンを変更
 * 2018.3.10 J.Kawahara ver.1.04 AdMob を追加
 */
class MainActivity : AppCompatActivity() {

    private val editTextReceivePort by lazy { findViewById<EditText>(R.id.editTextReceivePort) }
    private val editTextSendPort by lazy { findViewById<EditText>(R.id.editTextSendPort) }

    private val editTextSendAddress0 by lazy { findViewById<EditText>(R.id.editTextSendAddress0) }
    private val editTextSendAddress1 by lazy { findViewById<EditText>(R.id.editTextSendAddress1) }
    private val editTextSendAddress2 by lazy { findViewById<EditText>(R.id.editTextSendAddress2) }
    private val editTextSendAddress3 by lazy { findViewById<EditText>(R.id.editTextSendAddress3) }

    private val switchEnabled by lazy { findViewById<Switch>(R.id.switchEnabled) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 画面をポートレートに固定する
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        initializeControls()

        // AdMob
        MobileAds.initialize(applicationContext, "ca-app-pub-1882812461462801~4821961708")
        val adView = findViewById<AdView>(R.id.adView)
        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)
    }

    private val textWatcher = object: TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) { }
        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) { }
        override fun afterTextChanged(p0: Editable?) {
            // 編集を行った場合、一旦通信を切断する
            if (switchEnabled.isChecked) {
                stopService()
                switchEnabled.isChecked = false
            }
        }
    }

    private fun initializeControls() {
        editTextReceivePort.addTextChangedListener(textWatcher)
        editTextSendPort.addTextChangedListener(textWatcher)
        editTextSendAddress0.addTextChangedListener(textWatcher)
        editTextSendAddress1.addTextChangedListener(textWatcher)
        editTextSendAddress2.addTextChangedListener(textWatcher)
        editTextSendAddress3.addTextChangedListener(textWatcher)

        switchEnabled.setOnCheckedChangeListener(switchCheckedChangeListener)
    }

    override fun onResume() {
        super.onResume()

        // 設定値を読み込む
        AppPreference.initialize(applicationContext)

        editTextReceivePort.setText(AppPreference.sendPort.toString())
        editTextSendPort.setText(AppPreference.sendPort.toString())
        editTextSendAddress0.setText(AppPreference.sendAddress0.toString())
        editTextSendAddress1.setText(AppPreference.sendAddress1.toString())
        editTextSendAddress2.setText(AppPreference.sendAddress2.toString())
        editTextSendAddress3.setText(AppPreference.sendAddress3.toString())

        switchEnabled.isChecked = AppPreference.enabled

        // 自機のIPアドレスを取得・表示する
        val textViewMyIpAddress = findViewById<TextView>(R.id.textViewMyIpAddress)
        textViewMyIpAddress.text = myIpAddress()
    }

    override fun onPause() {
        super.onPause()
        saveConfiguration()
    }

    private val switchCheckedChangeListener = CompoundButton.OnCheckedChangeListener {
        _, isChecked ->

        if (isChecked) {
            // 設定値を保存して、サービスを開始する
            saveConfiguration()
            startService()
        } else {
            // サービスを停止する
            stopService()
        }
    }

    private fun editTextToInt(editText: EditText): Int {
        val value = editText.text.toString()
        return try {
            value.toInt()
        }
        catch (e: NumberFormatException) {
            0
        }
    }

    private fun saveConfiguration() {
        AppPreference.enabled = switchEnabled.isChecked
        AppPreference.receivePort = editTextToInt(editTextReceivePort)
        AppPreference.sendAddress0 = editTextToInt(editTextSendAddress0)
        AppPreference.sendAddress1 = editTextToInt(editTextSendAddress1)
        AppPreference.sendAddress2 = editTextToInt(editTextSendAddress2)
        AppPreference.sendAddress3 = editTextToInt(editTextSendAddress3)
        AppPreference.sendPort = editTextToInt(editTextSendPort)

        AppPreference.saveAll(applicationContext)
    }

    private fun startService() {
        // 一旦サービスを停止する
        stopService()

        // サービス開始のインテントを発行する
        val startServiceIntent = Intent(this, UdpReflectorService::class.java)
        startService(startServiceIntent)
    }

    private fun stopService() {
        val stopServiceIntent = Intent(this, UdpReflectorService::class.java)
        stopService(stopServiceIntent)
    }

    // 自機のIPアドレスを取得する
    private fun myIpAddress(): String {
        val wifiManager = applicationContext
                .getSystemService(Context.WIFI_SERVICE) as WifiManager

        val myIp = wifiManager.connectionInfo.ipAddress
        return ((myIp shr 0) and 0xff).toString() + "." +
                ((myIp shr 8) and 0xff).toString() + "." +
                ((myIp shr 16) and 0xff).toString() + "." +
                ((myIp shr 24) and 0xff).toString()
    }
}

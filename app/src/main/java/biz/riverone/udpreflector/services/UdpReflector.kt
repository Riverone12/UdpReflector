package biz.riverone.udpreflector.services

import android.content.Context
import android.net.wifi.WifiManager
import android.util.Log
import java.net.*
import java.util.*

/**
 * UdpReflector.ktt: UDP受信
 * Created by kawahara on 2018/02/07.
 */
class UdpReflector {

    private var connected = false
    val isConnected: Boolean get() { return connected }

    var sendAddress: String = "192.168.1.255"
    var sendPort: Int = 51234

    private var myIpAddress: String = ""
    private var listenPort: Int = 51234

    fun start(context: Context, listenPort: Int) {
        myIpAddress = myIpAddress(context)

        this.listenPort = listenPort
        val thread = Thread {
            receiveStart()
        }
        thread.start()
    }

    fun disconnect() {
        // 受信ループを抜けるためのフラグを立てる
        connected = false

        // 受信ループを抜けるため、ダミーのパケットを送信する
        val thread = Thread {
            try {
                val data = ByteArray(0)
                val packet = DatagramPacket(
                        data,
                        data.size,
                        InetAddress.getByName("127.0.0.1"),
                        listenPort
                )
                val socket = DatagramSocket()
                socket.send(packet)
            }
            catch (e: SocketException) {
                e.printStackTrace()
            }
            catch (e: Exception) {
                e.printStackTrace()
            }
        }
        thread.start()
    }

    private fun receiveStart() {
        log("receive start listen $myIpAddress:$listenPort")

        try {
            // ソケット接続
            val udpSocket = DatagramSocket(null)
            udpSocket.reuseAddress = true
            udpSocket.broadcast = true
            udpSocket.bind(InetSocketAddress(listenPort))

            val receivedBytes = ByteArray(1024)
            val receivedPacket = DatagramPacket(receivedBytes, receivedBytes.size)

            // 受信ループ
            connected = true
            while (connected) {
                // 受信を開始する
                // （ここでブロックされるため、
                // connected をfalse にしただけではループを抜けないので注意!!）
                udpSocket.receive(receivedPacket)

                // 送信元が自分自身の場合、何も行わない
                val socketAddress = receivedPacket.socketAddress.toString()
                if (socketAddress.indexOf(myIpAddress) >= 0) {
                    continue
                } else if (socketAddress.indexOf("127.0.0.1") >= 0) {
                    continue
                } else if (socketAddress.indexOf("localhost") >= 0) {
                    continue
                }

                val receivedLength = receivedPacket.length
                if (receivedLength > 0) {
                    // 受信したデータを転送する
                    val ipAddress = InetAddress.getByName(sendAddress)
                    val sendPacket = DatagramPacket(
                            receivedBytes,
                            receivedLength,
                            ipAddress, sendPort)

                    udpSocket.send(sendPacket)
                    log("send to $sendAddress:$sendPort ($receivedLength bytes)")
                }
            }
            udpSocket.close()
        }
        catch (e: SocketException) {
            e.printStackTrace()
        }
        catch (e: Exception) {
            e.printStackTrace()
        }
        log("receive stop")
    }

    private fun log(message: String) {
        val cal = Calendar.getInstance()
        val year = cal.get(Calendar.YEAR)
        val month = cal.get(Calendar.MONTH) + 1
        val day = cal.get(Calendar.DAY_OF_MONTH)
        val hour = cal.get(Calendar.HOUR_OF_DAY)
        val min = cal.get(Calendar.MINUTE)
        val sec = cal.get(Calendar.SECOND)
        val dt = "$year/$month/$day $hour:$min:$sec"

        Log.d("UdpReflector", "[$dt] $message")
    }

    // 自機のIPアドレスを取得する
    private fun myIpAddress(context: Context): String {
        val wifiManager = context.applicationContext
                .getSystemService(Context.WIFI_SERVICE) as WifiManager

        val myIp = wifiManager.connectionInfo.ipAddress
        return ((myIp shr 0) and 0xff).toString() + "." +
                ((myIp shr 8) and 0xff).toString() + "." +
                ((myIp shr 16) and 0xff).toString() + "." +
                ((myIp shr 24) and 0xff).toString()
    }
}
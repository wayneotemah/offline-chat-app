package com.example.chat_app

import android.os.Bundle
import android.os.StrictMode
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugins.Pigeon
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.*
import java.util.*


class MainActivity : FlutterActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)

        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        // rest of your code
    }

    private class ChatApi : Pigeon.ChatApi {
        override fun search(keyword: String): MutableList<Pigeon.Chat> {
            val random = Random()
            val str = "https://source.unsplash.com/random/?book?sig=" + random.nextInt()
            val chat = Pigeon.Chat()
            val client = Trial()
            client.run()
            chat.clients = "changed"
            chat.message = ""
            return Collections.singletonList(chat)
        }
    }

    override fun configureFlutterEngine(flutterEngine: FlutterEngine) {
        super.configureFlutterEngine(flutterEngine)
        Pigeon.ChatApi.setup(flutterEngine.dartExecutor.binaryMessenger, ChatApi())
    }


    class Trial {
        private var `in`: BufferedReader? = null
        var out: PrintWriter? = null
        private val name: String
            private get() {
                return android.os.Build.MODEL
            }

        @Throws(Exception::class)
        fun run() {
            val serverAddress = "192.168.1.202"
            val socket = Socket(serverAddress, 9001)
            `in` = BufferedReader(InputStreamReader(socket.getInputStream()))
            out = PrintWriter(socket.getOutputStream(), true)
            while (true) {
                val line: String = `in`!!.readLine()
                if (line.startsWith("SUBMITNAME")) {
                    out!!.println(name)
                } else if (line.startsWith("NAMEACCEPTED")) {
                    println("name accepted")
                } else if (line.startsWith("MESSAGE")) {
                    println(
                        """
                        ${line.substring(8)}
                        
                        """.trimIndent()
                    )
                } else if (line.startsWith("CONTACTS")) {
                    println("Contacts ${line.substring(8)}")

                } else if (line.startsWith("DISCONNECT")) {
                    println("disconnect ${line.substring(10)}")

                }
            }
        }

        private object ServerDiscovery {
            private const val BROADCAST_IP = "255.255.255.255"

            @get:Throws(Exception::class)
            val serverAddress: String
                get() {
                    val socket = DatagramSocket()
                    socket.broadcast = true
                    val sendData = "DISCOVER_SERVER_REQUEST".toByteArray()
                    val sendPacket = DatagramPacket(
                        sendData, sendData.size, InetAddress.getByName(
                            BROADCAST_IP
                        ), 9001
                    )
                    socket.send(sendPacket)
                    val receiveData = ByteArray(1024)
                    val receivePacket = DatagramPacket(receiveData, receiveData.size)
                    while (true) {
                        println("Looking  for server in local net")
                        socket.receive(receivePacket)
                        val message: String = String(receivePacket.data).trim { it <= ' ' }
                        if (message.startsWith("DISCOVER_SERVER_RESPONSE")) {
                            System.out.println(
                                "Found server at " + receivePacket.address.hostAddress
                            )
                            return receivePacket.address.hostAddress as String
                        }
                    }
                }
        }

//        companion object {
//            @Throws(Exception::class)
//            @JvmStatic
//            fun main() {
//
//            }
//        }
    }


}




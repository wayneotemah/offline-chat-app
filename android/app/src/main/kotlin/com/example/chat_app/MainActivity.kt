package com.example.chat_app
import android.os.Bundle
import android.os.StrictMode
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugins.Pigeon
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.*
import java.util.*
import com.example.chat_app.MySocket

class MainActivity : FlutterActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)

        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        // rest of your code
    }
    private class ChatApi: Pigeon.ChatApi {
        override fun search(keyword: String): MutableList<Pigeon.Chat> {
            val random = Random()
            val str = "https://source.unsplash.com/random/?book?sig=" + random.nextInt()
            val chat = Pigeon.Chat()
            val newSocket: MySocket = MySocket()
            newSocket.main()
            chat.clients = "changed"
            chat.message = InetAddress.getByName("192.168.137.137").toString()

            return Collections.singletonList(chat)
        }
    }
    override fun configureFlutterEngine(flutterEngine: FlutterEngine) {
        super.configureFlutterEngine(flutterEngine)
        Pigeon.ChatApi.setup(flutterEngine.dartExecutor.binaryMessenger, ChatApi())
    }
}
object Main : Thread() {

    private const val PORT = 9000

    private val names = HashSet<String>()


    private val writers = HashSet<PrintWriter>()


    @Throws(Exception::class)
    @JvmStatic
    fun main(args: Array<String>) {
        var socket: Socket? = null
        println("The chat server is running.")
        Thread {
            println("Listening to broadcast messages")
            val serverDiscovery = ServerDiscovery
            try {
                ServerDiscovery.sendAddress()
            } catch (e: Exception) {
                println(e)
            }
        }.start()
        //        ServerDiscovery serverDiscover = new ServerDiscovery();
//        serverDiscover.sendAddress();
        val listener = ServerSocket(PORT)
        try {
            while (true) {
                socket = listener.accept()
                Handler(socket).start()
            }
        } finally {
            listener.close()
        }
    }

    private object ServerDiscovery {
        //        private static final int PORT = 8888;
        @Throws(Exception::class)
        fun sendAddress() {
            val socket = DatagramSocket(PORT)
            val receiveData = ByteArray(1024)
            val receivePacket = DatagramPacket(receiveData, receiveData.size)
            do {
                println("Received a message")
                socket.receive(receivePacket)
                val message = String(receivePacket.data).trim { it <= ' ' }
                if (message == "DISCOVER_SERVER_REQUEST") {
                    println("Received a discover message")

                    // Get server's IP address
                    val serverAddress = receivePacket.address

                    // Get server's hostname
                    val serverHostname = serverAddress.hostName

                    // Send response back to client
                    val response =
                        "DISCOVER_SERVER_RESPONSE " + serverAddress.hostAddress + " " + serverHostname
                    val sendData = response.toByteArray()
                    val sendPacket = DatagramPacket(
                        sendData,
                        sendData.size,
                        receivePacket.address,
                        receivePacket.port
                    )
                    socket.send(sendPacket)
                }
            } while (true)
        }
    }

    private class Handler
        (private val socket: Socket?) : Thread() {
        private var username: String? = null
        private var `in`: BufferedReader? = null
        private var out: PrintWriter? = null


        override fun run() {
            try {

                // Create character streams for the socket.
                `in` = BufferedReader(
                    InputStreamReader(
                        socket!!.getInputStream()
                    )
                )
                out = PrintWriter(socket.getOutputStream(), true)


                while (true) {
                    out!!.println("SUBMITNAME")
                    username = `in`!!.readLine()
                    if (username == null) {
                        return
                    }
                    username = username!!.split("/").toTypedArray()[0]
                    println("$username :Has connected")
                    synchronized(names) {
                        if (!names.contains(username)) {
                            names.add(username!!)
                            return
                        }
                    }
                }


                out!!.println("NAMEACCEPTED")
                writers.add(out!!)
                for (writer in writers) {
                    writer.println("DISCONNECT $username has connected")
                    writer.println("CONTACTS " + names.toString())
                }



                while (true) {
                    val input = `in`!!.readLine() ?: return
                    for (writer in writers) {
                        writer.println("MESSAGE $username: $input")
                    }
                }
            } catch (e: IOException) {
                println("$username Has disconnected")
            } finally {

                if (username != null) {
                    names.remove(username)
                    for (writer in writers) {
                        writer.println("DISCONNECT $username has disconnected")
                        writer.println("CONTACTS " + names.toString())
                    }
                }
                if (out != null) {
                    writers.remove(out)
                }
                try {
                    socket!!.close()
                } catch (e: IOException) {
                    println(e)
                }
            }
        }
    }
}



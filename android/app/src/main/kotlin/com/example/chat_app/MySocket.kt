package com.example.chat_app

import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.ServerSocket
import java.net.Socket
import java.util.HashSet

class MySocket : Thread() {

    private val PORT = 9000

    private val names = HashSet<String>()


    private val writers = HashSet<PrintWriter>()

    public fun main() {
        var socket: Socket? = null
        println("The chat server is running.")
        Thread {
            println("Listening to broadcast messages")
            val serverDiscovery = ServerDiscovery()
            try {
                ServerDiscovery().sendAddress()
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

    private class ServerDiscovery {
        //        private static final int PORT = 8888;
        @Throws(Exception::class)
        fun sendAddress() {
            val socket = DatagramSocket(MySocket().PORT)
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
                    synchronized(MySocket().names) {
                        if (!MySocket().names.contains(username)) {
                            MySocket().names.add(username!!)
                            return
                        }
                    }
                }


                out!!.println("NAMEACCEPTED")
                MySocket().writers.add(out!!)
                for (writer in MySocket().writers) {
                    writer.println("DISCONNECT $username has connected")
                    writer.println("CONTACTS " + MySocket().names.toString())
                }



                while (true) {
                    val input = `in`!!.readLine() ?: return
                    for (writer in MySocket().writers) {
                        writer.println("MESSAGE $username: $input")
                    }
                }
            } catch (e: IOException) {
                println("$username Has disconnected")
            } finally {

                if (username != null) {
                    MySocket().names.remove(username)
                    for (writer in MySocket().writers) {
                        writer.println("DISCONNECT $username has disconnected")
                        writer.println("CONTACTS " + MySocket().names.toString())
                    }
                }
                if (out != null) {
                    MySocket().writers.remove(out)
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

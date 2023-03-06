//package com.example.chat_app
//
//class trial {
//    var `in`: BufferedReader? = null
//    var out: PrintWriter? = null
//    private val name: String
//        private get() {
//            val ip: InetAddress
//            ip = try {
//                InetAddress.getLocalHost()
//            } catch (e: UnknownHostException) {
//                throw RuntimeException(e)
//            }
//            return ip.toString()
//        }
//
//    @Throws(Exception::class)
//    private fun run() {
//        val serverAddress = ServerDiscovery.serverAddress
//        val socket = Socket(serverAddress, 9000)
//        `in` = BufferedReader(InputStreamReader(socket.getInputStream()))
//        out = PrintWriter(socket.getOutputStream(), true)
//        while (true) {
//            val line: String = `in`.readLine()
//            if (line.startsWith("SUBMITNAME")) {
//                out.println(name)
//            } else if (line.startsWith("NAMEACCEPTED")) {
//                textField.setEditable(true)
//            } else if (line.startsWith("MESSAGE")) {
//                messageArea.append(
//                    """
//                        ${line.substring(8)}
//
//                        """.trimIndent()
//                )
//            } else if (line.startsWith("CONTACTS")) {
//                contactField.setText(line.substring(8))
//                contactField.setEditable(false)
//            } else if (line.startsWith("DISCONNECT")) {
//                statusField.setText(line.substring(10))
//                statusField.setEditable(false)
//            }
//        }
//    }
//
//    private object ServerDiscovery {
//        private const val BROADCAST_IP = "255.255.255.255"
//
//        @get:Throws(Exception::class)
//        val serverAddress: String
//            get() {
//                val socket = DatagramSocket()
//                socket.setBroadcast(true)
//                val sendData = "DISCOVER_SERVER_REQUEST".toByteArray()
//                val sendPacket = DatagramPacket(
//                    sendData, sendData.size, InetAddress.getByName(
//                        BROADCAST_IP
//                    ), 9000
//                )
//                socket.send(sendPacket)
//                val receiveData = ByteArray(1024)
//                val receivePacket = DatagramPacket(receiveData, receiveData.size)
//                while (true) {
//                    println("Looking  for server in local net")
//                    socket.receive(receivePacket)
//                    val message: String = String(receivePacket.getData()).trim { it <= ' ' }
//                    if (message.startsWith("DISCOVER_SERVER_RESPONSE")) {
//                        System.out.println(
//                            "Found server at " + receivePacket.getAddress().getHostAddress()
//                        )
//                        return receivePacket.getAddress().getHostAddress()
//                    }
//                }
//            }
//    }
//
//    companion object {
//        @Throws(Exception::class)
//        @JvmStatic
//        fun main(args: Array<String>) {
//            val client = trial()
//            client.run()
//        }
//    }
//}
//
//
//

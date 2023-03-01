package com.example.chat_app

import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugins.Pigeon
import java.util.*

class MainActivity : FlutterActivity() {
    private class ChatApi: Pigeon.ChatApi {
        override fun search(keyword: String?): List<Pigeon.Chat> {
            val random = Random()
            val str = "https://source.unsplash.com/random/?book?sig=" + random.nextInt()
            val chat = Pigeon.Chat()
            chat.clients = "Hello"
            chat.message = "message"
            return Collections.singletonList(chat)
        }
    }
    override fun configureFlutterEngine(flutterEngine: FlutterEngine) {
        super.configureFlutterEngine(flutterEngine)

        Pigeon.ChatApi.setup(flutterEngine.dartExecutor.binaryMessenger, ChatApi())
    }
}

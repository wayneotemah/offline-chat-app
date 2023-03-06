import 'dart:async';

import 'package:chat_app/pigeon.dart';
import 'package:flutter/material.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Flutter Demo',
      theme: ThemeData(
        primarySwatch: Colors.blue,
      ),
      home: const MessageBoard(),
    );
  }
}

class MyHomePage extends StatefulWidget {
  MyHomePage({super.key, required this.title});

  final String title;

  @override
  State<MyHomePage> createState() => _MyHomePageState();
}

class _MyHomePageState extends State<MyHomePage> {
  int _counter = 0;

  List<Chat> chats = [];

  Future getChat() async {
    final List<Chat?> chats = await ChatApi().search('Killer');
    final newChats = List<Chat>.from(chats);
    print(
        '${chats[0]!.message} #################################################');
    print('${chats[0]!.clients}');
  }

  void startTimer() {
    Timer.periodic(const Duration(seconds: 1), (timer) {
      getChat();
    });
  }

  @override
  void dispose() {
    print('disposed');
    super.dispose();
  }

  @override
  void initState() {
    startTimer();

    super.initState();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text(widget.title),
      ),
      body: const Center(
        child: MessageBoard(),
      ),
    );
  }
}

class Message {
  final String author;
  final String text;

  Message({required this.author, required this.text});
}

class MessageBoard extends StatefulWidget {
  const MessageBoard({Key? key}) : super(key: key);

  @override
  _MessageBoardState createState() => _MessageBoardState();
}

class _MessageBoardState extends State<MessageBoard> {
  final List<Message> _messages = [];

  final _authorController = TextEditingController();
  final _textController = TextEditingController();

  @override
  void dispose() {
    _authorController.dispose();
    _textController.dispose();
    super.dispose();
  }

  void _addMessage() {
    final author = _authorController.text;
    final text = _textController.text;

    if (author.isNotEmpty && text.isNotEmpty) {
      final message = Message(author: author, text: text);
      setState(() {
        _messages.add(message);
      });
      _authorController.clear();
      _textController.clear();
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Message Board'),
      ),
      body: Column(
        children: [
          Expanded(
            child: ListView.builder(
              itemCount: _messages.length,
              itemBuilder: (context, index) {
                final message = _messages[index];
                return ListTile(
                  title: Text(message.author),
                  subtitle: Text(message.text),
                );
              },
            ),
          ),
          Padding(
            padding: const EdgeInsets.all(16.0),
            child: Column(
              children: [
                TextField(
                  controller: _authorController,
                  decoration: const InputDecoration(
                    hintText: 'Enter your name',
                  ),
                ),
                TextField(
                  controller: _textController,
                  decoration: const InputDecoration(
                    hintText: 'Enter your message',
                  ),
                ),
                ElevatedButton(
                  onPressed: _addMessage,
                  child: const Text('Send'),
                ),
              ],
            ),
          ),
        ],
      ),
    );
  }
}

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
      home: MyHomePage(title: 'Flutter Demo Home Page'),
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
    print('${chats[0]!.message}');
    print('${chats[0]!.clients}');
  }

  void startTimer() {
    Timer.periodic(const Duration(seconds: 3), (timer) {
      getChat();
    });
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
      body: Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: <Widget>[
            const Text(
              'You have pushed the button this many times:',
            ),
            Text(
              '$_counter',
              style: Theme.of(context).textTheme.headlineMedium,
            ),
          ],
        ),
      ),
      floatingActionButton: FloatingActionButton(
        onPressed: getChat,
        tooltip: 'Increment',
        child: const Icon(Icons.add),
      ),
    );
  }
}

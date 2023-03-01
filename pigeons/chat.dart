import 'package:pigeon/pigeon.dart';

class Chat {
  String? message;
  String? clients;
}

@FlutterApi()
abstract class ChatApi {
  List<Chat?> search(String keyword);
}

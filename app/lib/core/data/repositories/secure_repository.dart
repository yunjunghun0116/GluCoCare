import 'package:flutter_secure_storage/flutter_secure_storage.dart';

class SecureRepository {
  static final SecureRepository _instance = SecureRepository._internal();

  factory SecureRepository() => _instance;

  late final FlutterSecureStorage _storage;

  SecureRepository._internal() {
    _storage = const FlutterSecureStorage();
  }

  static final String _refreshTokenKey = "REFRESH_TOKEN";

  Future<void> writeRefreshToken(String token) async {
    await _storage.write(key: _refreshTokenKey, value: token);
  }

  Future<String?> readRefreshToken() async {
    return await _storage.read(key: _refreshTokenKey);
  }

  Future<void> deleteRefreshToken() async {
    await _storage.delete(key: _refreshTokenKey);
  }
}

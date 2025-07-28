import 'package:app/core/domain/repositories/local_repository.dart';
import 'package:shared_preferences/shared_preferences.dart';

import '../../exceptions/custom_exception.dart';
import '../../exceptions/exception_message.dart';

class LocalRepositoryImpl implements LocalRepository {
  static final LocalRepositoryImpl _instance = LocalRepositoryImpl._internal();

  factory LocalRepositoryImpl() => _instance;

  LocalRepositoryImpl._internal();

  late final SharedPreferences _preferences;

  @override
  Future<void> initialize() async {
    _preferences = await SharedPreferences.getInstance();
  }

  @override
  void delete(String key) {
    if (!_preferences.containsKey(key)) return;
    _preferences.remove(key);
  }

  @override
  T read<T>(String key) {
    if (!_preferences.containsKey(key)) throw CustomException(ExceptionMessage.badRequest);
    dynamic value = _preferences.get(key);
    if (value is T) {
      return value;
    }
    throw CustomException(ExceptionMessage.invalidType);
  }

  @override
  Future<void> save<T>(String key, value) async {
    if (value is String) {
      await _preferences.setString(key, value);
      return;
    }
    if (value is int) {
      await _preferences.setInt(key, value);
      return;
    }
    if (value is bool) {
      await _preferences.setBool(key, value);
      return;
    }
    if (value is double) {
      await _preferences.setDouble(key, value);
      return;
    }
    if (value is List<String>) {
      await _preferences.setStringList(key, value);
      return;
    }
    throw CustomException(ExceptionMessage.invalidType);
  }
}

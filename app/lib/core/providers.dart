import 'package:dio/dio.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

import 'data/repositories/local_repository_impl.dart';
import 'domain/repositories/local_repository.dart';

// --- LocalRepositories Providers ---
final localRepositoryProvider = Provider<LocalRepository>((ref) => LocalRepositoryImpl());

// --- HTTP Providers ---
final dioProvider = Provider<Dio>(
  (ref) => Dio(
    BaseOptions(
      connectTimeout: Duration(seconds: 5),
      receiveTimeout: Duration(seconds: 5),
      headers: {'Content-Type': 'application/json', 'ngrok-skip-browser-warning': true},
    ),
  ),
);

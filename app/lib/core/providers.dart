import 'package:app/core/http/auth_interceptor.dart';
import 'package:dio/dio.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../shared/constants/app_values.dart';

// --- HTTP Providers ---
final dioProvider = Provider<Dio>((ref) {
  var dio = Dio(
    BaseOptions(
      baseUrl: AppValues.serverBaseUrl,
      connectTimeout: Duration(seconds: 5),
      receiveTimeout: Duration(seconds: 5),
      headers: {'Content-Type': 'application/json', 'ngrok-skip-browser-warning': 'true'},
    ),
  );
  var refreshDio = Dio(
    BaseOptions(
      baseUrl: AppValues.serverBaseUrl,
      headers: {'Content-Type': 'application/json', 'ngrok-skip-browser-warning': 'true'},
    ),
  );
  dio.interceptors.add(AuthInterceptor(refreshDio));
  return dio;
});

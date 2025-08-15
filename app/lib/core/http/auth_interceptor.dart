import 'dart:developer';

import 'package:app/core/data/repositories/local_repository.dart';
import 'package:app/core/data/repositories/secure_repository.dart';
import 'package:app/core/exceptions/custom_exception.dart';
import 'package:app/features/auth/data/models/token_response.dart';
import 'package:app/shared/constants/local_repository_key.dart';
import 'package:dio/dio.dart';

class AuthInterceptor extends Interceptor {
  final Dio _dio;

  AuthInterceptor(this._dio);

  @override
  void onRequest(RequestOptions options, RequestInterceptorHandler handler) async {
    try {
      var accessToken = await LocalRepository().read(LocalRepositoryKey.accessToken);
      options.headers['Authorization'] = 'Bearer $accessToken';
    } on CustomException catch (e) {
      log("[Interceptor] AccessToken 없음");
    }
    return handler.next(options);
  }

  @override
  void onError(DioException err, ErrorInterceptorHandler handler) async {
    if (err.response?.statusCode == 401) {
      log("[Interceptor] 401 → accessToken 재발급 시도");

      var refreshToken = await SecureRepository().readRefreshToken();
      if (refreshToken == null) {
        log("[Interceptor] refreshToken 없음 → 로그아웃 필요");
        return handler.next(err);
      }
      var success = await _refreshToken(refreshToken);

      if (success) {
        log("[Interceptor] 토큰 재발급 성공");
        var accessToken = LocalRepository().read<String>(LocalRepositoryKey.accessToken);

        var retryRequest = err.requestOptions;
        retryRequest.headers['Authorization'] = 'Bearer $accessToken';

        try {
          var response = await _dio.fetch(retryRequest);
          return handler.resolve(response);
        } catch (e) {
          return handler.reject(e as DioException);
        }
      } else {
        log("[Interceptor] accessToken 재발급 실패 → 로그아웃");
        return handler.next(err);
      }
    } else {
      return handler.next(err);
    }
  }

  Future<bool> _refreshToken(String refreshToken) async {
    try {
      var response = await _dio.post('/api/members/refresh-token', data: {'token': refreshToken});

      var tokenResponse = TokenResponse.fromJson(response.data);
      await LocalRepository().save<String>(LocalRepositoryKey.accessToken, tokenResponse.accessToken);
      await SecureRepository().writeRefreshToken(tokenResponse.refreshToken);
      return true;
    } catch (e) {
      log("[Interceptor] 토큰 재발급 실패: $e");
      return false;
    }
  }
}

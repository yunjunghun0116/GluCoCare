import 'package:app/core/exceptions/exception_message.dart';
import 'package:dio/dio.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

import '../exceptions/custom_exception.dart';

abstract class BaseController<S extends BaseState> extends StateNotifier<S> {
  final Dio _dio;

  BaseController(super.state, this._dio);

  Future<Response> getRequest(String url, {Map<String, dynamic>? queryParameters, Map<String, String>? headers}) async {
    try {
      if (state.isLoading) throw CustomException(ExceptionMessage.progressing);
      state = state.copyWith(isLoading: true) as S;

      var response = await _dio.get(
        url,
        queryParameters: queryParameters,
        options: Options(headers: headers),
      );
      return response;
    } finally {
      state = state.copyWith(isLoading: false) as S;
    }
  }

  Future<Response> postRequest(String url, {dynamic data, Map<String, String>? headers}) async {
    try {
      if (state.isLoading) throw CustomException(ExceptionMessage.progressing);
      state = state.copyWith(isLoading: true) as S;

      var response = await _dio.post(
        url,
        data: data,
        options: Options(headers: headers),
      );
      return response;
    } finally {
      state = state.copyWith(isLoading: false) as S;
    }
  }

  Future<Response> putRequest(String url, {dynamic data, Map<String, String>? headers}) async {
    try {
      if (state.isLoading) throw CustomException(ExceptionMessage.progressing);
      state = state.copyWith(isLoading: true) as S;

      var response = await _dio.put(
        url,
        data: data,
        options: Options(headers: headers),
      );
      return response;
    } finally {
      state = state.copyWith(isLoading: false) as S;
    }
  }

  Future<Response> patchRequest(String url, {dynamic data, Map<String, String>? headers}) async {
    try {
      if (state.isLoading) throw CustomException(ExceptionMessage.progressing);
      state = state.copyWith(isLoading: true) as S;

      var response = await _dio.patch(
        url,
        data: data,
        options: Options(headers: headers),
      );
      return response;
    } finally {
      state = state.copyWith(isLoading: false) as S;
    }
  }

  Future<Response> deleteRequest(String url, {Map<String, String>? headers}) async {
    try {
      if (state.isLoading) throw CustomException(ExceptionMessage.progressing);
      state = state.copyWith(isLoading: true) as S;

      var response = await _dio.delete(url, options: Options(headers: headers));
      return response;
    } finally {
      state = state.copyWith(isLoading: false) as S;
    }
  }
}

class BaseState {
  final bool isLoading;

  const BaseState({this.isLoading = false});

  BaseState copyWith({bool? isLoading}) {
    return BaseState(isLoading: isLoading ?? this.isLoading);
  }
}

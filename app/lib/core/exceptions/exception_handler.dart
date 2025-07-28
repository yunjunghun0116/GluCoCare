import 'dart:developer';

import 'package:app/shared/utils/local_util.dart';
import 'package:flutter/material.dart';

import 'custom_exception.dart';

class ExceptionHandler {
  static final ExceptionHandler _instance = ExceptionHandler._internal();

  factory ExceptionHandler() => _instance;

  ExceptionHandler._internal();

  void handleException(Object error, StackTrace? stackTrace, BuildContext? context) {
    log("StackTrace : ${stackTrace.toString()}");
    if (error.runtimeType == CustomException) {
      var customException = error as CustomException;
      log("에러 발생(${customException.exception.statusCode}) : ${customException.exception.description}");
      if (context != null) {
        LocalUtil.showMessage(context, message: customException.exception.description);
      }
      return;
    }
    log("시스템 내부 에러 발생 : $error, StackTrace : $stackTrace");
  }
}

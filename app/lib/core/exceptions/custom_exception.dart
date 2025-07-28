import 'exception_message.dart';

class CustomException implements Exception {
  ExceptionMessage exception;

  CustomException(this.exception);

  @override
  String toString() => "에러 발생(${exception.statusCode}) : ${exception.description}";
}

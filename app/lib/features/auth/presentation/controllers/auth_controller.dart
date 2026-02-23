import 'package:app/core/exceptions/custom_exception.dart';
import 'package:app/core/exceptions/exception_message.dart';
import 'package:app/features/auth/data/models/login_request.dart';
import 'package:app/features/auth/data/models/register_request.dart';
import 'package:app/features/auth/data/models/token_response.dart';

import '../../../../core/base/base_controller.dart';
import '../../../../core/data/repositories/secure_repository.dart';

class AuthController extends BaseController<BaseState> {
  AuthController(super.state, super.dio);

  Future<String?> login(LoginRequest loginRequest) async {
    try {
      var response = await postRequest(
        "/api/members/login",
        data: {"email": loginRequest.email, "password": loginRequest.password},
      );

      if (response.statusCode == 200) {
        var tokenResponse = TokenResponse.fromJson(response.data);
        await SecureRepository().writeRefreshToken(tokenResponse.refreshToken);
        return tokenResponse.accessToken;
      }
    } catch (exception) {
      throw CustomException(ExceptionMessage.wrongEmailOrPassword);
    }
    return null;
  }

  Future<bool> autoLogin() async {
    var response = await getRequest("/api/members/auto-login");
    if (response.statusCode == 200) {
      return true;
    }
    return false;
  }

  Future<String?> register(RegisterRequest registerRequest) async {
    var response = await postRequest(
      "/api/members/register",
      data: {"email": registerRequest.email, "password": registerRequest.password, "name": registerRequest.name},
    );
    if (response.statusCode == 200) {
      var tokenResponse = TokenResponse.fromJson(response.data);
      await SecureRepository().writeRefreshToken(tokenResponse.refreshToken);
      return tokenResponse.accessToken;
    }
    return null;
  }

  Future<bool> validateUniqueEmail(String email) async {
    var response = await postRequest("/api/members/exists-email", data: {"email": email});
    if (response.statusCode == 200) {
      return response.data;
    }
    return false;
  }
}

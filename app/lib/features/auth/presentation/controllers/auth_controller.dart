import 'package:app/features/auth/data/models/login_dto.dart';
import 'package:app/features/auth/data/models/register_dto.dart';
import 'package:app/shared/constants/app_values.dart';

import '../../../../core/base/base_controller.dart';

class AuthController extends BaseController<BaseState> {
  AuthController(super.state, super.dio);

  Future<String?> login(LoginDto loginDto) async {
    var response = await postRequest(
      "${AppValues.serverBaseUrl}/api/members/login",
      data: {"email": loginDto.email, "password": loginDto.password},
    );

    if (response.statusCode == 200) {
      return response.data["token"];
    }
    return null;
  }

  Future<String?> autoLogin(String accessToken) async {
    var response = await postRequest(
      "${AppValues.serverBaseUrl}/api/members/auto-login",
      headers: {"Authorization": getBearerToken(accessToken)},
    );

    if (response.statusCode == 200) {
      return response.data["token"];
    }
    return null;
  }

  Future<String?> register(RegisterDto registerDto) async {
    var response = await postRequest(
      "${AppValues.serverBaseUrl}/api/members/register",
      data: {"email": registerDto.email, "password": registerDto.password, "name": registerDto.name},
    );
    if (response.statusCode == 200) {
      return response.data["token"];
    }
    return null;
  }

  Future<bool> validateUniqueEmail(String email) async {
    var response = await postRequest("${AppValues.serverBaseUrl}/api/members/exists-email", data: {"email": email});
    if (response.statusCode == 200) {
      return response.data;
    }
    return false;
  }
}

import 'package:app/features/auth/data/models/login_dto.dart';
import 'package:app/features/auth/data/models/register_dto.dart';
import 'package:app/features/auth/data/models/token_response_dto.dart';

import '../../../../core/base/base_controller.dart';
import '../../../../core/data/repositories/secure_repository.dart';

class AuthController extends BaseController<BaseState> {
  AuthController(super.state, super.dio);

  Future<String?> login(LoginDto loginDto) async {
    var response = await postRequest(
      "/api/members/login",
      data: {"email": loginDto.email, "password": loginDto.password},
    );

    if (response.statusCode == 200) {
      var tokenResponse = TokenResponseDto.fromJson(response.data);
      await SecureRepository().writeRefreshToken(tokenResponse.refreshToken);
      return tokenResponse.accessToken;
    }
    return null;
  }

  Future<bool> autoLogin(String accessToken) async {
    var response = await getRequest("/api/members/auto-login");
    if (response.statusCode == 200) {
      return true;
    }
    return false;
  }

  Future<String?> register(RegisterDto registerDto) async {
    var response = await postRequest(
      "/api/members/register",
      data: {"email": registerDto.email, "password": registerDto.password, "name": registerDto.name},
    );
    if (response.statusCode == 200) {
      var tokenResponse = TokenResponseDto.fromJson(response.data);
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

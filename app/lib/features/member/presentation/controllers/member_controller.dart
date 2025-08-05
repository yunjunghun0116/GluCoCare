import 'package:app/features/auth/data/models/login_dto.dart';
import 'package:app/features/auth/data/models/register_dto.dart';
import 'package:app/shared/constants/app_values.dart';

import '../../../../core/base/base_controller.dart';

class MemberController extends BaseController<BaseState> {
  MemberController(super.state, super.dio);

  Future<String?> getName(String accessToken) async {
    var response = await getRequest("/api/members/name");
    if (response.statusCode == 200) {
      return response.data;
    }
    return null;
  }
}

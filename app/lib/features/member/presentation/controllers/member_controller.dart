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

import '../../../../core/base/base_controller.dart';

class FCMTokenController extends BaseController<BaseState> {
  FCMTokenController(super.state, super.dio);

  Future<bool> updateFCMToken(String fcmToken) async {
    var response = await postRequest("/api/fcm-tokens", data: {"fcmToken": fcmToken});
    if (response.statusCode == 200) {
      return true;
    }
    return false;
  }
}

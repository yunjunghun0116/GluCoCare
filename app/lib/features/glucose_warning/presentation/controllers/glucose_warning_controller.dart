import 'package:app/features/care/data/models/care_giver_response.dart';
import 'package:app/features/glucose_warning/data/models/glucose_alert_policy_response.dart';

import '../../../../core/base/base_controller.dart';
import '../../../../core/exceptions/custom_exception.dart';
import '../../../../core/exceptions/exception_message.dart';

class GlucoseWarningController extends BaseController<BaseState> {
  GlucoseWarningController(super.state, super.dio);

  Future<GlucoseAlertPolicyResponse> getGlucoseAlertPolicy(CareGiverResponse careGiver) async {
    var response = await getRequest("/api/glucose-alert-policies?careGiverId=${careGiver.id}");
    if (response.statusCode == 200) {
      return GlucoseAlertPolicyResponse.fromJson(response.data);
    }
    throw CustomException(ExceptionMessage.badRequest);
  }

  Future<bool> updateHighRisk(GlucoseAlertPolicyResponse glucoseAlertPolicy, int newValue) async {
    var response = await postRequest(
      "/api/glucose-alert-policies/${glucoseAlertPolicy.id}/high-risk",
      data: {"highRiskValue": newValue},
    );
    if (response.statusCode == 200) {
      return true;
    }
    return false;
  }

  Future<bool> updateVeryHighRisk(GlucoseAlertPolicyResponse glucoseAlertPolicy, int newValue) async {
    var response = await postRequest(
      "/api/glucose-alert-policies/${glucoseAlertPolicy.id}/very-high-risk",
      data: {"veryHighRiskValue": newValue},
    );
    if (response.statusCode == 200) {
      return true;
    }
    return false;
  }
}

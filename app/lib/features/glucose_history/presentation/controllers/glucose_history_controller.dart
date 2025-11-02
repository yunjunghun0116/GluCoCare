import 'package:app/features/glucose_history/data/models/create_glucose_history_request.dart';
import 'package:app/features/glucose_history/data/models/glucose_history_response.dart';

import '../../../../core/base/base_controller.dart';

class GlucoseHistoryController extends BaseController<BaseState> {
  GlucoseHistoryController(super.state, super.dio);

  Future<List<GlucoseHistoryResponse>?> getAllGlucoseHistories(int patientId) async {
    var response = await getRequest("/api/glucose-histories/$patientId");
    if (response.statusCode == 200) {
      var result = (response.data as List).map((data) => GlucoseHistoryResponse.fromJson(data)).toList();
      result.sort((a, b) => a.dateTime.difference(b.dateTime).inMinutes);
      return result;
    }
    return null;
  }

  Future<bool> createGlucoseHistory(CreateGlucoseHistoryRequest request) async {
    var response = await postRequest(
      "/api/glucose-histories",
      data: {"dateTime": request.dateTime.toUtc().toIso8601String(), "value": request.sgv},
    );
    if (response.statusCode == 200) {
      return true;
    }
    return false;
  }
}

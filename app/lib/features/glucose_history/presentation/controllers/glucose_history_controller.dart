import 'package:app/features/glucose_history/data/models/glucose_history_response.dart';

import '../../../../core/base/base_controller.dart';

class GlucoseHistoryController extends BaseController<BaseState> {
  GlucoseHistoryController(super.state, super.dio);

  Future<List<GlucoseHistoryResponse>?> getAllGlucoseHistories(int patientId) async {
    var response = await getRequest("/api/patients/$patientId/glucose-histories");
    if (response.statusCode == 200) {
      var result = (response.data as List).map((data) => GlucoseHistoryResponse.fromJson(data)).toList();
      result.sort((a, b) => a.dateTime.difference(b.dateTime).inMinutes);
      return result;
    }
    return null;
  }
}

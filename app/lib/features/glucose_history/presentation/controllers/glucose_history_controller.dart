import 'package:app/core/exceptions/custom_exception.dart';
import 'package:app/core/exceptions/exception_message.dart';
import 'package:app/features/care_giver/data/models/care_giver_response_dto.dart';
import 'package:app/features/care_giver/data/models/create_care_giver_dto.dart';
import 'package:app/features/glucose_history/data/models/glucose_history_response_dto.dart';

import '../../../../core/base/base_controller.dart';

class GlucoseHistoryController extends BaseController<BaseState> {
  GlucoseHistoryController(super.state, super.dio);

  Future<List<GlucoseHistoryResponseDto>?> getAllGlucoseHistories(int patientId) async {
    var response = await getRequest("/api/patients/$patientId/glucose-histories");
    if (response.statusCode == 200) {
      return (response.data as List).map((data) => GlucoseHistoryResponseDto.fromJson(data)).toList();
    }
    return null;
  }
}

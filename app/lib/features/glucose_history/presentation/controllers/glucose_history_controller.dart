import 'package:app/features/glucose_history/data/models/create_glucose_history_request.dart';
import 'package:app/features/glucose_history/data/models/glucose_history_response.dart';
import 'package:app/features/glucose_history/data/models/predict_glucose_response.dart';

import '../../../../core/base/base_controller.dart';

class GlucoseHistoryController extends BaseController<BaseState> {
  GlucoseHistoryController(super.state, super.dio);

  Future<List<GlucoseHistoryResponse>?> getAllGlucoseHistories(int careRelationId) async {
    var response = await getRequest("/api/glucose-histories?careRelationId=$careRelationId");
    if (response.statusCode == 200) {
      var result = (response.data as List).map((data) => GlucoseHistoryResponse.fromJson(data)).toList();
      result.sort((a, b) => a.dateTime.difference(b.dateTime).inMinutes);
      return result;
    }
    return null;
  }

  Future<List<PredictGlucoseResponse>?> getPredictGlucose(int careRelationId) async {
    var response = await getRequest("/api/glucose-histories/predict?careRelationId=$careRelationId");
    if (response.statusCode == 200) {
      var result = (response.data as List).map((data) => PredictGlucoseResponse.fromJson(data)).toList();
      result.sort((a, b) => a.dateTime.difference(b.dateTime).inMinutes);
      return result;
    }
    return null;
  }

  Future<List<PredictGlucoseResponse>?> getPredictGlucoseWithExercise(int careRelationId) async {
    var response = await getRequest(
      "/api/glucose-histories/predict/exercise?careRelationId=$careRelationId&duration=30",
    );
    if (response.statusCode == 200) {
      var result = (response.data as List).map((data) => PredictGlucoseResponse.fromJson(data)).toList();
      result.sort((a, b) => a.dateTime.difference(b.dateTime).inMinutes);
      return result;
    }
    return null;
  }

  Future<bool> createGlucoseHistory(CreateGlucoseHistoryRequest request) async {
    var response = await postRequest(
      "/api/glucose-histories",
      data: {
        "careRelationId": request.careRelationId,
        "dateTime": request.dateTime.toIso8601String(),
        "sgv": request.sgv,
      },
    );
    if (response.statusCode == 200) {
      return true;
    }
    return false;
  }
}

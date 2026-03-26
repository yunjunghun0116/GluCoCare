import 'package:app/features/point/data/models/point_history_response.dart';
import 'package:app/features/point/data/models/point_response.dart';

import '../../../../core/base/base_controller.dart';

class PointController extends BaseController<BaseState> {
  PointController(super.state, super.dio);

  Future<PointResponse?> readPoint() async {
    var response = await getRequest("/api/points");
    if (response.statusCode == 200) {
      var pointResponse = PointResponse.fromJson(response.data);
      return pointResponse;
    }
    return null;
  }

  Future<List<PointHistoryResponse>?> readHistories() async {
    var response = await getRequest("/api/points/history");
    if (response.statusCode == 200) {
      var result = (response.data as List).map((data) => PointHistoryResponse.fromJson(data)).toList();
      return result;
    }
    return null;
  }
}

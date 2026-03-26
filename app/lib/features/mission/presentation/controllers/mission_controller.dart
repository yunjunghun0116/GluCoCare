import 'package:app/features/mission/data/models/daily_mission_response.dart';

import '../../../../core/base/base_controller.dart';

class MissionController extends BaseController<BaseState> {
  MissionController(super.state, super.dio);

  Future<List<DailyMissionResponse>?> readDailyMissions() async {
    var response = await getRequest("/api/daily-missions");
    if (response.statusCode == 200) {
      var result = (response.data as List).map((data) => DailyMissionResponse.fromJson(data)).toList();
      return result;
    }
    return null;
  }

  Future<bool> completeDailyMission(int id) async {
    var response = await postRequest("/api/daily-missions/$id");
    if (response.statusCode == 200) {
      return true;
    }
    return false;
  }
}

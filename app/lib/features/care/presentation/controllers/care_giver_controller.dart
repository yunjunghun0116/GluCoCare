import 'package:app/core/exceptions/custom_exception.dart';
import 'package:app/core/exceptions/exception_message.dart';

import '../../../../core/base/base_controller.dart';
import '../../data/models/care_giver_response.dart';
import '../../data/models/create_care_giver_request.dart';

class CareGiverController extends BaseController<BaseState> {
  CareGiverController(super.state, super.dio);

  Future<CareGiverResponse?> createCareGiver(CreateCareGiverRequest createCareGiverDto) async {
    var response = await postRequest(
      "/api/care-givers",
      data: {"name": createCareGiverDto.patientName, "patientId": createCareGiverDto.patientId},
    );
    if (response.statusCode == 200) {
      return CareGiverResponse.fromJson(response.data);
    }
    return null;
  }

  Future<CareGiverResponse?> getCareGiver(int id) async {
    var response = await getRequest("/api/care-givers/$id");
    if (response.statusCode == 200) {
      return CareGiverResponse.fromJson(response.data);
    }
    return null;
  }

  Future<List<CareGiverResponse>?> getAllCareGiver() async {
    var response = await getRequest("/api/care-givers");
    if (response.statusCode == 200) {
      return (response.data as List).map((data) => CareGiverResponse.fromJson(data)).toList();
    }
    return null;
  }

  Future<void> deleteCareGiver(int id) async {
    var response = await deleteRequest("/api/care-givers/$id");
    if (response.statusCode == 200) {
      return;
    }
    throw CustomException(ExceptionMessage.badRequest);
  }
}

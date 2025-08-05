import 'package:app/core/exceptions/custom_exception.dart';
import 'package:app/core/exceptions/exception_message.dart';
import 'package:app/features/care_giver/data/models/care_giver_response_dto.dart';
import 'package:app/features/care_giver/data/models/create_care_giver_dto.dart';

import '../../../../core/base/base_controller.dart';

class CareGiverController extends BaseController<BaseState> {
  CareGiverController(super.state, super.dio);

  Future<CareGiverResponseDto?> createCareGiver(CreateCareGiverDto createCareGiverDto) async {
    var response = await postRequest(
      "/api/care-givers",
      data: {"name": createCareGiverDto.patientName, "patientId": createCareGiverDto.patientId},
    );
    if (response.statusCode == 200) {
      return CareGiverResponseDto.fromJson(response.data);
    }
    return null;
  }

  Future<CareGiverResponseDto?> getCareGiver(int id) async {
    var response = await getRequest("/api/care-givers/$id");
    if (response.statusCode == 200) {
      return CareGiverResponseDto.fromJson(response.data);
    }
    return null;
  }

  Future<List<CareGiverResponseDto>?> getAllCareGiver() async {
    var response = await getRequest("/api/care-givers");
    if (response.statusCode == 200) {
      return (response.data as List).map((data) => CareGiverResponseDto.fromJson(data)).toList();
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

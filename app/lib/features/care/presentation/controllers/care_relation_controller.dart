import 'package:app/core/exceptions/custom_exception.dart';
import 'package:app/core/exceptions/exception_message.dart';

import '../../../../core/base/base_controller.dart';
import '../../data/models/care_relation_response.dart';
import '../../data/models/create_care_relation_request.dart';

class CareRelationController extends BaseController<BaseState> {
  CareRelationController(super.state, super.dio);

  Future<CareRelationResponse?> createCareRelation(CreateCareRelationRequest createCareRelationDto) async {
    var response = await postRequest(
      "/api/care-relations",
      data: {"relationType": "CAREGIVER", "patientId": createCareRelationDto.patientId},
    );
    if (response.statusCode == 200) {
      return CareRelationResponse.fromJson(response.data);
    }
    return null;
  }

  Future<CareRelationResponse?> getCareRelation(int id) async {
    var response = await getRequest("/api/care-relations/$id");
    if (response.statusCode == 200) {
      return CareRelationResponse.fromJson(response.data);
    }
    return null;
  }

  Future<List<CareRelationResponse>?> getAllCareRelations() async {
    var response = await getRequest("/api/care-relations");
    if (response.statusCode == 200) {
      return (response.data as List).map((data) => CareRelationResponse.fromJson(data)).toList();
    }
    return null;
  }

  Future<void> deleteCareRelation(int id) async {
    var response = await deleteRequest("/api/care-relations/$id");
    if (response.statusCode == 200) {
      return;
    }
    throw CustomException(ExceptionMessage.badRequest);
  }
}

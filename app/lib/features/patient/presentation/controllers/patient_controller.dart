import '../../../../core/base/base_controller.dart';
import '../../../../core/exceptions/custom_exception.dart';
import '../../../../core/exceptions/exception_message.dart';

class MemberController extends BaseController<BaseState> {
  MemberController(super.state, super.dio);

  Future<String?> readName() async {
    var response = await getRequest("/api/members/name");
    if (response.statusCode == 200) {
      return response.data;
    }
    return null;
  }

  Future<String?> readPatientInformation() async {
    var response = await getRequest("/api/members/patient");
    if (response.statusCode == 200) {
      return response.data;
    }
    return null;
  }

  Future<bool> readIsPatient() async {
    var response = await getRequest("/api/members/is-patient");
    if (response.statusCode == 200) {
      return response.data;
    }
    return false;
  }

  Future<bool> updateName(String newName) async {
    var response = await postRequest("/api/members/update-name", data: {"name": newName});
    if (response.statusCode == 200) {
      return true;
    }
    throw CustomException(ExceptionMessage.badRequest);
  }

  Future<bool> updateToPatient() async {
    var response = await postRequest("/api/members/update-to-patient");
    if (response.statusCode == 200) {
      return true;
    }
    throw CustomException(ExceptionMessage.badRequest);
  }

  Future<bool> delete() async {
    var response = await deleteRequest("/api/members");
    if (response.statusCode == 200) {
      return true;
    }
    throw CustomException(ExceptionMessage.badRequest);
  }
}

import 'package:app/features/patient/data/models/read_patient_response.dart';

import '../../../../core/base/base_controller.dart';
import '../../../../core/exceptions/custom_exception.dart';
import '../../../../core/exceptions/exception_message.dart';

class PatientController extends BaseController<BaseState> {
  PatientController(super.state, super.dio);

  Future<ReadPatientResponse?> readPatientInformation() async {
    var response = await getRequest("/api/patients");
    if (response.statusCode == 200) {
      return ReadPatientResponse.fromJson(response.data);
    }
    return null;
  }

  Future<bool> readIsPatient() async {
    var response = await getRequest("/api/patients/is-patient");
    if (response.statusCode == 200) {
      return response.data;
    }
    return false;
  }

  Future<bool> updateToPatient() async {
    var response = await postRequest("/api/patients");
    if (response.statusCode == 200) {
      return true;
    }
    throw CustomException(ExceptionMessage.badRequest);
  }
}

import 'package:app/features/patient/data/models/create_patient_response.dart';

import '../../../../core/base/base_controller.dart';
import '../../../../core/exceptions/custom_exception.dart';
import '../../../../core/exceptions/exception_message.dart';
import '../../data/models/read_patient_response.dart';

class PatientController extends BaseController<BaseState> {
  PatientController(super.state, super.dio);

  Future<bool> existsPatients() async {
    var response = await getRequest("/api/patients/exists");
    if (response.statusCode == 200) {
      return response.data;
    }
    return false;
  }

  Future<CreatePatientResponse> createPatient(String patientName) async {
    var response = await postRequest("/api/patients", data: {"name": patientName});
    if (response.statusCode == 200) {
      var patientResponse = CreatePatientResponse.fromJson(response.data);
      return patientResponse;
    }
    throw CustomException(ExceptionMessage.badRequest);
  }

  Future<ReadPatientResponse> readPatient() async {
    var response = await getRequest("/api/patients");
    if (response.statusCode == 200) {
      var patientResponse = ReadPatientResponse.fromJson(response.data);
      return patientResponse;
    }
    throw CustomException(ExceptionMessage.badRequest);
  }
}

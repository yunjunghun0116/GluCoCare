import 'package:app/core/base/base_controller.dart';
import 'package:app/core/providers.dart';
import 'package:app/features/patient/presentation/controllers/patient_controller.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

// --- Controller Providers ---
final patientControllerProvider = StateNotifierProvider<PatientController, BaseState>((ref) {
  var dio = ref.read(dioProvider);
  return PatientController(BaseState(), dio);
});

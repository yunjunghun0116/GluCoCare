import 'package:app/core/base/base_controller.dart';
import 'package:app/core/providers.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

import 'controllers/patient_controller.dart';

// --- Controller Providers ---
final patientController = StateNotifierProvider<PatientController, BaseState>((ref) {
  var dio = ref.read(dioProvider);
  return PatientController(BaseState(), dio);
});

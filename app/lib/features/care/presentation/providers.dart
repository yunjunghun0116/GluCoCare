import 'package:app/core/base/base_controller.dart';
import 'package:app/core/providers.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

import 'controllers/care_giver_controller.dart';

// --- Controller Providers ---
final careGiverControllerProvider = StateNotifierProvider<CareGiverController, BaseState>((ref) {
  var dio = ref.read(dioProvider);
  return CareGiverController(BaseState(), dio);
});

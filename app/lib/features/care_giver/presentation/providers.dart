import 'package:app/core/base/base_controller.dart';
import 'package:app/core/providers.dart';
import 'package:app/features/care_giver/presentation/controllers/care_giver_controller.dart';
import 'package:app/features/member/presentation/controllers/member_controller.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

// --- Controller Providers ---
final careGiverControllerProvider = StateNotifierProvider<CareGiverController, BaseState>((ref) {
  var dio = ref.read(dioProvider);
  return CareGiverController(BaseState(), dio);
});

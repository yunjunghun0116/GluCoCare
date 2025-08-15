import 'package:app/core/base/base_controller.dart';
import 'package:app/core/providers.dart';
import 'package:app/features/glucose_warning/presentation/controllers/glucose_warning_controller.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

// --- Controller Providers ---
final glucoseWarningControllerProvider = StateNotifierProvider<GlucoseWarningController, BaseState>((ref) {
  var dio = ref.read(dioProvider);
  return GlucoseWarningController(BaseState(), dio);
});

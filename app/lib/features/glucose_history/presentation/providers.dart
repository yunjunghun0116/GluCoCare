import 'package:app/core/base/base_controller.dart';
import 'package:app/core/providers.dart';
import 'package:app/features/glucose_history/presentation/controllers/glucose_history_controller.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';

// --- Controller Providers ---
final glucoseHistoryControllerProvider = StateNotifierProvider<GlucoseHistoryController, BaseState>((ref) {
  var dio = ref.read(dioProvider);
  return GlucoseHistoryController(BaseState(), dio);
});
